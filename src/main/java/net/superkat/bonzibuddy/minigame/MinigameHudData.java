package net.superkat.bonzibuddy.minigame;

import net.minecraft.util.math.MathHelper;

import java.util.UUID;

/**
 * A common class(client and server) which contains data which will be rendered by the client. This class is not to contain any client-specific code, as it WILL be loaded and used by the server!
 */
public class MinigameHudData {
    public final UUID uuid;
    public int time;

    public MinigameHudData(UUID uuid, int time) {
        this.uuid = uuid;
        this.time = time;
    }

    public MinigameHudData() {
        this.uuid = MathHelper.randomUuid();
    }

    public void setTime(int time) {
        this.time = time;
    }
}
