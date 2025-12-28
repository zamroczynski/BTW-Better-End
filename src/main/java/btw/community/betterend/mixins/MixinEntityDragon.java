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
}