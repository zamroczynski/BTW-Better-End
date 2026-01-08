package btw.community.betterend.client;

import btw.community.betterend.client.texture.EnderGuardianColorizer;
import btw.community.betterend.client.texture.RecoloredEntityTexture;
import btw.community.betterend.entity.EntityEnderGuardian;
import net.minecraft.src.*;

public class RenderEnderGuardian extends RenderZombie {

    private static ResourceLocation guardianTextureLoc = null;
    // Ścieżka do bazowej tekstury Zombie w wersji 1.6.4
    // Dzięki temu mod pobierze teksturę z aktualnie używanego Resource Packa
    private static final ResourceLocation zombieBaseTexture = new ResourceLocation("textures/entity/zombie/zombie.png");

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (guardianTextureLoc == null) {
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

            // Tworzymy dynamiczną teksturę na bazie zombie, używając nowego kolorera
            RecoloredEntityTexture dynamicTexture = new RecoloredEntityTexture(zombieBaseTexture, new EnderGuardianColorizer());

            // Rejestrujemy ją pod unikalną nazwą w TextureManagerze, aby nie generować jej co klatkę
            guardianTextureLoc = textureManager.getDynamicTextureLocation("betterend_enderguardian", dynamicTexture);
        }
        return guardianTextureLoc;
    }
}