package net.superkat.bonzibuddy.network.packets.room;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public record CreatedFriendRoomS2C() implements CustomPayload {
    public static final CustomPayload.Id<CreatedFriendRoomS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "created_friend_room_s2c"));
    public static final PacketCodec<RegistryByteBuf, CreatedFriendRoomS2C> CODEC = CustomPayload.codecOf(CreatedFriendRoomS2C::write, CreatedFriendRoomS2C::new);

    public CreatedFriendRoomS2C(RegistryByteBuf buf) {
        this();
    }

    public void write(RegistryByteBuf buf) {

    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
