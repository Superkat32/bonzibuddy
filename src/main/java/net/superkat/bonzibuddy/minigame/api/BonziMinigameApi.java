package net.superkat.bonzibuddy.minigame.api;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.BonziCatastrophicClonesMinigame;
import net.superkat.bonzibuddy.minigame.BonziMinigame;
import net.superkat.bonzibuddy.minigame.BonziMinigameManager;

import java.util.List;

/**
 * Class to be called by all minigames and outside code for common actions, such as starting a minigame.
 *
 * @see net.superkat.bonzibuddy.minigame.BonziMinigameManager
 * @see BonziMinigame
 */
public class BonziMinigameApi {

    /**
     * Start a Bonzi Minigame of a specific type at a specific position.
     *
     * @param minigame The minigame type to start.
     * @param world The world to start in.
     * @param startingPos The minigame's starting position.
     */
    public static void startBonziMinigame(BonziMinigameType minigame, ServerWorld world, BlockPos startingPos) {
        BonziMinigame bonziMinigame = createBonziMinigame(minigame, world, startingPos);
        BonziBUDDY.LOGGER.info("Starting new Bonzi Minigame");
        bonziMinigame.start();
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
