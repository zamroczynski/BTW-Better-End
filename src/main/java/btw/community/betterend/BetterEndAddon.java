package btw.community.betterend;

import api.BTWAddon;
import api.config.AddonConfig;

public class BetterEndAddon extends BTWAddon {
    private static BetterEndAddon instance;

    // Config variables
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
        this.addonConfig = new AddonConfig(this.modID);
        this.registerConfigProperties(this.addonConfig);
        this.addonConfig.readAndWriteConfig();

        BetterEndItems.totemOfTheCravenID = this.addonConfig.getInt("IDs.TotemOfTheCraven");
        totemCooldownSeconds = this.addonConfig.getInt("General.TotemCooldownSeconds");
        isTotemSingleUse = this.addonConfig.getBoolean("General.IsTotemSingleUse");

        System.out.println(this.getName() + " Config Loaded: Cooldown=" + totemCooldownSeconds + "s, SingleUse=" + isTotemSingleUse);
    }

    @Override
    public void registerConfigProperties(AddonConfig config) {
        config.registerInt("IDs.TotemOfTheCraven", 31000, "ID for the Totem of the Craven item");

        config.registerInt("General.TotemCooldownSeconds", 600,
                "Cooldown for Totem of the Craven in seconds. Default: 600 (10 minutes).");

        config.registerBoolean("General.IsTotemSingleUse", false,
                "If true, Totem of the Craven is consumed upon use. Default: false.");
    }
}