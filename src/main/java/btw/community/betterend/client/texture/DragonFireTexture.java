package btw.client.texture;

import net.minecraft.src.TextureUtil;

public class DragonFireTexture extends FireTexture {

    public DragonFireTexture(String name) {
        super(name, 0);
    }

    @Override
    public void updateAnimation() {
        if (this.fireAnimation != null) {
            this.fireAnimation.copyRegularFireFrameToByteBuffer(this.frameBuffer, this.bufferPixelSize);
        }

        for (int i = 0; i < this.bufferPixelSize; ++i) {
            int pixel = this.frameBuffer[i];

            int alpha = (pixel >> 24) & 0xFF;

            if (alpha == 0) continue;

            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;

            int newR = r;
            int newG = (int)(g * 0.2f);
            int newB = g + (int)(r * 0.4f);

            if (newB > 255) newB = 255;

            this.frameBuffer[i] = (alpha << 24) | (newR << 16) | (newG << 8) | newB;
        }
        TextureUtil.uploadTextureSub(this.frameBuffer, this.bufferWidth, this.bufferHeight, this.originX, this.originY, false, false);
    }
}