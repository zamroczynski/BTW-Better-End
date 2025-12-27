package btw.community.betterend.mixins;

import btw.community.betterend.BetterEndAddon;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityEnderCrystal.class)
public class MixinEntityEnderCrystal {

    @Inject(method = "attackEntityFrom",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityEnderCrystal;setDead()V"))
    private void onCrystalDestroyed(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity sourceEntity = source.getEntity();

        if (sourceEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sourceEntity;

            int durationTicks = BetterEndAddon.crystalDebuffDuration * 20;

            if (durationTicks > 0) {
                Potion targetPotion = (Math.random() < 0.5) ? Potion.confusion : Potion.blindness;

                player.addPotionEffect(new PotionEffect(targetPotion.id, durationTicks));
            }
        }
    }
}