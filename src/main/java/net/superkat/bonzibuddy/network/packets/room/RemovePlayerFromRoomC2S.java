package net.superkat.bonzibuddy.network.packets.room;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

import java.util.UUID;

public record RemovePlayerFromRoomC2S(UUID roomUuid, UUID player) implements CustomPayload {
    public static final CustomPayload.Id<RemovePlayerFromRoomC2S> ID = new CustomPayload.Id<>(Identifier.of(BonziBUDDY.MOD_ID, "remove_player_from_room_c2s"));
    public static final PacketCodec<RegistryByteBuf, RemovePlayerFromRoomC2S> CODEC = CustomPayload.codecOf(RemovePlayerFromRoomC2S::write, RemovePlayerFromRoomC2S::new);

    public RemovePlayerFromRoomC2S(RegistryByteBuf buf) {
        this(buf.readUuid(), buf.readUuid());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeUuid(this.roomUuid);
        buf.writeUuid(this.player);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
