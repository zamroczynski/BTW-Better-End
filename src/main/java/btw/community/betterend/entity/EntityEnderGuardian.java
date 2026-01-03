package btw.community.betterend.entity;

import btw.community.betterend.BetterEndAddon;
import btw.item.BTWItems;
import net.minecraft.src.*;

public class EntityEnderGuardian extends EntityZombie {

    public EntityEnderGuardian(World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(BetterEndAddon.guardianMaxHealth);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(BetterEndAddon.guardianAttackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(BetterEndAddon.guardianKnockbackResistance);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(BetterEndAddon.guardianMovementSpeed);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(BetterEndAddon.guardianFollowRange);
    }

    @Override
    protected void addRandomArmor() {
        this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.steelAxe));

        int roll = this.rand.nextInt(100);
        int currentThreshold = 0;

        currentThreshold += BetterEndAddon.guardianArmorChanceWool;
        if (roll < currentThreshold) {
            equipFullSet(BTWItems.woolHelmet, BTWItems.woolChest, BTWItems.woolLeggings, BTWItems.woolBoots);
            return;
        }

        currentThreshold += BetterEndAddon.guardianArmorChanceLeather;
        if (roll < currentThreshold) {
            equipFullSet(Item.helmetLeather, Item.plateLeather, Item.legsLeather, Item.bootsLeather);
            return;
        }

        currentThreshold += BetterEndAddon.guardianArmorChanceIron;
        if (roll < currentThreshold) {
            equipFullSet(Item.helmetIron, Item.plateIron, Item.legsIron, Item.bootsIron);
            return;
        }

        currentThreshold += BetterEndAddon.guardianArmorChanceChain;
        if (roll < currentThreshold) {
            equipFullSet(Item.helmetChain, Item.plateChain, Item.legsChain, Item.bootsChain);
            return;
        }

        currentThreshold += BetterEndAddon.guardianArmorChanceDiamond;
        if (roll < currentThreshold) {
            equipFullSet(Item.helmetDiamond, Item.plateDiamond, Item.legsDiamond, Item.bootsDiamond);
            return;
        }

        currentThreshold += BetterEndAddon.guardianArmorChanceSoulSteel;
        if (roll < currentThreshold) {
            equipFullSet(BTWItems.plateHelmet, BTWItems.plateBreastplate, BTWItems.plateLeggings, BTWItems.plateBoots);
            return;
        }
    }

    private void equipFullSet(Item helm, Item chest, Item legs, Item boots) {
        this.setCurrentItemOrArmor(4, new ItemStack(helm));
        this.setCurrentItemOrArmor(3, new ItemStack(chest));
        this.setCurrentItemOrArmor(2, new ItemStack(legs));
        this.setCurrentItemOrArmor(1, new ItemStack(boots));
    }

    @Override
    protected void checkForCatchFireInSun() {
        //
    }

    @Override
    protected int getDropItemId() {
        return 0;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingLevel) {
        //
    }

    @Override
    protected void dropRareDrop(int lootingLevel) {
        //
    }

    @Override
    protected void dropEquipment(boolean par1, int par2) {
        //
    }

    @Override
    public boolean interact(EntityPlayer player) {
        return false;
    }

    @Override
    public void onKillEntity(EntityLivingBase entityKilled) {
        //
    }
}