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
    public static int dragonAttackDamage;
    public static int dragonMaxHealth;
    public static int dragonKnockback;
    public static int dragonCrystalRegenAmount;
    public static int dragonCrystalExplosionDamage;
    public static int dragonXPPeriodic;
    public static int dragonXPFinal;
    public static int endlessFireBlockID;
    public static int dragonEndlessBreathCooldown;
    public static int dragonEndlessBreathDuration;
    public static int dragonEndlessFireDurationMin;
    public static int dragonEndlessFireDurationMax;
    public static int dragonEndlessBreathRadius;
    public static int dragonEndlessBreathWitherStrength;
    public static int dragonAttackCooldown;

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
        BetterEndBlocks.createBlocks();

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
        config.registerInt("Dragon.AttackDamage", 30, "Damage dealt by Ender Dragon's head collision (in half-hearts). Vanilla: 10, Default: 30");
        config.registerInt("Dragon.MaxHealth", 200, "Max health of Ender Dragon (200 = 100 hearts). Vanilla: 200");
        config.registerInt("Dragon.KnockbackStrength", 4, "Knockback strength when hit by Dragon body/wings. Vanilla: 4");
        config.registerInt("Dragon.CrystalRegenAmount", 1, "Health restored by Ender Crystal per tick cycle. Vanilla: 1");
        config.registerInt("Dragon.CrystalExplosionDamage", 10, "Damage taken by Dragon when a linked crystal is destroyed. Vanilla: 10");
        config.registerInt("Dragon.XPPeriodic", 1000, "Amount of XP dropped periodically during Dragon death animation. Vanilla: 1000");
        config.registerInt("Dragon.XPFinal", 2000, "Amount of XP dropped at the end of Dragon death animation. Vanilla: 2000");
        config.registerInt("IDs.EndlessFire", 3000, "ID for the Endless Fire block");
        config.registerInt("Dragon.EndlessBreath.Cooldown", 100, "Internal cooldown for breath attack (not used in State Machine mode).");
        config.registerInt("Dragon.EndlessBreath.Duration", 60, "Duration of the Dragon's roar/hover during attack in ticks (60 = 3s).");
        config.registerInt("Dragon.EndlessBreath.FireDurationMin", 30, "Minimum duration of Endless Fire on ground in seconds.");
        config.registerInt("Dragon.EndlessBreath.FireDurationMax", 60, "Maximum duration of Endless Fire on ground in seconds.");
        config.registerInt("Dragon.EndlessBreath.Radius", 10, "Radius around player to spawn Endless Fire.");
        config.registerInt("Dragon.EndlessBreath.WitherStrength", 1, "Strength of Wither effect (0 = I, 1 = II, etc).");
        config.registerInt("Dragon.General.AttackCooldown", 15, "Time in seconds between Dragon attacks (Peace phase).");
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
        dragonAttackDamage = config.getInt("Dragon.AttackDamage");
        dragonMaxHealth = config.getInt("Dragon.MaxHealth");
        dragonKnockback = config.getInt("Dragon.KnockbackStrength");
        dragonCrystalRegenAmount = config.getInt("Dragon.CrystalRegenAmount");
        dragonCrystalExplosionDamage = config.getInt("Dragon.CrystalExplosionDamage");
        dragonXPPeriodic = config.getInt("Dragon.XPPeriodic");
        dragonXPFinal = config.getInt("Dragon.XPFinal");
        endlessFireBlockID = config.getInt("IDs.EndlessFire");
        dragonEndlessBreathCooldown = config.getInt("Dragon.EndlessBreath.Cooldown");
        dragonEndlessBreathDuration = config.getInt("Dragon.EndlessBreath.Duration");
        dragonEndlessFireDurationMin = config.getInt("Dragon.EndlessBreath.FireDurationMin");
        dragonEndlessFireDurationMax = config.getInt("Dragon.EndlessBreath.FireDurationMax");
        dragonEndlessBreathRadius = config.getInt("Dragon.EndlessBreath.Radius");
        dragonEndlessBreathWitherStrength = config.getInt("Dragon.EndlessBreath.WitherStrength");
        dragonAttackCooldown = config.getInt("Dragon.General.AttackCooldown");
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