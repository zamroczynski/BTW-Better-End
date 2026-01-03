package btw.community.betterend.client;

import btw.community.betterend.entity.EntityEnderGuardian;
import net.minecraft.src.Entity;
import net.minecraft.src.RenderZombie;
import net.minecraft.src.ResourceLocation;

public class RenderEnderGuardian extends RenderZombie {

    // Ścieżka do tekstury: assets/betterend/textures/entity/ender_guardian.png
    private static final ResourceLocation guardianTexture = new ResourceLocation("betterend", "textures/entity/ender_guardian.png");

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return guardianTexture;
    }
}