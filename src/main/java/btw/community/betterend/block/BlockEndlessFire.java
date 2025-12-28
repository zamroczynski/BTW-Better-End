package btw.community.betterend.block;

import btw.community.betterend.BetterEndAddon;
import net.minecraft.src.*;

import java.util.Random;

public class BlockEndlessFire extends Block {

    public BlockEndlessFire(int id) {
        super(id, Material.circuits);

        this.setUnlocalizedName("betterend.endless_fire");
        this.setLightValue(0.5F);
        this.setTickRandomly(true);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        scheduleExtinguish(world, x, y, z);
    }

    private void scheduleExtinguish(World world, int x, int y, int z) {
        if (!world.isRemote) {
            int minSeconds = BetterEndAddon.dragonEndlessFireDurationMin;
            int maxSeconds = BetterEndAddon.dragonEndlessFireDurationMax;

            int minTicks = minSeconds * 20;
            int maxTicks = maxSeconds * 20;

            int duration = minTicks + world.rand.nextInt(maxTicks - minTicks + 1);

            world.scheduleBlockUpdate(x, y, z, this.blockID, duration);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        world.setBlockToAir(x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            if (!player.capabilities.isCreativeMode) {
                int strength = BetterEndAddon.dragonEndlessBreathWitherStrength;
                player.addPotionEffect(new PotionEffect(Potion.wither.id, 100, strength));
            }
        }
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        for (int i = 0; i < 3; ++i) {
            double px = (double)x + rand.nextDouble();
            double py = (double)y + rand.nextDouble() * 0.5D + 0.25D;
            double pz = (double)z + rand.nextDouble();
            double vx = (rand.nextDouble() - 0.5D) * 0.5D;
            double vy = (rand.nextDouble() - 0.5D) * 0.5D;
            double vz = (rand.nextDouble() - 0.5D) * 0.5D;

            world.spawnParticle("portal", px, py, pz, vx, vy, vz);
        }
    }

    @Override
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("fire_layer_0");
    }
}