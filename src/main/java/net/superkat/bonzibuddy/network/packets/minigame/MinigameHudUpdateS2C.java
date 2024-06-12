package net.superkat.bonzibuddy.network.packets.minigame;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.MinigameHudData;

import java.util.UUID;

public class MinigameHudUpdateS2C implements CustomPayload {
    public static final CustomPayload.Id<MinigameHudUpdateS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "minigame_timer_update_s2c"));
    public static final PacketCodec<RegistryByteBuf, MinigameHudUpdateS2C> CODEC = CustomPayload.codecOf(MinigameHudUpdateS2C::write, MinigameHudUpdateS2C::new);

    private final UUID uuid;
    private final MinigameHudUpdateS2C.Action action;
    private final int time;

    public MinigameHudUpdateS2C(MinigameHudData hudData, MinigameHudUpdateS2C.Action action) {
        this.uuid = hudData.uuid;
        this.action = action;
        this.time = hudData.time;

    }
    public MinigameHudUpdateS2C(RegistryByteBuf buf) {
        this.uuid = buf.readUuid();
        this.action = buf.readEnumConstant(MinigameHudUpdateS2C.Action.class);
        this.time = buf.readInt();
    }

    public void write(RegistryByteBuf buf) {
        buf.writeUuid(this.uuid);
        buf.writeEnumConstant(this.action);
        buf.writeInt(this.time);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Action getAction() {
        return action;
    }

    public int getTime() {
        return time;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    /**
     * This entire system is based on Minecraft's BossBarS2CPacket implementation, though on a less complex scale. Minecraft's system, while nicer in implementation, is really just an over-engineered switch statement in my honest opinion.
     */
    public static enum Action {
        ADD,
        UPDATE_TIMER,
        REMOVE;
    }


}
