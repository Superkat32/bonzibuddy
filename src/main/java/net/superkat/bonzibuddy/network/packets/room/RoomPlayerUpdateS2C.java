package net.superkat.bonzibuddy.network.packets.room;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

import java.util.UUID;

public record RoomPlayerUpdateS2C(UUID roomUuid, UUID playerUpdated, boolean playerJoined) implements CustomPayload {
    public static final CustomPayload.Id<RoomPlayerUpdateS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "room_player_update_s2c"));
    public static final PacketCodec<RegistryByteBuf, RoomPlayerUpdateS2C> CODEC = CustomPayload.codecOf(RoomPlayerUpdateS2C::write, RoomPlayerUpdateS2C::new);

    public RoomPlayerUpdateS2C(RegistryByteBuf buf) {
        this(buf.readUuid(), buf.readUuid(), buf.readBoolean());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeUuid(roomUuid);
        buf.writeUuid(playerUpdated);
        buf.writeBoolean(playerJoined);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
