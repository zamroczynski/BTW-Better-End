package btw.community.betterend.client;

import btw.community.betterend.entity.EntityEnderMite;
import net.minecraft.src.*;

public class RenderEnderMite extends RenderLiving {
    private static final ResourceLocation miteTextures = new ResourceLocation("textures/entity/silverfish.png");

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
        return miteTextures;
    }
}