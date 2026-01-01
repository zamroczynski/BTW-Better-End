package btw.community.betterend.block;

import btw.community.betterend.BetterEndAddon;
import net.minecraft.src.*;

import java.util.Random;

public class BlockDragonFire extends Block {

    public BlockDragonFire(int id) {
        super(id, Material.vine);
        this.setUnlocalizedName("betterend.dragon_fire");
        this.setLightValue(0.66F);
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
        return 3;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote) {
            if (entity instanceof EntityItem) {
                entity.setDead();
                return;
            }

            if (entity instanceof EntityLivingBase) {
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
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        scheduleExtinguish(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID) {
        if (!world.doesBlockHaveSolidTopSurface(x, y - 1, z)) {
            world.setBlockToAir(x, y, z);
        }
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
    }

    @Override
    public void registerIcons(IconRegister register) {
        // Musimy rzutować na TextureMap, aby użyć dedykowanej metody rejestracji ze Sprite'm
        if (register instanceof TextureMap) {
            TextureMap map = (TextureMap) register;
            String name = "betterend:dragon_fire";

            // Tworzymy instancję naszej proceduralnej tekstury
            TextureAtlasSprite texture = new btw.client.texture.DragonFireTexture(name);

            // Rejestrujemy ją w mapie tekstur bloków używając poprawnej metody
            // Ta metoda dodaje wpis do mapRegisteredSprites i zwraca Icon
            this.blockIcon = map.registerIcon(name, texture);
        } else {
            // Fallback: Rejestracja standardowa (szukanie pliku .png)
            // To się nie powinno zdarzyć w normalnym cyklu ładowania bloków
            super.registerIcons(register);
        }
    }
}