package btw.community.betterend.client.texture;

import net.minecraft.src.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class RecoloredEntityTexture extends DynamicTexture {
    private final ResourceLocation baseTextureLocation;
    private final IColorizer colorizer;

    public RecoloredEntityTexture(ResourceLocation baseResource, IColorizer colorizer) {
        super(loadAndProcess(baseResource, colorizer));
        this.baseTextureLocation = baseResource;
        this.colorizer = colorizer;
    }

    private static BufferedImage loadAndProcess(ResourceLocation loc, IColorizer colorizer) {
        ResourceManager rm = Minecraft.getMinecraft().getResourceManager();
        try {
            Resource r = rm.getResource(loc);
            InputStream stream = r.getInputStream();
            BufferedImage img = ImageIO.read(stream);
            stream.close();
            return colorizer.colorize(img);
        } catch (Exception e) {
            System.err.println("BetterEnd: Failed to load base texture for recolor: " + loc);
            e.printStackTrace();
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }

    @Override
    public void loadTexture(ResourceManager resourceManager) {
        try {
            Resource resource = resourceManager.getResource(this.baseTextureLocation);
            InputStream stream = resource.getInputStream();
            BufferedImage original = ImageIO.read(stream);
            stream.close();

            if (original != null) {
                BufferedImage processed = this.colorizer.colorize(original);

                processed.getRGB(0, 0, processed.getWidth(), processed.getHeight(),
                        this.getTextureData(), 0, processed.getWidth());

                this.updateDynamicTexture();
            }
        } catch (IOException e) {
            System.err.println("BetterEnd: Failed to reload dynamic texture: " + baseTextureLocation);
            e.printStackTrace();
        }
    }
}