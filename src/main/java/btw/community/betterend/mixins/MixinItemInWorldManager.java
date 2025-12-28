package btw.community.betterend.mixins;

import btw.community.betterend.BetterEndAddon;
import btw.community.betterend.entity.EntityEnderMite;
import net.minecraft.src.Block;
import net.minecraft.src.ItemInWorldManager;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInWorldManager.class)
public class MixinItemInWorldManager {

    @Shadow
    public World theWorld;

    @Inject(method = "removeBlock", at = @At("HEAD"))
    private void onRemoveBlockHead(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (!theWorld.isRemote && theWorld.provider.dimensionId == 1) {

            int blockID = theWorld.getBlockId(x, y, z);

            if (blockID == 0) return;

            int chance = 0;

            if (blockID == Block.whiteStone.blockID) {
                chance = BetterEndAddon.miteSpawnChanceEndBlocks;
            } else {
                chance = BetterEndAddon.miteSpawnChanceOtherBlocks;
            }

            if (chance > 0 && theWorld.rand.nextInt(100) < chance) {
                spawnEnderMite(x, y, z);
            }
        }
    }

    private void spawnEnderMite(int x, int y, int z) {
        EntityEnderMite mite = new EntityEnderMite(theWorld);
        mite.setLocationAndAngles((double)x + 0.5D, (double)y, (double)z + 0.5D, 0.0F, 0.0F);
        theWorld.spawnEntityInWorld(mite);
        mite.spawnExplosionParticle();
    }
}