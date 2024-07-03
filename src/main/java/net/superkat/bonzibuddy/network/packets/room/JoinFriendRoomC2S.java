package net.superkat.bonzibuddy.network.packets.room;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

import java.util.UUID;

public record JoinFriendRoomC2S(UUID roomUuid) implements CustomPayload {
    public static final CustomPayload.Id<JoinFriendRoomC2S> ID = new CustomPayload.Id<>(Identifier.of(BonziBUDDY.MOD_ID, "join_friend_room_c2s"));
    public static final PacketCodec<RegistryByteBuf, JoinFriendRoomC2S> CODEC = CustomPayload.codecOf(JoinFriendRoomC2S::write, JoinFriendRoomC2S::new);

    public JoinFriendRoomC2S(RegistryByteBuf buf) {
        this(buf.readUuid());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeUuid(this.roomUuid);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
