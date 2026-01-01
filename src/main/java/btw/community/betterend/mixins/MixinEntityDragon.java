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
    @Shadow public float animTime;
    @Shadow public float prevAnimTime;
    @Shadow public EntityDragonPart dragonPartHead;
    @Shadow public EntityDragonPart dragonPartBody;
    @Shadow public EntityDragonPart dragonPartWing1;
    @Shadow public EntityDragonPart dragonPartWing2;
    @Shadow public EntityDragonPart dragonPartTail1;
    @Shadow public EntityDragonPart dragonPartTail2;
    @Shadow public EntityDragonPart dragonPartTail3;
    @Shadow public double targetX;
    @Shadow public double targetY;
    @Shadow public double targetZ;
    @Shadow public boolean forceNewTarget;
    @Shadow private Entity target;
    @Shadow public double[][] ringBuffer;
    @Shadow public int ringBufferIndex;
    @Shadow protected abstract void updateDragonEnderCrystal();
    @Shadow protected abstract void collideWithEntities(List par1List);
    @Shadow protected abstract void attackEntitiesInList(List par1List);
    @Shadow protected abstract float simplifyAngle(double par1);
    @Shadow protected abstract boolean destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB);
    @Shadow public abstract double[] getMovementOffsets(int par1, float par2);

    @Unique private DragonPhase be_currentPhase = DragonPhase.COOLDOWN;
    @Unique private int be_phaseTimer = 200;
    @Unique private EntityPlayer be_targetPlayer = null;
    @Unique private static final int BE_PHASE_WATCHER_ID = 24;

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
        this.dataWatcher.addObject(BE_PHASE_WATCHER_ID, (byte)0);
    }

    // --- Main Logic ---
    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true)
    public void be_onLivingUpdate(CallbackInfo ci) {
        if (this.getHealth() <= 0) return;

        boolean isClient = this.worldObj.isRemote;
        byte dwValue = this.dataWatcher.getWatchableObjectByte(BE_PHASE_WATCHER_ID);

        if (isClient) {
            if (dwValue == 1) be_currentPhase = DragonPhase.BREATHING;
            else if (dwValue == 2) be_currentPhase = DragonPhase.DRAGON_BREATHING;
            else be_currentPhase = DragonPhase.COOLDOWN;
        }

        if (be_currentPhase == DragonPhase.BREATHING || dwValue == 1) {
            if (be_currentPhase != DragonPhase.BREATHING) be_currentPhase = DragonPhase.BREATHING;
            be_handleBreathingState();
            ci.cancel();
            return;
        }

        if (be_currentPhase == DragonPhase.DRAGON_BREATHING || dwValue == 2) {
            if (be_currentPhase != DragonPhase.DRAGON_BREATHING) be_currentPhase = DragonPhase.DRAGON_BREATHING;
            be_handleDragonBreathingState();
            ci.cancel();
            return;
        }

        if (!isClient) {
            switch (be_currentPhase) {
                case COOLDOWN: be_handleCooldownState(); break;
                case SELECTING: be_handleSelectionState(); break;
                case CHARGING: be_handleChargingState(); break;
                case RECOVERY: be_handleRecoveryState(); break;
            }
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

        int choice = this.rand.nextInt(3);

        if (choice == 0) {
            be_startChargingAttack();
        } else if (choice == 1) {
            be_startBreathingAttack(); // Endless Breath
        } else {
            be_startDragonBreathingAttack(); // Dragon's Breath
        }
    }

    @Unique
    private void be_handleChargingState() {
        be_phaseTimer--;
        if (be_targetPlayer == null || be_targetPlayer.isDead || be_phaseTimer <= 0 || this.getDistanceSqToEntity(be_targetPlayer) < 225.0D) {
            be_enterRecoveryState();
        }
    }

    @Unique
    private void be_handleRecoveryState() {
        double distToCenterSq = this.posX * this.posX + this.posZ * this.posZ;
        if (distToCenterSq < 900.0D || this.posY > 80.0D) {
            be_currentPhase = DragonPhase.COOLDOWN;
            be_phaseTimer = BetterEndAddon.dragonAttackCooldown * 20;
            this.forceNewTarget = true;
        } else {
            this.targetX = 0.0D;
            this.targetY = 90.0D;
            this.targetZ = 0.0D;
        }
    }

    // --- Attack Logic ---

    @Unique
    private void be_startChargingAttack() {
        be_currentPhase = DragonPhase.CHARGING;
        be_phaseTimer = 100;
        this.target = be_targetPlayer;
        this.forceNewTarget = false;
        this.dataWatcher.updateObject(BE_PHASE_WATCHER_ID, (byte)0);
    }

    @Unique
    private void be_startBreathingAttack() {
        be_currentPhase = DragonPhase.BREATHING;
        be_phaseTimer = BetterEndAddon.dragonEndlessBreathDuration + 20;
        this.dataWatcher.updateObject(BE_PHASE_WATCHER_ID, (byte)1); // Sync: Endless Breath
        this.worldObj.playSoundAtEntity(this, "mob.enderdragon.growl", 10.0F, 0.5F);
        this.target = null;
        this.forceNewTarget = false;
    }

    @Unique
    private void be_startDragonBreathingAttack() {
        be_currentPhase = DragonPhase.DRAGON_BREATHING;
        be_phaseTimer = BetterEndAddon.dragonBreathDuration + 20;
        this.dataWatcher.updateObject(BE_PHASE_WATCHER_ID, (byte)2); // Sync: Dragon Breath
        this.worldObj.playSoundAtEntity(this, "mob.enderdragon.growl", 10.0F, 0.5F);
        this.target = null;
        this.forceNewTarget = false;
    }

    @Unique
    private void be_enterRecoveryState() {
        be_currentPhase = DragonPhase.RECOVERY;
        be_targetPlayer = null;
        this.dataWatcher.updateObject(BE_PHASE_WATCHER_ID, (byte)0);
        this.target = null;
        this.forceNewTarget = false;
        this.targetX = 0.0D;
        this.targetY = 90.0D;
        this.targetZ = 0.0D;
    }

    // 1. Endless Breath
    @Unique
    private void be_handleBreathingState() {
        be_commonBreathingAnimation();

        // Spawn Endless Fire
        if (!this.worldObj.isRemote && be_phaseTimer == 20) {
            be_spawnEndlessFire();
            this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 10.0F, 0.5F);
        }
    }

    // 2. Dragon's Breath
    @Unique
    private void be_handleDragonBreathingState() {
        be_commonBreathingAnimation();

        if (!this.worldObj.isRemote && be_phaseTimer == 20) {
            be_spawnDragonFire();
            this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 10.0F, 0.5F);
        }
    }

    @Unique
    private void be_commonBreathingAnimation() {
        this.prevAnimTime = this.animTime;
        this.animTime += 0.02F;
        this.updateDragonEnderCrystal();

        if (!this.worldObj.isRemote) {
            if (be_targetPlayer == null || be_targetPlayer.isDead || --be_phaseTimer <= 0) {
                be_enterRecoveryState();
                return;
            }
        }

        Entity target = be_targetPlayer;
        if (target == null && this.worldObj.isRemote) {
            target = this.worldObj.getClosestPlayerToEntity(this, 150.0D);
        }
        if (target != null) {
            double dx = target.posX - this.posX;
            double dz = target.posZ - this.posZ;
            double angleRad = Math.atan2(dx, dz);
            float targetYaw = (float)(180.0D - angleRad * 180.0D / Math.PI);
            this.rotationYaw = this.simplifyAngle(targetYaw - this.rotationYaw) * 0.1F + this.rotationYaw;
        }

        this.motionX = 0.0D;
        this.motionY = Math.sin(this.ticksExisted * 0.1D) * 0.05D;
        this.motionZ = 0.0D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.worldObj.isRemote) {
            float cycle = MathHelper.cos(this.animTime * (float)Math.PI * 2.0F);
            float prevCycle = MathHelper.cos(this.prevAnimTime * (float)Math.PI * 2.0F);
            if (prevCycle <= -0.3F && cycle >= -0.3F) {
                this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
            }
        }

        be_updateBodyParts();
    }

    @Unique
    private void be_spawnEndlessFire() {
        if (be_targetPlayer == null) return;
        int radius = BetterEndAddon.dragonEndlessBreathRadius;
        spawnFireLogic(radius, BetterEndBlocks.endlessFire.blockID);
    }

    @Unique
    private void be_spawnDragonFire() {
        if (be_targetPlayer == null) return;
        int radius = BetterEndAddon.dragonEndlessBreathRadius;
        spawnFireLogic(radius, BetterEndAddon.dragonFireBlockID);
    }

    @Unique
    private void spawnFireLogic(int radius, int blockID) {
        for (int i = 0; i < radius * 5; i++) {
            double r = radius * Math.sqrt(this.rand.nextDouble());
            double theta = this.rand.nextDouble() * 2 * Math.PI;

            int x = MathHelper.floor_double(be_targetPlayer.posX + r * Math.cos(theta));
            int z = MathHelper.floor_double(be_targetPlayer.posZ + r * Math.sin(theta));
            int y = MathHelper.floor_double(be_targetPlayer.posY);

            for (int k = 0; k <= 4; k++) {
                int checkY = y + k;
                if (this.worldObj.isAirBlock(x, checkY, z) && this.worldObj.doesBlockHaveSolidTopSurface(x, checkY - 1, z)) {
                    this.worldObj.setBlock(x, checkY, z, blockID);
                    break;
                }
                checkY = y - k;
                if (k != 0 && this.worldObj.isAirBlock(x, checkY, z) && this.worldObj.doesBlockHaveSolidTopSurface(x, checkY - 1, z)) {
                    this.worldObj.setBlock(x, checkY, z, blockID);
                    break;
                }
            }
        }
    }

    // --- Helping method
    @Unique
    private void be_updateBodyParts() {
        if (this.ringBufferIndex < 0) {
            for(int i = 0; i < this.ringBuffer.length; ++i) {
                this.ringBuffer[i][0] = this.rotationYaw;
                this.ringBuffer[i][1] = this.posY;
            }
        }
        if (++this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
        }
        this.ringBuffer[this.ringBufferIndex][0] = this.rotationYaw;
        this.ringBuffer[this.ringBufferIndex][1] = this.posY;

        this.renderYawOffset = this.rotationYaw;

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

        float f1 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float)Math.PI;
        float f2 = MathHelper.cos(f1);
        float f3 = -MathHelper.sin(f1);
        float f4 = this.rotationYaw * (float)Math.PI / 180.0F;
        float f5 = MathHelper.sin(f4);
        float f6 = MathHelper.cos(f4);

        this.dragonPartBody.onUpdate();
        this.dragonPartBody.setLocationAndAngles(this.posX + (f5 * 0.5F), this.posY, this.posZ - (f6 * 0.5F), 0.0F, 0.0F);
        this.dragonPartWing1.onUpdate();
        this.dragonPartWing1.setLocationAndAngles(this.posX + (f6 * 4.5F), this.posY + 2.0D, this.posZ + (f5 * 4.5F), 0.0F, 0.0F);
        this.dragonPartWing2.onUpdate();
        this.dragonPartWing2.setLocationAndAngles(this.posX - (f6 * 4.5F), this.posY + 2.0D, this.posZ - (f5 * 4.5F), 0.0F, 0.0F);

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
        this.dragonPartHead.setLocationAndAngles(this.posX + (f7 * 5.5F * f2), this.posY + (adouble1[1] - adouble[1]) + (f3 * 5.5F), this.posZ - (f8 * 5.5F * f2), 0.0F, 0.0F);

        for(int k = 0; k < 3; ++k) {
            EntityDragonPart entitydragonpart = (k == 0) ? this.dragonPartTail1 : ((k == 1) ? this.dragonPartTail2 : this.dragonPartTail3);
            double[] adouble2 = this.getMovementOffsets(12 + k * 2, 1.0F);
            float f9 = this.rotationYaw * (float)Math.PI / 180.0F + this.simplifyAngle(adouble2[0] - adouble[0]) * (float)Math.PI / 180.0F;
            float f10 = MathHelper.sin(f9);
            float f11 = MathHelper.cos(f9);
            float f12 = 1.5F;
            float f13 = (k + 1) * 2.0F;
            entitydragonpart.onUpdate();
            entitydragonpart.setLocationAndAngles(this.posX - ((f5 * f12 + f10 * f13) * f2), this.posY + (adouble2[1] - adouble[1]) - ((f13 + f12) * f3) + 1.5D, this.posZ + ((f6 * f12 + f11 * f13) * f2), 0.0F, 0.0F);
        }

        if (!this.worldObj.isRemote) {
            this.destroyBlocksInAABB(this.dragonPartHead.boundingBox);
            this.destroyBlocksInAABB(this.dragonPartBody.boundingBox);
        }
    }
}