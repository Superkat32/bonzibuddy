package net.superkat.bonzibuddy.minigame;

import net.minecraft.util.math.MathHelper;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.minigame.MinigameHudUpdateS2C;

import java.util.UUID;

/**
 * A common class(client and server) which contains data which will be rendered by the client. This class is not to contain any client-specific code, as it WILL be loaded and used by the server!
 * @see net.superkat.bonzibuddy.rendering.hud.MinigameHudRenderer
 */
public class MinigameHudData {
    public final UUID uuid;
    public BonziMinigameType type;
    public String name;
    public int time;
    public int wave;
    public boolean onePlayerLeft;

    public MinigameHudData(UUID uuid, BonziMinigameType type, String name, int time, int wave, boolean onePlayerLeft) {
        this.uuid = uuid;
        this.type = type;
        this.name = name;
        this.time = time;
        this.wave = wave;
        this.onePlayerLeft = onePlayerLeft;
    }
    public MinigameHudData(MinigameHudUpdateS2C packet) {
        this.uuid = packet.uuid;
        this.type = packet.type;
        this.name = packet.name;
        this.time = packet.time;
        this.wave = packet.wave;
        this.onePlayerLeft = packet.onePlayerLeft;
    }
    public MinigameHudData(BonziMinigameType type, String name) {
        this.uuid = MathHelper.randomUuid();
        this.type = type;
        this.name = name;
        this.time = 0;
        this.wave = 0;
        this.onePlayerLeft= false;
    }
    public MinigameHudData() {
        this.uuid = MathHelper.randomUuid();
        this.type = BonziMinigameType.ABSTRACT;
        this.name = "Bonzi Minigame";
        this.time = 0;
        this.wave = 0;
        this.onePlayerLeft = false;
    }

    public void setType(BonziMinigameType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public void setOnePlayerLeft(boolean onePlayerLeft) {
        this.onePlayerLeft = onePlayerLeft;
    }

}
