package btw.community.betterend.item;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class ItemMiteGland extends Item {
    public ItemMiteGland(int id) {
        super(id);
        this.setUnlocalizedName("betterend.mite_gland");
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.setTextureName("btw:witch_wart");
    }
}