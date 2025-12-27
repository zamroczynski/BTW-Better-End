package btw.community.betterend.item;

import btw.community.betterend.BetterEndAddon;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class ItemTotemOfTheCraven extends Item {

    public ItemTotemOfTheCraven(int id) {
        super(id);
        this.setUnlocalizedName("betterend.totem_craven");
        this.setCreativeTab(CreativeTabs.tabTransport);
        this.setTextureName("betterend:totem_craven");
        this.setMaxStackSize(1);
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

            performTeleport(playerMP);

            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setLong("lastUsed", currentTime);

            if (BetterEndAddon.isTotemSingleUse && !player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }

        return stack;
    }

    private void performTeleport(EntityPlayerMP player) {
        MinecraftServer server = MinecraftServer.getServer();

        player.worldObj.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.0F);

        if (player.dimension != 0) {
            server.getConfigurationManager().transferPlayerToDimension(player, 0);
        }

        WorldServer overworld = server.worldServerForDimension(0);
        ChunkCoordinates spawn = overworld.getSpawnPoint();

        player.playerNetServerHandler.setPlayerLocation(
                spawn.posX + 0.5,
                spawn.posY,
                spawn.posZ + 0.5,
                player.rotationYaw,
                player.rotationPitch
        );

        player.fallDistance = 0.0F;

        player.worldObj.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.0F);

        player.addChatMessage("You have fled to safety!");
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