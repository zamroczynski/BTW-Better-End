package btw.community.betterend;

import api.BTWAddon;
import api.config.AddonConfig;
import btw.item.BTWItems;
import btw.community.betterend.entity.EntityEnderMite;
import net.minecraft.src.Block;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.EntityList;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.EnvType;

public class BetterEndAddon extends BTWAddon {
    private static BetterEndAddon instance;

    public static int totemCooldownSeconds;
    public static boolean isTotemSingleUse;
    public static int crystalDebuffDuration;

    public static int miteSpawnChanceEndBlocks;
    public static int miteSpawnChanceOtherBlocks;
    public static int miteAttackDamage;
    public static boolean isMiteKnockbackEnabled;
    public static int miteDropChance;
    public static int entityEnderMiteID;
    public static int miteHungerDamage;
    public static int miteArmorDamage;

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
        System.out.println(this.getName() + " Config Loaded.");

        BetterEndItems.createItems();
        createRecipes();

        registerEntity();

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientProxy.registerRenderers();
        }
    }

    @Override
    public void registerConfigProperties(AddonConfig config) {
        config.registerInt("IDs.TotemOfTheCraven", 31000, "ID for the Totem of the Craven item");
        config.registerInt("General.TotemCooldownSeconds", 600, "Cooldown for Totem of the Craven in seconds.");
        config.registerBoolean("General.IsTotemSingleUse", false, "If true, Totem of the Craven is consumed upon use.");
        config.registerInt("General.CrystalDebuffDuration", 10, "Duration of Nausea/Blindness effect when destroying an Ender Crystal.");

        config.registerInt("IDs.MiteGland", 31001, "ID for the Mite Gland item");
        config.registerInt("IDs.EntityEnderMite", 201, "Global Entity ID for Ender Mite");
        config.registerInt("EnderMite.SpawnChanceEndBlocks", 10, "Chance (%) for Ender Mite to spawn when breaking End blocks.");
        config.registerInt("EnderMite.SpawnChanceOtherBlocks", 5, "Chance (%) for Ender Mite to spawn when breaking non-End blocks in The End.");
        config.registerInt("EnderMite.AttackDamage", 0, "Damage dealt by Ender Mite (in half-hearts). Default: 0");
        config.registerBoolean("EnderMite.KnockbackEnabled", false, "Does Ender Mite attack cause knockback? Default: false");
        config.registerInt("EnderMite.DropChance", 20, "Chance (%) to drop Mite Gland. Default: 20%");
        config.registerInt("EnderMite.HungerDamage", 1, "Amount of hunger removed per attack (1 = half shank). Default: 1");
        config.registerInt("EnderMite.ArmorDamage", 1, "Amount of durability lost on armor per attack. Default: 1");
    }

    @Override
    public void handleConfigProperties(AddonConfig config) {
        BetterEndItems.totemOfTheCravenID = config.getInt("IDs.TotemOfTheCraven");
        totemCooldownSeconds = config.getInt("General.TotemCooldownSeconds");
        isTotemSingleUse = config.getBoolean("General.IsTotemSingleUse");
        crystalDebuffDuration = config.getInt("General.CrystalDebuffDuration");

        BetterEndItems.miteGlandID = config.getInt("IDs.MiteGland");
        entityEnderMiteID = config.getInt("IDs.EntityEnderMite");

        miteSpawnChanceEndBlocks = config.getInt("EnderMite.SpawnChanceEndBlocks");
        miteSpawnChanceOtherBlocks = config.getInt("EnderMite.SpawnChanceOtherBlocks");
        miteAttackDamage = config.getInt("EnderMite.AttackDamage");
        isMiteKnockbackEnabled = config.getBoolean("EnderMite.KnockbackEnabled");
        miteDropChance = config.getInt("EnderMite.DropChance");
        miteHungerDamage = config.getInt("EnderMite.HungerDamage");
        miteArmorDamage = config.getInt("EnderMite.ArmorDamage");
    }

    private void registerEntity() {
        EntityList.addMapping(EntityEnderMite.class, "EnderMite", entityEnderMiteID, 0x152156, 0x69178d);
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