package btw.community.betterend.client;

import btw.community.betterend.client.texture.EnderMiteColorizer;
import btw.community.betterend.client.texture.RecoloredEntityTexture;
import btw.community.betterend.entity.EntityEnderMite;
import net.minecraft.src.*;

public class RenderEnderMite extends RenderLiving {
    private static ResourceLocation miteTextureLoc = null;
    private static final ResourceLocation silverfishBase = new ResourceLocation("textures/entity/silverfish.png");

    public RenderEnderMite() {
        super(new ModelSilverfish(), 0.3F);
    }

    protected float getMiteDeathRotation(EntityEnderMite entity) {
        return 180.0F;
    }

    @Override
    protected float getDeathMaxRotation(EntityLivingBase entity) {
        return this.getMiteDeathRotation((EntityEnderMite)entity);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (miteTextureLoc == null) {
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            RecoloredEntityTexture dynamicTexture = new RecoloredEntityTexture(silverfishBase, new EnderMiteColorizer());

            miteTextureLoc = textureManager.getDynamicTextureLocation("betterend_endermite", dynamicTexture);
        }
        return miteTextureLoc;
    }
}