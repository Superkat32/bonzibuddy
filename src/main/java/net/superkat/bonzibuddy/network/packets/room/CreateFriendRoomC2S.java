package net.superkat.bonzibuddy.network.packets.room;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public record CreateFriendRoomC2S() implements CustomPayload {
    public static final CustomPayload.Id<CreateFriendRoomC2S> ID = new CustomPayload.Id<>(Identifier.of(BonziBUDDY.MOD_ID, "create_friend_room_c2s"));
    public static final PacketCodec<RegistryByteBuf, CreateFriendRoomC2S> CODEC = CustomPayload.codecOf(CreateFriendRoomC2S::write, CreateFriendRoomC2S::new);

    public CreateFriendRoomC2S(RegistryByteBuf buf) {
        this();
    }

    public void write(RegistryByteBuf buf) {

    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
