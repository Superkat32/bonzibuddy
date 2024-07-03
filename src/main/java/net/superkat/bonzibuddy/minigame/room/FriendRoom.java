package net.superkat.bonzibuddy.minigame.room;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.superkat.bonzibuddy.network.packets.room.RoomPlayerUpdateS2C;

import java.util.Set;
import java.util.UUID;

public class FriendRoom {
    public UUID hostUuid;
    public Set<UUID> players = Sets.newHashSet();

    public FriendRoom(UUID hostUuid) {
        this.hostUuid = hostUuid;
        addPlayer(hostUuid);
    }

    public void addPlayer(UUID player) {
        players.add(player);
    }

    public void removePlayer(UUID player) {
        players.remove(player);
    }

    public void syncPlayers(ServerPlayerEntity updatedPlayer, boolean joined) {
        players.forEach(uuid -> {
            ServerPlayerEntity player = (ServerPlayerEntity) updatedPlayer.getServerWorld().getPlayerByUuid(uuid);
            if(player != null) {
                ServerPlayNetworking.send(player, new RoomPlayerUpdateS2C(this.hostUuid, updatedPlayer.getUuid(), joined));
            }
        });
    }

    public UUID getHostUuid() {
        return hostUuid;
    }
}
