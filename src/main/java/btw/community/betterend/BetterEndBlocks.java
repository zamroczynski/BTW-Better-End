package btw.community.betterend;

import btw.community.betterend.block.BlockEndlessFire;
import net.minecraft.src.Block;

public class BetterEndBlocks {
    public static Block endlessFire;

    public static void createBlocks() {
        endlessFire = new BlockEndlessFire(BetterEndAddon.endlessFireBlockID);
    }
}