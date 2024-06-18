package net.superkat.bonzibuddy.minigame.api;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnLocation;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.BonziBuddyEntities;
import net.superkat.bonzibuddy.minigame.BonziCatastrophicClonesMinigame;
import net.superkat.bonzibuddy.minigame.BonziMinigame;
import net.superkat.bonzibuddy.minigame.BonziMinigameManager;
import net.superkat.bonzibuddy.minigame.TripleChaosMinigame;

import java.util.List;

/**
 * Class to be called by all minigames and outside code for common actions, such as starting a minigame.
 *
 * @see net.superkat.bonzibuddy.minigame.BonziMinigameManager
 * @see BonziMinigame
 */
public class BonziMinigameApi {

    //The structure spacing times 16
    public static final int STRUCTURE_SPACING = 256;
    public static final int STRUCTURE_PLATFORM_Y = 37;
    public static final int STRUCTURE_SPAWN_Y = STRUCTURE_PLATFORM_Y + 3;

    /**
     * Start a Bonzi Minigame of a specific type at a specific position.
     *
     * @param minigame The minigame type to start.
     * @param world The world to start in.
     * @param startingPos The minigame's starting position.
     *
     * @return The started Bonzi Minigame.
     */
    public static BonziMinigame startBonziMinigame(BonziMinigameType minigame, ServerWorld world, BlockPos startingPos) {
        BonziMinigame bonziMinigame = createBonziMinigame(minigame, world, startingPos);
        BonziBUDDY.LOGGER.info("Starting Bonzi Minigame " + bonziMinigame.getId() + " (" + bonziMinigame.getMinigameType().getName() + ")!");
        bonziMinigame.start();
        return bonziMinigame;
    }

    /**
     * Create a new Bonzi Minigame. This method does NOT start it.
     *
     * @param minigame The minigame type to create.
     * @param world The world to create the minigame in.
     * @param startingPos The minigame's starting position.
     * @return The newly created minigame.
     */
    public static BonziMinigame createBonziMinigame(BonziMinigameType minigame, ServerWorld world, BlockPos startingPos) {
        BonziMinigameManager minigameManager = getMinigameManager(world);
        return minigameManager.createNewBonziMinigame(minigame, startingPos);
    }

    public static BonziMinigame createMinigameFromType(BonziMinigameType minigameType, int id, ServerWorld world, BlockPos startingPos) {
        BonziMinigame bonziMinigame;
        //I wonder if having a register method would work better than this, but I don't exactly know how you'd do that.
        switch (minigameType) {
            default -> bonziMinigame = new BonziMinigame(id, world, startingPos);
            case CATASTROPHIC_CLONES -> bonziMinigame = new BonziCatastrophicClonesMinigame(id, world, startingPos);
            case TRIPLE_CHAOS -> bonziMinigame = new TripleChaosMinigame(id, world, startingPos);
        }
        return bonziMinigame;
    }

    public static BonziMinigame createMinigameFromType(BonziMinigameType minigameType, ServerWorld world, NbtCompound nbt) {
        BonziMinigame bonziMinigame;
        switch (minigameType) {
            default -> bonziMinigame = new BonziMinigame(world, nbt);
            case CATASTROPHIC_CLONES -> bonziMinigame = new BonziCatastrophicClonesMinigame(world, nbt);
        }
        return bonziMinigame;
    }

    public static BonziMinigame getMinigameById(ServerWorld world, int id) {
        BonziMinigameManager minigameManager = getMinigameManager(world);
        return minigameManager.getMinigameById(id);
    }

    public static List<BonziMinigame> getAllMinigames(ServerWorld world) {
        BonziMinigameManager minigameManager = getMinigameManager(world);
        return minigameManager.getAllMinigames();
    }

    public static void removeBonziMinigame(BonziMinigame minigame) {
        minigame.invalidate();
    }

    public static boolean removeAllMinigames(ServerWorld world) {
        BonziMinigameManager minigameManager = getMinigameManager(world);
        List<BonziMinigame> minigames = minigameManager.getAllMinigames();
        if(minigames != null && !minigames.isEmpty()) {
            minigames.forEach(BonziMinigameApi::removeBonziMinigame);
            return true;
        }
        return false;
   }

   public static void clearAnyEntities(ServerWorld world, BlockPos pos, int radius) {
       List<? extends LivingEntity> enemies = world.getEntitiesByType(BonziBuddyEntities.BONZI_CLONE, entity -> entity.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) <= radius * radius);
       enemies.forEach(LivingEntity::discard);

       List<? extends LivingEntity> protectBonziEntities = world.getEntitiesByType(BonziBuddyEntities.PROTECTABLE_BONZI_BUDDY, entity -> entity.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) <= radius * radius);
       protectBonziEntities.forEach(LivingEntity::discard);
   }

    public static BlockPos getEnemySpawnPos(ServerWorld world, BlockPos minigamePos, int proximity, int tries) {
        //confusion - stolen from Raid
        int i = proximity == 0 ? 2 : 2 - proximity;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        SpawnLocation spawnLocation = SpawnRestriction.getLocation(EntityType.RAVAGER);
        float radius = 20;

        for(int j = 0; j < tries; ++j) {
            float f = world.random.nextFloat() * (float) (Math.PI * 2);
            int k = minigamePos.getX() + MathHelper.floor(MathHelper.cos(f) * radius * (float)i) + world.random.nextInt(5);
            int l = minigamePos.getZ() + MathHelper.floor(MathHelper.sin(f) * radius * (float)i) + world.random.nextInt(5);
            int m = world.getTopY(Heightmap.Type.WORLD_SURFACE, k, l);
            mutable.set(k, m, l);
            if (!world.isNearOccupiedPointOfInterest(mutable) || proximity >= 2) {
                int n = 10;
                if (world.isRegionLoaded(mutable.getX() - n, mutable.getZ() - n, mutable.getX() + n, mutable.getZ() + n)
                        && world.shouldTickEntity(mutable)
                        && (
                        spawnLocation.isSpawnPositionOk(world, mutable, EntityType.RAVAGER)
                                || world.getBlockState(mutable.down()).isOf(Blocks.SNOW) && world.getBlockState(mutable).isAir()
                )) {
                    return mutable.add(0, 1, 0);
                }
            }
        }
        return null;
    }

