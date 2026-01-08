package btw.community.betterend.client.texture;

import java.awt.image.BufferedImage;

public class EnderGuardianColorizer implements IColorizer {

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

            // Zombie jest głównie zielony. Aby uzyskać ładny fiolet, musimy bazować na jasności piksela,
            // a nie na bezpośrednim mnożeniu kanałów (bo R i B są w zombie niskie).
            // Używamy standardowej wagi luminancji, ale z lekkim faworyzowaniem zielonego,
            // bo tam jest najwięcej "informacji" w teksturze zombie.
            float luminance = (r * 0.2f + g * 0.7f + b * 0.1f);

            // Docelowy odcień fioletu (Enderowy)
            // Podbijamy lekko jasność (* 1.1), aby tekstura nie była zbyt ciemna po konwersji
            int newR = (int)(luminance * 0.65f * 1.1f);
            int newG = (int)(luminance * 0.20f * 1.1f);
            int newB = (int)(luminance * 0.85f * 1.1f);

            // Clamp values to 0-255 (zabezpieczenie przed wyjściem poza zakres)
            newR = Math.min(255, Math.max(0, newR));
            newG = Math.min(255, Math.max(0, newG));
            newB = Math.min(255, Math.max(0, newB));

            dstPixels[i] = (alpha << 24) | (newR << 16) | (newG << 8) | newB;
        }

        newImage.setRGB(0, 0, w, h, dstPixels, 0, w);
        return newImage;
    }
}