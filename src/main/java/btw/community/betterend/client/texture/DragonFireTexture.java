package btw.client.texture;

import net.minecraft.src.Minecraft;
import net.minecraft.src.Resource;
import java.io.IOException;
import java.util.Arrays;

public class DragonFireTexture extends FireTexture {

    public DragonFireTexture(String name, int animationIndex) {
        super(name, animationIndex);
    }

    @Override
    public void loadSprite(Resource par1Resource) throws IOException {
        int targetWidth = 16;
        int targetHeight = 16;

        FireAnimation globalAnimation = FireAnimation.instanceArray[this.fireAnimationIndex];

        if (globalAnimation != null) {
            targetWidth = globalAnimation.width;
            targetHeight = globalAnimation.textureHeight;
        } else {
            System.out.println("BetterEnd: Warning - Vanilla Fire Animation not found, defaulting to 16x16.");
        }

        this.width = targetWidth;
        this.height = targetHeight;

        int[] emptyImage = new int[targetWidth * targetHeight];
        Arrays.fill(emptyImage, 0);

        this.framesTextureData.clear();
        this.framesTextureData.add(emptyImage);
    }

    @Override
    public void updateAnimation() {
        if (this.fireAnimation == null) {
            this.fireAnimation = FireAnimation.instanceArray[this.fireAnimationIndex];
        }

        if (this.fireAnimation != null) {
            int requiredSize = this.bufferWidth * this.bufferHeight;
            if (this.frameBuffer.length < requiredSize) {
                this.frameBuffer = new int[requiredSize];
            }

            this.fireAnimation.copyRegularFireFrameToByteBuffer(this.frameBuffer, requiredSize);

            for (int i = 0; i < requiredSize; ++i) {
                int pixel = this.frameBuffer[i];

                int alpha = (pixel >> 24) & 0xFF;
                if (alpha == 0) continue;

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;

                int newR = (int)(r * 0.6f);
                int newG = (int)(g * 0.1f);
                int newB = (int)(r * 0.95f);

                if (newR > 255) newR = 255;
                if (newG > 255) newG = 255;
                if (newB > 255) newB = 255;

                this.frameBuffer[i] = (alpha << 24) | (newR << 16) | (newG << 8) | newB;
            }
            net.minecraft.src.TextureUtil.uploadTextureSub(this.frameBuffer, this.bufferWidth, this.bufferHeight, this.originX, this.originY, false, false);
        }
    }
}