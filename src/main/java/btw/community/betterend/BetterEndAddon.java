package btw.community.betterend;

import api.BTWAddon;
import api.config.AddonConfig;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class BetterEndAddon extends BTWAddon {
    private static BetterEndAddon instance;

    public static int totemCooldownSeconds;
    public static boolean isTotemSingleUse;
    public static int crystalDebuffDuration;

    public BetterEndAddon() {
        super();
        instance = this;
    }

    public static BetterEndAddon getInstance() {
        return instance;
    }

    @Override
    public void initialize() {
        AddonConfig config = this.addonConfig;
        System.out.println(this.getName() + " Config Loaded: Cooldown=" + totemCooldownSeconds + "s, SingleUse=" + isTotemSingleUse);
        BetterEndItems.createItems();
        createRecipes();
    }

    @Override
    public void registerConfigProperties(AddonConfig config) {
        config.registerInt("IDs.TotemOfTheCraven", 31000, "ID for the Totem of the Craven item");
        config.registerInt("General.TotemCooldownSeconds", 600,
                "Cooldown for Totem of the Craven in seconds. Default: 600 (10 minutes).");
        config.registerBoolean("General.IsTotemSingleUse", false,
                "If true, Totem of the Craven is consumed upon use. Default: false.");

        config.registerInt("General.CrystalDebuffDuration", 10,
                "Duration of Nausea/Blindness effect when destroying an Ender Crystal (in seconds). Default: 10.");
    }

    @Override
    public void handleConfigProperties(AddonConfig config) {
        BetterEndItems.totemOfTheCravenID = config.getInt("IDs.TotemOfTheCraven");
        totemCooldownSeconds = config.getInt("General.TotemCooldownSeconds");
        isTotemSingleUse = config.getBoolean("General.IsTotemSingleUse");

        crystalDebuffDuration = config.getInt("General.CrystalDebuffDuration");
    }

    private void createRecipes() {
        CraftingManager.getInstance().addRecipe(new ItemStack(BetterEndItems.totemOfTheCraven), new Object[] {
                "GRG",
                "CNC",
                "GRG",
                'G', Block.thinGlass,
                'R', BTWItems.redstoneEye,
                'C', BTWItems.corpseEye,
                'N', Item.netherStar
        });
    }
}