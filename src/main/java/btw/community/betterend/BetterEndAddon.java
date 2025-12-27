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
    }

    @Override
    public void handleConfigProperties(AddonConfig config) {
        BetterEndItems.totemOfTheCravenID = config.getInt("IDs.TotemOfTheCraven");
        totemCooldownSeconds = config.getInt("General.TotemCooldownSeconds");
        isTotemSingleUse = config.getBoolean("General.IsTotemSingleUse");
    }

    private void createRecipes() {
        // Glass Pane | Redstone Eye | Glass Pane
        // Corpse Eye | Nether Star  | Corpse Eye
        // Glass Pane | Redstone Eye | Glass Pane

        CraftingManager.getInstance().addRecipe(new ItemStack(BetterEndItems.totemOfTheCraven), new Object[] {
                "GRG",
                "CNC",
                "GRG",
                'G', Block.thinGlass,          // Glass Pane
                'R', BTWItems.redstoneEye,     // Redstone Eye (z BTW)
                'C', BTWItems.corpseEye,       // Corpse Eye (z BTW)
                'N', Item.netherStar           // Nether Star
        });
    }
}