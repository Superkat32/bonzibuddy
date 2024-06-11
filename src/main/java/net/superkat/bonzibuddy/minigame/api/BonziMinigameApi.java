package net.superkat.bonzibuddy.minigame.api;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
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

    public static void startBonziMinigame(BonziMinigameType minigame, ServerWorld world, BlockPos startingPos) {
        BonziMinigame bonziMinigame = createBonziMinigame(minigame, world, startingPos);
        BonziBUDDY.LOGGER.info("Starting new Bonzi Minigame");
        bonziMinigame.start();
    }

    public static BonziMinigame createBonziMinigame(BonziMinigameType minigame, ServerWorld world, BlockPos startingPos) {
        BonziMinigameManager minigameManager = getMinigameManager(world);
        return minigameManager.createNewBonziMinigame(minigame, startingPos);
    }

    public static BonziMinigame createMinigameFromType(BonziMinigameType minigameType, int id, ServerWorld world, BlockPos startingPos) {
        BonziMinigame bonziMinigame;
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

    public static BonziMinigameManager getMinigameManager(ServerWorld world) {
        return ((BonziMinigameWorld) world).bonzibuddy$getMinigameManager();
    }

    public static void teleportPlayersToRespawn(List<ServerPlayerEntity> players) {

    }

}
