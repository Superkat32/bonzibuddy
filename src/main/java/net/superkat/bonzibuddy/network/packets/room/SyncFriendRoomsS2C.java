package net.superkat.bonzibuddy.network.packets.room;

import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.room.FriendRoom;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SyncFriendRoomsS2C implements CustomPayload {
    public static final CustomPayload.Id<SyncFriendRoomsS2C> ID = new Id<>(Identifier.of(BonziBUDDY.MOD_ID, "sync_friend_rooms_s2c"));
    public static final PacketCodec<RegistryByteBuf, SyncFriendRoomsS2C> CODEC = CustomPayload.codecOf(SyncFriendRoomsS2C::write, SyncFriendRoomsS2C::new);

    public final List<FriendRoom> rooms;
//    public final UUID[] rooms;

    public SyncFriendRoomsS2C(List<FriendRoom> rooms) {
        this.rooms = rooms;
//        this.rooms = rooms.stream().map(FriendRoom::getHostUuid).distinct().toArray(UUID[]::new);
    }

    public SyncFriendRoomsS2C(RegistryByteBuf buf) {
        int roomCount = buf.readInt();

        FriendRoom[] friendRooms = new FriendRoom[roomCount];
        if(roomCount > buf.readableBytes()) {
            throw new DecoderException("VarIntArray with size " + roomCount + " is bigger than allowed " + buf.readableBytes());
        } else {
            for (int i = 0; i < friendRooms.length; i++) {
                friendRooms[i] = readFriendRoom(buf);
            }
        }

        this.rooms = Arrays.stream(friendRooms).toList();
    }

    private FriendRoom readFriendRoom(RegistryByteBuf buf) {
        UUID hostUuid = buf.readUuid();
        FriendRoom room = new FriendRoom(hostUuid);

        int playerCount = buf.readInt();
        for (int i = 0; i < playerCount; i++) {
            UUID player = buf.readUuid();
            room.addPlayer(player);
        }
        return room;
    }

//    private UUID[] readUuidArray(RegistryByteBuf buf) {
//        int length = buf.readInt();
//
//        UUID[] readRooms = new UUID[length];
//        if (length > buf.readableBytes()) {
//            throw new DecoderException("VarIntArray with size " + length + " is bigger than allowed " + buf.readableBytes());
//        } else {
//            for (int i = 0; i < readRooms.length; i++) {
//                readRooms[i] = buf.readUuid();
//            }
//        }
//        return readRooms;
//    }

    public void write(RegistryByteBuf buf) {
        int roomCount = rooms.size();
        buf.writeInt(roomCount);

        for (FriendRoom room : rooms) {
            writeFriendRoom(buf, room);
        }
    }

    private void writeFriendRoom(RegistryByteBuf buf, FriendRoom room) {
        buf.writeUuid(room.hostUuid);

        List<UUID> players = room.players.stream().toList();
        int playerCount = players.size();
        buf.writeInt(playerCount);
        for (int i = 0; i < playerCount; i++) {
            buf.writeUuid(players.get(i));
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
