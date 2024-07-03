package net.superkat.bonzibuddy.network.packets.room;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

import java.util.UUID;

public record LeaveFriendRoomC2S(UUID roomUuid) implements CustomPayload {
    public static final CustomPayload.Id<LeaveFriendRoomC2S> ID = new CustomPayload.Id<>(Identifier.of(BonziBUDDY.MOD_ID, "leave_friend_room_c2s"));
    public static final PacketCodec<RegistryByteBuf, LeaveFriendRoomC2S> CODEC = CustomPayload.codecOf(LeaveFriendRoomC2S::write, LeaveFriendRoomC2S::new);

    public LeaveFriendRoomC2S(RegistryByteBuf buf) {
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
