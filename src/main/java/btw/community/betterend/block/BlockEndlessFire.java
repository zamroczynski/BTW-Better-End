package btw.community.betterend.block;

import btw.community.betterend.BetterEndAddon;
import net.minecraft.src.*;
import java.util.Random;

public class BlockEndlessFire extends Block {
    public BlockEndlessFire(int id) {
        super(id, Material.vine);
        this.setUnlocalizedName("betterend.endless_fire");
        this.setLightValue(0.5F);
        this.setTickRandomly(true);
        this.setHardness(-1.0F);
    }

    @Override
    public int idDropped(int metadata, Random rand, int fortune) {
        return 0;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;
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
                int duration = BetterEndAddon.dragonEndlessBreathWitherDuration;
                player.addPotionEffect(new PotionEffect(Potion.wither.id, duration, strength));
            }
        }
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        for (int i = 0; i < 12; ++i) {
            double px = (double)x + rand.nextDouble();
            double py = (double)y + rand.nextDouble() * 0.5D + 0.25D;
            double pz = (double)z + rand.nextDouble();
            world.spawnParticle("portal", px, py, pz, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void registerIcons(IconRegister register) {
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        scheduleExtinguish(world, x, y, z);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return Block.stone.getIcon(side, meta);
    }

    private void scheduleExtinguish(World world, int x, int y, int z) {
        if (!world.isRemote) {
            int minTicks = BetterEndAddon.dragonEndlessFireDurationMin * 20;
            int maxTicks = BetterEndAddon.dragonEndlessFireDurationMax * 20;
            int duration = minTicks + world.rand.nextInt(maxTicks - minTicks + 1);
            world.scheduleBlockUpdate(x, y, z, this.blockID, duration);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        world.setBlockToAir(x, y, z);
    }
}