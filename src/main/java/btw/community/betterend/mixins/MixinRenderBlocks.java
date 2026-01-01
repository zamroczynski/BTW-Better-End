package btw.community.betterend.mixins;

import btw.community.betterend.block.BlockDragonFire;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks {
    @Shadow public IBlockAccess blockAccess;
    @Shadow public abstract boolean hasOverrideBlockTexture();
    @Shadow public Icon overrideBlockTexture;

    @Inject(method = "renderBlockByRenderType", at = @At("HEAD"), cancellable = true)
    public void onRenderBlockByRenderType(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (block instanceof BlockDragonFire) {
            boolean result = renderDragonFire((BlockDragonFire)block, x, y, z);
            cir.setReturnValue(result);
        }
    }

    @Unique
    public boolean renderDragonFire(BlockDragonFire fireBlock, int x, int y, int z) {
        Tessellator tessellator = Tessellator.instance;
        Icon icon0 = fireBlock.getFireIcon(0);
        Icon icon1 = fireBlock.getFireIcon(1);
        Icon currentIcon = icon0;

        if (this.hasOverrideBlockTexture()) {
            currentIcon = this.overrideBlockTexture;
        }

        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setBrightness(fireBlock.getMixedBrightnessForBlock(this.blockAccess, x, y, z));

        double minU = (double)currentIcon.getMinU();
        double minV = (double)currentIcon.getMinV();
        double maxU = (double)currentIcon.getMaxU();
        double maxV = (double)currentIcon.getMaxV();

        float scale = 1.4F;
        boolean rendered = false;

        boolean solidBelow = this.blockAccess.doesBlockHaveSolidTopSurface(x, y - 1, z);

        if (!rendered && solidBelow) {
            double xCenter = (double)x + 0.5D + 0.2D;
            double xCenterNeg = (double)x + 0.5D - 0.2D;
            double zCenter = (double)z + 0.5D + 0.2D;
            double zCenterNeg = (double)z + 0.5D - 0.2D;
            double xNeg = (double)x + 0.5D - 0.3D;
            double xPos = (double)x + 0.5D + 0.3D;
            double zNeg = (double)z + 0.5D - 0.3D;
            double zPos = (double)z + 0.5D + 0.3D;

            tessellator.addVertexWithUV(xNeg, (double)((float)y + scale), (double)(z + 1), maxU, minV);
            tessellator.addVertexWithUV(xCenter, (double)(y + 0), (double)(z + 1), maxU, maxV);
            tessellator.addVertexWithUV(xCenter, (double)(y + 0), (double)(z + 0), minU, maxV);
            tessellator.addVertexWithUV(xNeg, (double)((float)y + scale), (double)(z + 0), minU, minV);

            tessellator.addVertexWithUV(xPos, (double)((float)y + scale), (double)(z + 0), maxU, minV);
            tessellator.addVertexWithUV(xCenterNeg, (double)(y + 0), (double)(z + 0), maxU, maxV);
            tessellator.addVertexWithUV(xCenterNeg, (double)(y + 0), (double)(z + 1), minU, maxV);
            tessellator.addVertexWithUV(xPos, (double)((float)y + scale), (double)(z + 1), minU, minV);

            minU = (double)icon1.getMinU();
            minV = (double)icon1.getMinV();
            maxU = (double)icon1.getMaxU();
            maxV = (double)icon1.getMaxV();

            tessellator.addVertexWithUV((double)(x + 1), (double)((float)y + scale), zPos, maxU, minV);
            tessellator.addVertexWithUV((double)(x + 1), (double)(y + 0), zCenterNeg, maxU, maxV);
            tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), zCenterNeg, minU, maxV);
            tessellator.addVertexWithUV((double)(x + 0), (double)((float)y + scale), zPos, minU, minV);

            tessellator.addVertexWithUV((double)(x + 0), (double)((float)y + scale), zNeg, maxU, minV);
            tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), zCenter, maxU, maxV);
            tessellator.addVertexWithUV((double)(x + 1), (double)(y + 0), zCenter, minU, maxV);
            tessellator.addVertexWithUV((double)(x + 1), (double)((float)y + scale), zNeg, minU, minV);

            xCenter = (double)x + 0.5D - 0.5D;
            xCenterNeg = (double)x + 0.5D + 0.5D;
            zCenter = (double)z + 0.5D - 0.5D;
            zCenterNeg = (double)z + 0.5D + 0.5D;
            xNeg = (double)x + 0.5D - 0.4D;
            xPos = (double)x + 0.5D + 0.4D;
            zNeg = (double)z + 0.5D - 0.4D;
            zPos = (double)z + 0.5D + 0.4D;

            tessellator.addVertexWithUV(xNeg, (double)((float)y + scale), (double)(z + 0), minU, minV);
            tessellator.addVertexWithUV(xCenter, (double)(y + 0), (double)(z + 0), minU, maxV);
            tessellator.addVertexWithUV(xCenter, (double)(y + 0), (double)(z + 1), maxU, maxV);
            tessellator.addVertexWithUV(xNeg, (double)((float)y + scale), (double)(z + 1), maxU, minV);

            tessellator.addVertexWithUV(xPos, (double)((float)y + scale), (double)(z + 1), minU, minV);
            tessellator.addVertexWithUV(xCenterNeg, (double)(y + 0), (double)(z + 1), minU, maxV);
            tessellator.addVertexWithUV(xCenterNeg, (double)(y + 0), (double)(z + 0), maxU, maxV);
            tessellator.addVertexWithUV(xPos, (double)((float)y + scale), (double)(z + 0), maxU, minV);

            minU = (double)icon0.getMinU();
            minV = (double)icon0.getMinV();
            maxU = (double)icon0.getMaxU();
            maxV = (double)icon0.getMaxV();

            tessellator.addVertexWithUV((double)(x + 0), (double)((float)y + scale), zPos, minU, minV);
            tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), zCenterNeg, minU, maxV);
            tessellator.addVertexWithUV((double)(x + 1), (double)(y + 0), zCenterNeg, maxU, maxV);
            tessellator.addVertexWithUV((double)(x + 1), (double)((float)y + scale), zPos, maxU, minV);

            tessellator.addVertexWithUV((double)(x + 1), (double)((float)y + scale), zNeg, minU, minV);
            tessellator.addVertexWithUV((double)(x + 1), (double)(y + 0), zCenter, minU, maxV);
            tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), zCenter, maxU, maxV);
            tessellator.addVertexWithUV((double)(x + 0), (double)((float)y + scale), zNeg, maxU, minV);

            rendered = true;
        }

        return rendered;
    }
}