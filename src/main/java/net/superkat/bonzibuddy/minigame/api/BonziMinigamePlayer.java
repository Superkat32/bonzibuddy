package net.superkat.bonzibuddy.minigame.api;

public interface BonziMinigamePlayer {
    boolean bonzibuddy$respawningFromMinigame();
    boolean bonzibuddy$inMinigame();
    void bonzibuddy$setRespawningFromMinigame(boolean respawning);
    void bonzibuddy$setInMinigame(boolean inMinigame);
}
