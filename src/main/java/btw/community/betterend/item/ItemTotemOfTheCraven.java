package btw.community.betterend.item;

import btw.community.betterend.BetterEndAddon;
import btw.community.betterend.client.TotemCravenSprite;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import java.util.Iterator;

public class ItemTotemOfTheCraven extends Item {
    public ItemTotemOfTheCraven(int id) {
        super(id);
        this.setUnlocalizedName("betterend.totem_craven");
        this.setCreativeTab(CreativeTabs.tabTransport);
        this.setMaxStackSize(1);
    }

    @Override
    public void registerIcons(IconRegister register) {
        TextureAtlasSprite customSprite = new TotemCravenSprite("betterend:totem_craven", "btw:corpse_eye");
        this.itemIcon = register.registerIcon("betterend:totem_craven", customSprite);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            long currentTime = world.getTotalWorldTime();
            long cooldownTicks = BetterEndAddon.totemCooldownSeconds * 20L;

            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("lastUsed")) {
                long lastUsed = stack.getTagCompound().getLong("lastUsed");
                long timeDiff = currentTime - lastUsed;
                if (timeDiff < cooldownTicks) {
                    long secondsLeft = (cooldownTicks - timeDiff) / 20L;
                    player.addChatMessage("Totem needs to recharge... (" + secondsLeft + "s)");
                    return stack;
                }
            }

            System.out.println("BetterEnd: Totem used by " + player.username);
            performTeleport(playerMP);

            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setLong("lastUsed", playerMP.worldObj.getTotalWorldTime());

            if (BetterEndAddon.isTotemSingleUse && !player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return stack;
    }

    private void performTeleport(EntityPlayerMP player) {
        MinecraftServer server = MinecraftServer.getServer();
        int oldDim = player.dimension;
        int newDim = 0;

        System.out.println("BetterEnd: Initiating teleport from Dim " + oldDim + " to " + newDim);
        player.worldObj.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.0F);

        WorldServer newWorld = server.worldServerForDimension(newDim);
        ChunkCoordinates spawn = newWorld.getSpawnPoint();

        if (oldDim != newDim) {
            ServerConfigurationManager configManager = server.getConfigurationManager();
            WorldServer oldWorld = server.worldServerForDimension(oldDim);

            player.dimension = newDim;
            player.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(
                    player.dimension,
                    (byte)newWorld.difficultySetting,
                    newWorld.getWorldInfo().getTerrainType(),
                    newWorld.getHeight(),
                    player.theItemInWorldManager.getGameType()
            ));

            oldWorld.removePlayerEntityDangerously(player);
            player.isDead = false;

            player.setLocationAndAngles(spawn.posX + 0.5, spawn.posY, spawn.posZ + 0.5, player.rotationYaw, player.rotationPitch);
            player.setWorld(newWorld);

            configManager.func_72375_a(player, oldWorld);

            newWorld.spawnEntityInWorld(player);
            newWorld.updateEntityWithOptionalForce(player, false);
            player.theItemInWorldManager.setWorld(newWorld);

            player.playerNetServerHandler.setPlayerLocation(
                    spawn.posX + 0.5,
                    spawn.posY,
                    spawn.posZ + 0.5,
                    player.rotationYaw,
                    player.rotationPitch
            );

            configManager.updateTimeAndWeatherForPlayer(player, newWorld);
            configManager.syncPlayerInventory(player);

            for (Object obj : player.getActivePotionEffects()) {
                PotionEffect effect = (PotionEffect) obj;
                player.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(player.entityId, effect));
            }
            player.timeOfLastDimensionSwitch = newWorld.getWorldTime();
        } else {
            player.playerNetServerHandler.setPlayerLocation(
                    spawn.posX + 0.5,
                    spawn.posY,
                    spawn.posZ + 0.5,
                    player.rotationYaw,
                    player.rotationPitch
            );
        }

        player.fallDistance = 0.0F;
        player.worldObj.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.0F);
        player.addChatMessage("You ran away like a coward!");
        System.out.println("BetterEnd: Teleport sequence finished successfully.");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, java.util.List list, boolean par4) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("lastUsed")) {
            long lastUsed = stack.getTagCompound().getLong("lastUsed");
            long timeDiff = player.worldObj.getTotalWorldTime() - lastUsed;
            long cooldownTicks = BetterEndAddon.totemCooldownSeconds * 20L;
            if (timeDiff < cooldownTicks) {
                long secondsLeft = (cooldownTicks - timeDiff) / 20L;
                list.add("Cooldown: " + secondsLeft + "s");
            } else {
                list.add("Ready to use");
            }
        }
    }
}