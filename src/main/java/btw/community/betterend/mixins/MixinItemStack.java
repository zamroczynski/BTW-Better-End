package btw.community.betterend.mixins;

import btw.community.betterend.BetterEndAddon;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @ModifyVariable(method = "damageItem", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int modifyDamageForDragonCurse(int amount, int originalAmount, EntityLivingBase entity) {
        if (entity != null && entity.isPotionActive(BetterEndAddon.potionDragonCurse)) {
            double multiplier = BetterEndAddon.dragonCurseArmorMultiplier;

            return (int) Math.ceil(amount * multiplier);
        }
        return amount;
    }
}