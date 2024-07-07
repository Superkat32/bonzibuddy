package net.superkat.bonzibuddy.minigame.room;

import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.superkat.bonzibuddy.network.packets.room.OnFriendRoomJoinS2C;
import net.superkat.bonzibuddy.network.packets.room.SyncFriendRoomsS2C;

import java.util.Map;
import java.util.UUID;

public class FriendRoomManager {
    public static final Map<UUID, FriendRoom> rooms = Maps.newHashMap();

    public static FriendRoom createRoom(UUID hostUuid) {
        FriendRoom room = new FriendRoom(hostUuid);;
        rooms.put(hostUuid, room);
        return room;
    }

    public static void startRoom(UUID hostUuid) {
        FriendRoom room = rooms.get(hostUuid);
        if(room != null) {

        }
    }

    public static void syncRooms(ServerPlayerEntity player) {
        SyncFriendRoomsS2C payload = new SyncFriendRoomsS2C(rooms.values().stream().toList());
        ServerPlayNetworking.send(player, payload);
    }

    public static void playerJoinRoom(ServerPlayerEntity player, UUID roomUuid) {
        FriendRoom room = rooms.get(roomUuid);
        if(room != null) {
            room.addPlayer(player.getUuid());
            room.syncPlayers(player, true);
            ServerPlayNetworking.send(player, new OnFriendRoomJoinS2C(roomUuid));
        }
    }

    public static void playerLeaveRoom(ServerPlayerEntity player, UUID roomUuid) {
        FriendRoom room = rooms.get(roomUuid);
        if(room != null) {
            UUID playerUuid = player.getUuid();
            boolean hostLeaving = room.hostUuid.equals(playerUuid);
            room.syncPlayers(player, false);
            room.removePlayer(playerUuid);

            if(room.players.isEmpty()) {
                rooms.remove(roomUuid);
            } else if (hostLeaving) {
                //closes the room instead of finding a new host because I honestly can't be bothered
                rooms.remove(roomUuid);
            }
        }
    }

    public static void removePlayer(ServerPlayerEntity host, UUID removeUuid) {
        UUID roomUuid = host.getUuid();
        FriendRoom room = rooms.get(roomUuid);
        if(room != null) {
            ServerPlayerEntity removePlayer = (ServerPlayerEntity) host.getWorld().getPlayerByUuid(removeUuid);
            playerLeaveRoom(removePlayer, roomUuid);
        }
    }

}
