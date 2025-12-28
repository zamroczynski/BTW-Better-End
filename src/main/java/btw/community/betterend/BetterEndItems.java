package btw.community.betterend;

import btw.community.betterend.item.ItemMiteGland;
import btw.community.betterend.item.ItemTotemOfTheCraven;
import net.minecraft.src.Item;

public class BetterEndItems {
    public static Item totemOfTheCraven;
    public static int totemOfTheCravenID = 31000;

    public static Item miteGland;
    public static int miteGlandID = 31001;

    public static void createItems() {
        totemOfTheCraven = new ItemTotemOfTheCraven(totemOfTheCravenID - 256);
        miteGland = new ItemMiteGland(miteGlandID - 256);
    }
}