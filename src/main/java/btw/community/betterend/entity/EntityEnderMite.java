package btw.community.betterend.entity;

import btw.community.betterend.BetterEndAddon;
import btw.community.betterend.BetterEndItems;
import net.minecraft.src.*;

public class EntityEnderMite extends EntityMob {

    public EntityEnderMite(World world) {
        super(world);
        this.setSize(0.3F, 0.7F);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.6D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((double)BetterEndAddon.miteAttackDamage);
    }

    @Override
    protected String getLivingSound() {
        return "mob.silverfish.say";
    }

    @Override
    protected String getHurtSound() {
        return "mob.silverfish.hit";
    }

    @Override
    protected String getDeathSound() {
        return "mob.silverfish.kill";
    }

    @Override
    protected void playStepSound(int x, int y, int z, int blockId) {
        this.playSound("mob.silverfish.step", 0.15F, 1.0F);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();

        boolean attackSuccess = target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;

            if (player.getFoodStats().getFoodLevel() > 0) {
                player.getFoodStats().addStats(-1, 0.0F);
            }

            destroyArmor(player);

            if (!attackSuccess && damage == 0) {
                attackSuccess = true;
            }
        }

        return attackSuccess;
    }

    private void destroyArmor(EntityPlayer player) {
        ItemStack[] armor = player.inventory.armorInventory;
        for (int i = 0; i < armor.length; i++) {
            ItemStack stack = armor[i];
            if (stack != null) {
                stack.damageItem(1, player);

                if (stack.stackSize <= 0) {
                    player.inventory.armorInventory[i] = null;
                }
            }
        }
    }

    @Override
    protected int getDropItemId() {
        return BetterEndItems.miteGland.itemID;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingLevel) {
        if (this.rand.nextInt(100) < BetterEndAddon.miteDropChance) {
            this.dropItem(BetterEndItems.miteGland.itemID, 1);
        }
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }
}