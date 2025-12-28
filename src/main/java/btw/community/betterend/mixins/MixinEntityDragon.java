package btw.community.betterend.mixins;

import btw.community.betterend.BetterEndAddon;
import btw.community.betterend.BetterEndBlocks;
import btw.community.betterend.dragon.DragonPhase;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.List;
import java.util.Random;

@Mixin(EntityDragon.class)
public abstract class MixinEntityDragon extends EntityLiving {
    @Shadow public double[][] ringBuffer;
    @Shadow public int ringBufferIndex;
    @Shadow public float animTime;
    @Shadow public float prevAnimTime;
    @Shadow public EntityDragonPart dragonPartHead;
    @Shadow public EntityDragonPart dragonPartBody;
    @Shadow public EntityDragonPart dragonPartWing1;
    @Shadow public EntityDragonPart dragonPartWing2;
    @Shadow public EntityDragonPart dragonPartTail1;
    @Shadow public EntityDragonPart dragonPartTail2;
    @Shadow public EntityDragonPart dragonPartTail3;
    @Shadow protected abstract void updateDragonEnderCrystal();
    @Shadow protected abstract void collideWithEntities(List par1List);
    @Shadow protected abstract void attackEntitiesInList(List par1List);
    @Shadow protected abstract float simplifyAngle(double par1);
    @Shadow protected abstract boolean destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB);
    @Shadow public abstract double[] getMovementOffsets(int par1, float par2);
    @Shadow public double targetX;
    @Shadow public double targetY;
    @Shadow public double targetZ;
    @Shadow public boolean forceNewTarget;
    @Shadow private Entity target;

    @Unique private DragonPhase be_currentPhase = DragonPhase.COOLDOWN;
    @Unique private int be_phaseTimer = 200;
    @Unique private EntityPlayer be_targetPlayer = null;

    public MixinEntityDragon(World par1World) {
        super(par1World);
    }

    @ModifyConstant(method = "attackEntitiesInList", constant = @Constant(floatValue = 10.0F))
    private float modifyDragonAttackDamage(float original) { return (float) BetterEndAddon.dragonAttackDamage; }

    @ModifyConstant(method = "applyEntityAttributes", constant = @Constant(doubleValue = 200.0D))
    private double modifyMaxHealth(double original) { return (double) BetterEndAddon.dragonMaxHealth; }

    @ModifyConstant(method = "collideWithEntities", constant = @Constant(doubleValue = 4.0D))
    private double modifyKnockbackStrength(double original) { return (double) BetterEndAddon.dragonKnockback; }

    @ModifyConstant(method = "updateDragonEnderCrystal", constant = @Constant(floatValue = 1.0F))
    private float modifyCrystalRegen(float original) { return (float) BetterEndAddon.dragonCrystalRegenAmount; }

    @ModifyConstant(method = "updateDragonEnderCrystal", constant = @Constant(floatValue = 10.0F))
    private float modifyCrystalExplosionDamage(float original) { return (float) BetterEndAddon.dragonCrystalExplosionDamage; }

    @ModifyConstant(method = "onDeathUpdate", constant = @Constant(intValue = 1000))
    private int modifyPeriodicXP(int original) { return BetterEndAddon.dragonXPPeriodic; }

    @ModifyConstant(method = "onDeathUpdate", constant = @Constant(intValue = 2000))
    private int modifyFinalXP(int original) { return BetterEndAddon.dragonXPFinal; }

    @Inject(method = "entityInit", at = @At("TAIL"))
    private void be_entityInit(CallbackInfo ci) {
        this.dataWatcher.addObject(20, (byte)0);
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true)
    public void onLivingUpdateHead(CallbackInfo ci) {
        if (this.getHealth() <= 0) return;

        if (this.worldObj.isRemote) {
            byte state = this.dataWatcher.getWatchableObjectByte(20);
            if (state == 1) {
                be_currentPhase = DragonPhase.BREATHING;
            } else {
                if (be_currentPhase == DragonPhase.BREATHING) {
                    be_currentPhase = DragonPhase.COOLDOWN;
                }
            }
        }

        if (be_currentPhase == DragonPhase.BREATHING) {
            be_handleBreathingState();
            be_updateDragonBodyParts();
            ci.cancel();
        } else {
            if (!this.worldObj.isRemote) {
                switch (be_currentPhase) {
                    case COOLDOWN:
                        be_handleCooldownState();
                        break;
                    case SELECTING:
                        be_handleSelectionState();
                        break;
                    case CHARGING:
                        be_handleChargingState();
                        break;
                    case RECOVERY:
                        be_handleRecoveryState();
                        break;
                }
            }
        }
    }

    @Unique
    private void be_handleBreathingState() {
        if (be_targetPlayer == null || be_targetPlayer.isDead) {
            be_enterRecoveryState();
            return;
        }

        be_phaseTimer--;
        if (be_phaseTimer <= 0) {
            be_enterRecoveryState();
            return;
        }

        double dx = be_targetPlayer.posX - this.posX;
        double dz = be_targetPlayer.posZ - this.posZ;

        double angleToPlayer = Math.atan2(dz, dx) * 180.0D / Math.PI;
        float targetYaw = (float)(angleToPlayer - 90.0D + 180.0D);

        float yawDiff = MathHelper.wrapAngleTo180_float(targetYaw - this.rotationYaw);
        this.rotationYaw += yawDiff * 0.15F;
        this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);

        double hoverY = Math.sin(this.ticksExisted * 0.15D) * 0.1D;
        this.motionX = 0;
        this.motionZ = 0;
        this.motionY = hoverY;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (be_phaseTimer == 20) {
            be_spawnEndlessFire();
            this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 10.0F, 0.5F);
        }
    }

    @Unique
    private void be_updateDragonBodyParts() {
        // 1. Regeneracja z kryształów
        this.updateDragonEnderCrystal();

        // 2. Aktualizacja animacji skrzydeł (SPOWOLNIENIE)
        this.prevAnimTime = this.animTime;

        // Zmieniono z 0.2F na 0.1F. Im mniejsza liczba, tym wolniej macha.
        float flapSpeed = 0.1F;

        this.animTime += flapSpeed;

        // Dźwięk skrzydeł
        if (this.worldObj.isRemote) {
            float cycle = MathHelper.cos(this.animTime * (float)Math.PI * 2.0F);
            float prevCycle = MathHelper.cos(this.prevAnimTime * (float)Math.PI * 2.0F);
            if (prevCycle <= -0.3F && cycle >= -0.3F) {
                // Zmieniono pitch (0.8 -> 0.7) i głośność, żeby dźwięk pasował do wolniejszego machania
                this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0F, 0.7F + this.rand.nextFloat() * 0.3F, false);
            }
        }

        // --- Reszta metody bez zmian ---

        // 3. Aktualizacja RingBuffer
        if (this.ringBufferIndex < 0) {
            for(int i = 0; i < this.ringBuffer.length; ++i) {
                this.ringBuffer[i][0] = (double)this.rotationYaw;
                this.ringBuffer[i][1] = this.posY;
            }
        }
        if (++this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
        }
        this.ringBuffer[this.ringBufferIndex][0] = (double)this.rotationYaw;
        this.ringBuffer[this.ringBufferIndex][1] = this.posY;

        // 4. Ustawienie renderYawOffset
        this.renderYawOffset = this.rotationYaw;

        // 5. Konfiguracja rozmiarów (Vanilla)
        this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
        this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
        this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
        this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
        this.dragonPartBody.height = 3.0F;
        this.dragonPartBody.width = 5.0F;
        this.dragonPartWing1.height = 2.0F;
        this.dragonPartWing1.width = 4.0F;
        this.dragonPartWing2.height = 3.0F;
        this.dragonPartWing2.width = 4.0F;

        // 6. MATEMATYKA POZYCJI CZĘŚCI CIAŁA
        float f1 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float)Math.PI;
        float f2 = MathHelper.cos(f1);
        float f3 = -MathHelper.sin(f1);
        float f4 = this.rotationYaw * (float)Math.PI / 180.0F;
        float f5 = MathHelper.sin(f4);
        float f6 = MathHelper.cos(f4);

        this.dragonPartBody.onUpdate();
        this.dragonPartBody.setLocationAndAngles(this.posX + (double)(f5 * 0.5F), this.posY, this.posZ - (double)(f6 * 0.5F), 0.0F, 0.0F);

        this.dragonPartWing1.onUpdate();
        this.dragonPartWing1.setLocationAndAngles(this.posX + (double)(f6 * 4.5F), this.posY + 2.0D, this.posZ + (double)(f5 * 4.5F), 0.0F, 0.0F);
        this.dragonPartWing2.onUpdate();
        this.dragonPartWing2.setLocationAndAngles(this.posX - (double)(f6 * 4.5F), this.posY + 2.0D, this.posZ - (double)(f5 * 4.5F), 0.0F, 0.0F);

        if (!this.worldObj.isRemote && this.hurtTime == 0) {
            this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
            this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
            this.attackEntitiesInList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.boundingBox.expand(1.0D, 1.0D, 1.0D)));
        }

        double[] adouble = this.getMovementOffsets(5, 1.0F);
        double[] adouble1 = this.getMovementOffsets(0, 1.0F);
        float f7 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
        float f8 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F - this.randomYawVelocity * 0.01F);

        this.dragonPartHead.onUpdate();
        this.dragonPartHead.setLocationAndAngles(this.posX + (double)(f7 * 5.5F * f2), this.posY + (adouble1[1] - adouble[1]) * 1.0D + (double)(f3 * 5.5F), this.posZ - (double)(f8 * 5.5F * f2), 0.0F, 0.0F);

        for(int k = 0; k < 3; ++k) {
            EntityDragonPart entitydragonpart = null;
            if (k == 0) entitydragonpart = this.dragonPartTail1;
            if (k == 1) entitydragonpart = this.dragonPartTail2;
            if (k == 2) entitydragonpart = this.dragonPartTail3;

            double[] adouble2 = this.getMovementOffsets(12 + k * 2, 1.0F);
            float f9 = this.rotationYaw * (float)Math.PI / 180.0F + this.simplifyAngle(adouble2[0] - adouble[0]) * (float)Math.PI / 180.0F * 1.0F;
            float f10 = MathHelper.sin(f9);
            float f11 = MathHelper.cos(f9);
            float f12 = 1.5F;
            float f13 = (float)(k + 1) * 2.0F;

            entitydragonpart.onUpdate();
            entitydragonpart.setLocationAndAngles(this.posX - (double)((f5 * f12 + f10 * f13) * f2), this.posY + (adouble2[1] - adouble[1]) * 1.0D - (double)((f13 + f12) * f3) + 1.5D, this.posZ + (double)((f6 * f12 + f11 * f13) * f2), 0.0F, 0.0F);
        }

        if (!this.worldObj.isRemote) {
            this.destroyBlocksInAABB(this.dragonPartHead.boundingBox);
            this.destroyBlocksInAABB(this.dragonPartBody.boundingBox);
        }
    }
    @Unique
    private void be_handleCooldownState() {
        if (be_phaseTimer > 0) {
            be_phaseTimer--;
        } else {
            be_currentPhase = DragonPhase.SELECTING;
        }

        if (this.target instanceof EntityPlayer) {
            this.target = null;
            this.forceNewTarget = true;
        }
    }

    @Unique
    private void be_handleSelectionState() {
        EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, 150.0D);

        if (player == null || player.capabilities.isCreativeMode) {
            be_phaseTimer = 20;
            be_currentPhase = DragonPhase.COOLDOWN;
            return;
        }

        this.be_targetPlayer = player;

        if (this.rand.nextBoolean()) {
            be_startBreathingAttack();
        } else {
            be_startChargingAttack();
        }
    }

    // --- Attack 1: Endless Breath ---

    @Unique
    private void be_startBreathingAttack() {
        be_currentPhase = DragonPhase.BREATHING;
        be_phaseTimer = BetterEndAddon.dragonEndlessBreathDuration + 20;

        this.dataWatcher.updateObject(20, (byte)1);

        this.worldObj.playSoundAtEntity(this, "mob.enderdragon.growl", 10.0F, 0.5F);
        this.target = null;
        this.forceNewTarget = false;
    }

    @Unique
    private void be_spawnEndlessFire() {
        if (be_targetPlayer == null) return;

        int radius = BetterEndAddon.dragonEndlessBreathRadius;
        int attempts = radius * 5;

        for (int i = 0; i < attempts; i++) {
            double offsetX = (this.rand.nextDouble() - 0.5D) * 2.0D * radius;
            double offsetZ = (this.rand.nextDouble() - 0.5D) * 2.0D * radius;

            int x = MathHelper.floor_double(be_targetPlayer.posX + offsetX);
            int y = MathHelper.floor_double(be_targetPlayer.posY);
            int z = MathHelper.floor_double(be_targetPlayer.posZ + offsetZ);

            for (int k = -3; k <= 3; k++) {
                int currentY = y + k;
                if (this.worldObj.isAirBlock(x, currentY, z) &&
                        this.worldObj.doesBlockHaveSolidTopSurface(x, currentY - 1, z)) {

                    this.worldObj.setBlock(x, currentY, z, BetterEndBlocks.endlessFire.blockID);
                    this.worldObj.playAuxSFX(2004, x, currentY, z, 0);
                    break;
                }
            }
        }
    }

    // --- Attack 2: Charge ---

    @Unique
    private void be_startChargingAttack() {
        be_currentPhase = DragonPhase.CHARGING;
        be_phaseTimer = 100;
        this.target = be_targetPlayer;
        this.forceNewTarget = false;
    }

    @Unique
    private void be_handleChargingState() {
        be_phaseTimer--;
        double distSq = this.getDistanceSqToEntity(be_targetPlayer);
        if (be_phaseTimer <= 0 || be_targetPlayer.isDead || distSq < 150.0D) {
            be_enterRecoveryState();
        }
    }

    // --- Recovery ---

    @Unique
    private void be_enterRecoveryState() {
        be_currentPhase = DragonPhase.RECOVERY;
        be_targetPlayer = null;

        this.dataWatcher.updateObject(20, (byte)0);

        this.target = null;
        this.forceNewTarget = false;
        this.targetX = 0.0D;
        this.targetY = 90.0D;
        this.targetZ = 0.0D;
    }

    @Unique
    private void be_handleRecoveryState() {
        double distToCenter = this.getDistance(0.0D, this.posY, 0.0D);

        if (distToCenter < 30.0D || this.posY > 80.0D) {
            be_currentPhase = DragonPhase.COOLDOWN;
            be_phaseTimer = BetterEndAddon.dragonAttackCooldown * 20;
            this.forceNewTarget = true;
        } else {
            this.targetX = 0.0D;
            this.targetY = 90.0D;
            this.targetZ = 0.0D;
        }
    }
}