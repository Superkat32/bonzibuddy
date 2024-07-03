package net.superkat.bonzibuddy.network.packets.room;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

import java.util.UUID;

public record OnFriendRoomJoinS2C(UUID roomUuid) implements CustomPayload {
    public static final CustomPayload.Id<OnFriendRoomJoinS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "on_room_join_s2c"));
    public static final PacketCodec<RegistryByteBuf, OnFriendRoomJoinS2C> CODEC = CustomPayload.codecOf(OnFriendRoomJoinS2C::write, OnFriendRoomJoinS2C::new);

    public OnFriendRoomJoinS2C(RegistryByteBuf buf) {
        this(buf.readUuid());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeUuid(roomUuid);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
