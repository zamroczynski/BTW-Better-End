package btw.community.betterend.mixins;

import btw.community.betterend.BetterEndAddon;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryEffectRenderer;
import net.minecraft.src.PotionEffect;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryEffectRenderer.class)
public abstract class MixinInventoryEffectRenderer extends GuiContainer {

    public MixinInventoryEffectRenderer(net.minecraft.src.Container par1Container) {
        super(par1Container);
    }

    @Inject(method = "drawPotionIcon", at = @At("HEAD"))
    private void onDrawPotionIcon(int x, int y, PotionEffect potionEffect, CallbackInfo ci) {
        if (potionEffect.getPotionID() == BetterEndAddon.potionDragonCurseID) {
            int color = 0xCC4B0082;

            drawRect(x + 6, y + 7, x + 6 + 18, y + 7 + 18, color);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}