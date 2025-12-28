package btw.community.betterend.item;

import btw.community.betterend.client.MiteGlandSprite;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Item;
import net.minecraft.src.TextureAtlasSprite;

public class ItemMiteGland extends Item {
    public ItemMiteGland(int id) {
        super(id);
        this.setUnlocalizedName("betterend.mite_gland");
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public void registerIcons(IconRegister register) {
        TextureAtlasSprite customSprite = new MiteGlandSprite("betterend:mite_gland", "btw:witch_wart");

        this.itemIcon = register.registerIcon("betterend:mite_gland", customSprite);
    }
}