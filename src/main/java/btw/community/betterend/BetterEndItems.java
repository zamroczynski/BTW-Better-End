package btw.community.betterend;

import btw.community.betterend.item.ItemTotemOfTheCraven;
import net.minecraft.src.Item;

public class BetterEndItems {
    public static Item totemOfTheCraven;
    public static int totemOfTheCravenID = 31000;

    public static void createItems() {
        totemOfTheCraven = new ItemTotemOfTheCraven(totemOfTheCravenID - 256);
    }
}