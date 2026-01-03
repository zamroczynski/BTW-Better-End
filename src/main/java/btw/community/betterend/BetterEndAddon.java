package btw.community.betterend;

import api.BTWAddon;
import api.config.AddonConfig;
import btw.community.betterend.entity.EntityEnderGuardian;
import btw.community.betterend.potion.PotionDragonCurse;
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
    public static int dragonEndlessBreathWitherDuration;
    public static int dragonFireBlockID;
    public static int potionDragonCurseID;
    public static PotionDragonCurse potionDragonCurse;
    public static int dragonBreathDuration;
    public static int dragonBreathFireDurationMin;
    public static int dragonBreathFireDurationMax;
    public static int dragonBreathFireBurnDuration;
    public static int dragonCurseDuration;
    public static double dragonCurseArmorMultiplier;
    public static int entityEnderGuardianID;
    public static int guardianArmorChanceWool;
    public static int guardianArmorChanceLeather;
    public static int guardianArmorChanceIron;
    public static int guardianArmorChanceChain;
    public static int guardianArmorChanceDiamond;
    public static int guardianArmorChanceSoulSteel;
    public static int guardianMaxHealth;
    public static int guardianAttackDamage;
    public static double guardianKnockbackResistance;
    public static double guardianMovementSpeed;
    public static double guardianFollowRange;

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

        if (net.minecraft.src.Potion.potionTypes[potionDragonCurseID] != null) {
            throw new RuntimeException("BetterEnd Error: Potion ID " + potionDragonCurseID + " is already in use!");
        }
        potionDragonCurse = new PotionDragonCurse(potionDragonCurseID);

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
        config.registerInt("Dragon.EndlessBreath.WitherDuration", 100, "Duration of Wither effect from Endless Fire in ticks (100 = 5s).");
        config.registerInt("IDs.DragonFire", 3001, "ID for the Dragon Fire block");
        config.registerInt("IDs.PotionDragonCurse", 24, "ID for Dragon Curse potion effect (Check for conflicts!)");
        config.registerInt("Dragon.DragonsBreath.Duration", 100, "Duration of Dragon's Breath attack in ticks (100 = 5s).");
        config.registerInt("Dragon.DragonsBreath.FireDurationMin", 15, "Min duration of Dragon Fire on ground (seconds).");
        config.registerInt("Dragon.DragonsBreath.FireDurationMax", 30, "Max duration of Dragon Fire on ground (seconds).");
        config.registerInt("Dragon.DragonsBreath.BurnDuration", 15, "How long player burns after touching Dragon Fire (seconds).");
        config.registerInt("Dragon.DragonsBreath.CurseDuration", 600, "Duration of Dragon Curse effect in ticks (600 = 30s).");
        config.registerDouble("Dragon.DragonsBreath.CurseArmorMultiplier", 2.0f, "Multiplier for armor damage when cursed.");
        config.registerInt("IDs.EntityEnderGuardian", 202, "Global Entity ID for Ender Guardian");
        config.registerInt("EnderGuardian.ArmorChance.Wool", 0, "Chance (0-100) for Ender Guardian to spawn with Wool armor.");
        config.registerInt("EnderGuardian.ArmorChance.Leather", 20, "Chance (0-100) for Ender Guardian to spawn with Leather armor.");
        config.registerInt("EnderGuardian.ArmorChance.Iron", 40, "Chance (0-100) for Ender Guardian to spawn with Iron armor.");
        config.registerInt("EnderGuardian.ArmorChance.Chain", 30, "Chance (0-100) for Ender Guardian to spawn with Chainmail armor.");
        config.registerInt("EnderGuardian.ArmorChance.Diamond", 0, "Chance (0-100) for Ender Guardian to spawn with Diamond armor.");
        config.registerInt("EnderGuardian.ArmorChance.SoulSteel", 0, "Chance (0-100) for Ender Guardian to spawn with Soulforged Steel armor.");
        config.registerInt("EnderGuardian.MaxHealth", 20, "Max health of Ender Guardian (20 = 10 hearts). Default Zombie: 20");
        config.registerInt("EnderGuardian.AttackDamage", 3, "Attack damage of Ender Guardian. Default Zombie: 3");
        config.registerDouble("EnderGuardian.KnockbackResistance", 0.01D, "Resistance to knockback (0.0 = none, 1.0 = full immunity). Default: 0.01");
        config.registerDouble("EnderGuardian.MovementSpeed", 0.23, "Movement speed of Ender Guardian. Default Zombie: 0.23");
        config.registerDouble("EnderGuardian.FollowRange", 16.0, "Range in blocks to detect player. Default Zombie: 16.0");
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
        dragonEndlessBreathWitherDuration = config.getInt("Dragon.EndlessBreath.WitherDuration");
        dragonFireBlockID = config.getInt("IDs.DragonFire");
        potionDragonCurseID = config.getInt("IDs.PotionDragonCurse");
        dragonBreathDuration = config.getInt("Dragon.DragonsBreath.Duration");
        dragonBreathFireDurationMin = config.getInt("Dragon.DragonsBreath.FireDurationMin");
        dragonBreathFireDurationMax = config.getInt("Dragon.DragonsBreath.FireDurationMax");
        dragonBreathFireBurnDuration = config.getInt("Dragon.DragonsBreath.BurnDuration");
        dragonCurseDuration = config.getInt("Dragon.DragonsBreath.CurseDuration");
        dragonCurseArmorMultiplier = config.getDouble("Dragon.DragonsBreath.CurseArmorMultiplier");
        entityEnderGuardianID = config.getInt("IDs.EntityEnderGuardian");
        guardianArmorChanceWool = config.getInt("EnderGuardian.ArmorChance.Wool");
        guardianArmorChanceLeather = config.getInt("EnderGuardian.ArmorChance.Leather");
        guardianArmorChanceIron = config.getInt("EnderGuardian.ArmorChance.Iron");
        guardianArmorChanceChain = config.getInt("EnderGuardian.ArmorChance.Chain");
        guardianArmorChanceDiamond = config.getInt("EnderGuardian.ArmorChance.Diamond");
        guardianArmorChanceSoulSteel = config.getInt("EnderGuardian.ArmorChance.SoulSteel");
        guardianMaxHealth = config.getInt("EnderGuardian.MaxHealth");
        guardianAttackDamage = config.getInt("EnderGuardian.AttackDamage");
        guardianKnockbackResistance = config.getDouble("EnderGuardian.KnockbackResistance");
        guardianMovementSpeed = config.getDouble("EnderGuardian.MovementSpeed");
        guardianFollowRange = config.getDouble("EnderGuardian.FollowRange");
    }

    private void registerEntity() {
        EntityList.addMapping(EntityEnderMite.class, "EnderMite", entityEnderMiteID, 0x152156, 0x69178d);
        EntityList.addMapping(EntityEnderGuardian.class, "EnderGuardian", entityEnderGuardianID, 0x000000, 0x6016A8);
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