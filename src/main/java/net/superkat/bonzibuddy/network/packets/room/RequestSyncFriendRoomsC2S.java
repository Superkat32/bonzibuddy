package net.superkat.bonzibuddy.network.packets.room;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public record RequestSyncFriendRoomsC2S() implements CustomPayload {
    public static final CustomPayload.Id<RequestSyncFriendRoomsC2S> ID = new CustomPayload.Id<>(Identifier.of(BonziBUDDY.MOD_ID, "request_sync_friend_rooms_c2s"));
    public static final PacketCodec<RegistryByteBuf, RequestSyncFriendRoomsC2S> CODEC = CustomPayload.codecOf(RequestSyncFriendRoomsC2S::write, RequestSyncFriendRoomsC2S::new);

    public RequestSyncFriendRoomsC2S(RegistryByteBuf buf) {
        this();
    }

    public void write(RegistryByteBuf buf) {
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
