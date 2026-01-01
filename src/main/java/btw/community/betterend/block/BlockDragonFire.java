package btw.community.betterend.block;

import btw.client.texture.DragonFireTexture;
import btw.community.betterend.BetterEndAddon;
import net.minecraft.src.*;

import java.util.Random;

public class BlockDragonFire extends BlockFire {

    private Icon[] iconArray;

    public BlockDragonFire(int id) {
        super(id);
        this.setUnlocalizedName("betterend.dragon_fire");
        this.setLightValue(0.66F);
        this.setTickRandomly(false);
        this.setHardness(0.0F);
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            living.setFire(BetterEndAddon.dragonBreathFireBurnDuration);
            if (!living.isPotionActive(BetterEndAddon.potionDragonCurse)) {
                living.addPotionEffect(new PotionEffect(
                        BetterEndAddon.potionDragonCurseID,
                        BetterEndAddon.dragonCurseDuration
                ));
            }
            entity.attackEntityFrom(DamageSource.inFire, 1.0F);
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        scheduleExtinguish(world, x, y, z);
    }

    private void scheduleExtinguish(World world, int x, int y, int z) {
        if (!world.isRemote) {
            int minTicks = BetterEndAddon.dragonBreathFireDurationMin * 20;
            int maxTicks = BetterEndAddon.dragonBreathFireDurationMax * 20;
            int duration = minTicks + world.rand.nextInt(maxTicks - minTicks + 1);
            world.scheduleBlockUpdate(x, y, z, this.blockID, duration);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        world.setBlockToAir(x, y, z);
    }

    @Override
    public void registerIcons(IconRegister register) {
        if (register instanceof TextureMap) {
            TextureMap map = (TextureMap) register;
            this.iconArray = new Icon[2];

            String name0 = "betterend:dragon_fire_layer_0";
            String name1 = "betterend:dragon_fire_layer_1";

            TextureAtlasSprite texture0 = new DragonFireTexture(name0, 0);
            TextureAtlasSprite texture1 = new DragonFireTexture(name1, 1);

            this.iconArray[0] = map.registerIcon(name0, texture0);
            this.iconArray[1] = map.registerIcon(name1, texture1);

            this.blockIcon = this.iconArray[0];
        } else {
            super.registerIcons(register);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return this.iconArray != null ? this.iconArray[0] : super.getIcon(side, meta);
    }

    @Override
    public Icon getFireIcon(int layer) {
        if (this.iconArray != null && layer >= 0 && layer < this.iconArray.length) {
            return this.iconArray[layer];
        }
        return this.blockIcon;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(24) == 0) {
            world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "fire.fire", 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }

        for (int i = 0; i < 3; ++i) {
            double px = (double)x + rand.nextDouble();
            double py = (double)y + rand.nextDouble() * 0.5D + 0.5D;
            double pz = (double)z + rand.nextDouble();
            world.spawnParticle("mobSpell", px, py, pz, 0.5D, 0.0D, 1.0D);
        }

        if (rand.nextInt(3) == 0) {
            double px = (double)x + rand.nextDouble();
            double py = (double)y + rand.nextDouble() * 0.5D + 0.5D;
            double pz = (double)z + rand.nextDouble();
            world.spawnParticle("largesmoke", px, py, pz, 0.0D, 0.0D, 0.0D);
        }
    }
}