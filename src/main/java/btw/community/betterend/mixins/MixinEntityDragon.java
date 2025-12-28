package btw.community.betterend.mixins;

import btw.community.betterend.BetterEndAddon;
import net.minecraft.src.EntityDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityDragon.class)
public class MixinEntityDragon {

    @ModifyConstant(method = "attackEntitiesInList", constant = @Constant(floatValue = 10.0F))
    private float modifyDragonAttackDamage(float original) {
        return (float) BetterEndAddon.dragonAttackDamage;
    }

    @ModifyConstant(method = "applyEntityAttributes", constant = @Constant(doubleValue = 200.0D))
    private double modifyMaxHealth(double original) {
        return (double) BetterEndAddon.dragonMaxHealth;
    }

    @ModifyConstant(method = "collideWithEntities", constant = @Constant(doubleValue = 4.0D))
    private double modifyKnockbackStrength(double original) {
        return (double) BetterEndAddon.dragonKnockback;
    }

    @ModifyConstant(method = "updateDragonEnderCrystal", constant = @Constant(floatValue = 1.0F))
    private float modifyCrystalRegen(float original) {
        return (float) BetterEndAddon.dragonCrystalRegenAmount;
    }

    @ModifyConstant(method = "updateDragonEnderCrystal", constant = @Constant(floatValue = 10.0F))
    private float modifyCrystalExplosionDamage(float original) {
        return (float) BetterEndAddon.dragonCrystalExplosionDamage;
    }

    @ModifyConstant(method = "onDeathUpdate", constant = @Constant(intValue = 1000))
    private int modifyPeriodicXP(int original) {
        return BetterEndAddon.dragonXPPeriodic;
    }

    @ModifyConstant(method = "onDeathUpdate", constant = @Constant(intValue = 2000))
    private int modifyFinalXP(int original) {
        return BetterEndAddon.dragonXPFinal;
    }
}