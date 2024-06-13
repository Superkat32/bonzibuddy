package net.superkat.bonzibuddy.network.packets.minigame;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.MinigameHudData;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;

import java.util.UUID;

public class MinigameHudUpdateS2C implements CustomPayload {
    public static final CustomPayload.Id<MinigameHudUpdateS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "minigame_hud_update_s2c"));
    public static final PacketCodec<RegistryByteBuf, MinigameHudUpdateS2C> CODEC = CustomPayload.codecOf(MinigameHudUpdateS2C::write, MinigameHudUpdateS2C::new);

    public final UUID uuid;
    public final MinigameHudUpdateS2C.Action action;
    public final BonziMinigameType type;
    public final String name;
    public final int time;
    public final int wave;
    public final boolean onePlayerLeft;

    public MinigameHudUpdateS2C(MinigameHudData hudData, MinigameHudUpdateS2C.Action action) {
        this.uuid = hudData.uuid;
        this.action = action;
        this.type = hudData.type;
        this.name = hudData.name;
        this.time = hudData.time;
        this.wave = hudData.wave;
        this.onePlayerLeft = hudData.onePlayerLeft;
    }
    public MinigameHudUpdateS2C(RegistryByteBuf buf) {
        this.uuid = buf.readUuid();
        this.action = buf.readEnumConstant(MinigameHudUpdateS2C.Action.class);
        this.type = buf.readEnumConstant(BonziMinigameType.class);
        this.name = buf.readString();
        this.time = buf.readInt();
        this.wave = buf.readInt();
        this.onePlayerLeft = buf.readBoolean();
    }

    public void write(RegistryByteBuf buf) {
        buf.writeUuid(this.uuid);
        buf.writeEnumConstant(this.action);
        buf.writeEnumConstant(this.type);
        buf.writeString(this.name);
        buf.writeInt(this.time);
        buf.writeInt(this.wave);
        buf.writeBoolean(this.onePlayerLeft);
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
        UPDATE_TIME,
        UPDATE_WAVE,
        UPDATE_ONE_PLAYER_LEFT,
        REMOVE,
    }


}
