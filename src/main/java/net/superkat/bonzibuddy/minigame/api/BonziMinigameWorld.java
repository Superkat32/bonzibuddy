package net.superkat.bonzibuddy.minigame.api;

import net.superkat.bonzibuddy.minigame.BonziMinigameManager;

/**
 * Duck interface for Bonzi Minigames.
 */
public interface BonziMinigameWorld {
    /**
     * Get the ServerWorld's current BonziMinigameManager
     *
     * @return The world's current BonziMinigameManager
     */
    BonziMinigameManager bonzibuddy$getMinigameManager();
}
