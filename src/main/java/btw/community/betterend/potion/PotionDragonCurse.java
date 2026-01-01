package btw.community.betterend.potion;

import net.minecraft.src.Potion;

public class PotionDragonCurse extends Potion {

    public PotionDragonCurse(int id) {
        super(id, true, 0x4B0082);
        this.setPotionName("potion.betterend.dragon_curse");
        this.setIconIndex(5, 0);
    }
}