package btw.community.betterend.client.texture;

import java.awt.image.BufferedImage;

public class EnderMiteColorizer implements IColorizer {

    @Override
    public BufferedImage colorize(BufferedImage original) {
        int w = original.getWidth();
        int h = original.getHeight();
        BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        int[] srcPixels = new int[w * h];
        original.getRGB(0, 0, w, h, srcPixels, 0, w);
        int[] dstPixels = new int[w * h];

        for (int i = 0; i < srcPixels.length; i++) {
            int color = srcPixels[i];
            int alpha = (color >> 24) & 0xFF;

            if (alpha == 0) {
                dstPixels[i] = 0;
                continue;
            }

            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            int newR = (int)(r * 0.30f);
            int newG = (int)(g * 0.15f);
            int newB = (int)(b * 0.35f);

            newR = Math.min(255, Math.max(0, newR));
            newG = Math.min(255, Math.max(0, newG));
            newB = Math.min(255, Math.max(0, newB));

            dstPixels[i] = (alpha << 24) | (newR << 16) | (newG << 8) | newB;
        }

        newImage.setRGB(0, 0, w, h, dstPixels, 0, w);
        return newImage;
    }
}