//    /**
//     * Force load chunks around a Bonzi Minigame. Helpful for removing entities if the area has been unloaded.
//     *
//     * @param world The world to force load the chunks in.
//     * @param minigameStartPos The minigame's starting position.
//     * @param radius The square(I think?) radius of chunks to load.
//     * @param forceload Should the chunks be forced loaded?
//     */
//    public static void forceloadMinigameChunks(ServerWorld world, BlockPos minigameStartPos, int radius, boolean forceload) {
//        radius *= 16;
//        ColumnPos from = new ColumnPos(minigameStartPos.getX() + radius, minigameStartPos.getZ() + radius);
//        ColumnPos to = new ColumnPos(minigameStartPos.getX() - radius, minigameStartPos.getZ() - radius);
//        confusion(world, from, to, forceload);
//    }
//
//
//    /**
//     * <s>Stolen</s> Borrowed from {@link net.minecraft.server.command.ForceLoadCommand}. I have no clue how it works.
//     */
//    private static void confusion(ServerWorld world, ColumnPos from, ColumnPos to, boolean forceLoaded) {
//        int i = Math.min(from.x(), to.x());
//        int j = Math.min(from.z(), to.z());
//        int k = Math.max(from.x(), to.x());
//        int l = Math.max(from.z(), to.z());
//        if (i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000) {
//            int m = ChunkSectionPos.getSectionCoord(i);
//            int n = ChunkSectionPos.getSectionCoord(j);
//            int o = ChunkSectionPos.getSectionCoord(k);
//            int p = ChunkSectionPos.getSectionCoord(l);
//            long q = ((long)(o - m) + 1L) * ((long)(p - n) + 1L);
//            if (q > 256L) {
//                BonziBUDDY.LOGGER.warn("Attempted chunks to force load were too big! This may cause entities to still be around...");
//            } else {
//                ChunkPos chunkPos = null;
//                int chunksLoaded = 0;
//
//                for(int s = m; s <= o; ++s) {
//                    for(int t = n; t <= p; ++t) {
//                        boolean bl = world.setChunkForced(s, t, forceLoaded);
//                        if (bl) {
//                            chunksLoaded++;
//                            if (chunkPos == null) {
//                                chunkPos = new ChunkPos(s, t);
//                            }
//                        }
//                    }
//                }
//
//                if(forceLoaded) {
//                    BonziBUDDY.LOGGER.info("Forced loaded " + chunksLoaded + " chunks because of a minigame's invalidation.");
//                } else {
//                    BonziBUDDY.LOGGER.info("Unloaded " + chunksLoaded + " chunks because of a minigame's invalidation.");
//                }
//            }
//        }
//    }

    public static BlockPos getAvailableMinigameBlockpos(ServerWorld world) {
        BonziMinigameManager minigameManager = getMinigameManager(world);
        int totalMinigames = minigameManager.getAllMinigames().size();
        if(totalMinigames == 0) {
            return findStructureCenterOffset(world, 0, STRUCTURE_PLATFORM_Y, 0);
        } else {
            return findStructureCenterOffset(world, totalMinigames * STRUCTURE_SPACING, STRUCTURE_PLATFORM_Y, 0);
        }
    }

    private static BlockPos findStructureCenterOffset(ServerWorld world, int x, int y, int z) {
        BlockPos searchPos = new BlockPos(x, y, z);
        int s = 25; //search
        if(!world.getBlockState(searchPos.add(0, 0, s)).isAir()) {
            return new BlockPos(searchPos).add(0, 0, s);
        } else if (!world.getBlockState(searchPos.add(0, 0, -s)).isAir()) {
            return new BlockPos(searchPos).add(0, 0, -s);
        } else if (!world.getBlockState(searchPos.add(-s, 0, 0)).isAir()) {
            return new BlockPos(searchPos).add(-s, 0, 0);
        } else if(!world.getBlockState(searchPos.add(s, 0, 0)).isAir()) {
            return new BlockPos(searchPos).add(s, 0, 0);
        } else {
            BonziBUDDY.LOGGER.warn("Couldn't find the center of the structure! Preforming detailed search...");
            return searchPos;
        }
    }

    public static void teleportPlayersToMinigame(BonziMinigame minigame, List<ServerPlayerEntity> players) {
        ServerWorld world = minigame.getWorld();
        BlockPos minigameStartPos = minigame.getStartPos();
        players.forEach(player ->  {
            player.teleport(world, minigameStartPos.getX(), minigameStartPos.getY() + 3, minigameStartPos.getZ(), 0f, 0f);
        });
    }

    /**
     * Get the world's BonziMinigameManager.
     *
     * @param world The world to get from.
     * @return The world's BonziMinigameManager.
     */
    public static BonziMinigameManager getMinigameManager(ServerWorld world) {
        return ((BonziMinigameWorld) world).bonzibuddy$getMinigameManager();
    }

    public static void teleportPlayersToRespawn(List<ServerPlayerEntity> players) {
        players.forEach(player -> {
            TeleportTarget teleportTarget = player.getRespawnTarget(false, TeleportTarget.NO_OP);
            player.teleportTo(teleportTarget);
        });
    }

}
