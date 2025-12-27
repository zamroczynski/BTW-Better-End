package btw.community.betterend.client;

import net.minecraft.src.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class TotemCravenSprite extends TextureAtlasSprite {
    private final String baseTextureName;

    public TotemCravenSprite(String iconName, String baseTextureName) {
        super(iconName);
        this.baseTextureName = baseTextureName;
    }

    @Override
    public void loadSprite(Resource par1Resource) throws IOException {
        String domain = "btw";
        String path = "textures/items/corpse_eye.png";

        if (baseTextureName.contains(":")) {
            String[] parts = baseTextureName.split(":", 2);
            domain = parts[0];
            path = "textures/items/" + parts[1] + ".png";
        }

        ResourceLocation baseLocation = new ResourceLocation(domain, path);
        ResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();

        Resource baseResource = null;

        try {
            baseResource = resourceManager.getResource(baseLocation);
        } catch (Exception e) {
            // Fallback
        }

        if (baseResource == null) {
            super.loadSprite(par1Resource);
            return;
        }

        this.clearFramesTextureData();

        BufferedImage originalImage = ImageIO.read(baseResource.getInputStream());

        if (originalImage.getHeight() > originalImage.getWidth()) {
            int size = originalImage.getWidth();
            originalImage = originalImage.getSubimage(0, 0, size, size);
        }

        BufferedImage processedImage = applyPurpleBackground(originalImage);

        this.height = processedImage.getHeight();
        this.width = processedImage.getWidth();

        int[] pixelData = new int[this.height * this.width];
        processedImage.getRGB(0, 0, this.width, this.height, pixelData, 0, this.width);

        this.framesTextureData.add(pixelData);
    }

    private BufferedImage applyPurpleBackground(BufferedImage original) {
        int w = original.getWidth();
        int h = original.getHeight();
        BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        int[] srcPixels = new int[w * h];
        original.getRGB(0, 0, w, h, srcPixels, 0, w);

        int[] dstPixels = new int[w * h];
        int purpleColor = 0xFF4B0082;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int index = y * w + x;
                int color = srcPixels[index];
                int alpha = (color >> 24) & 0xFF;

                if (alpha > 10) {
                    dstPixels[index] = color;
                } else {
                    if (isNearSolid(srcPixels, w, h, x, y)) {
                        dstPixels[index] = purpleColor;
                    } else {
                        dstPixels[index] = 0x00000000;
                    }
                }
            }
        }

        newImage.setRGB(0, 0, w, h, dstPixels, 0, w);
        return newImage;
    }

    private boolean isNearSolid(int[] pixels, int w, int h, int x, int y) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;

                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                    int neighborAlpha = (pixels[ny * w + nx] >> 24) & 0xFF;
                    if (neighborAlpha > 10) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}