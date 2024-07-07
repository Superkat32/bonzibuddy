package net.superkat.bonzibuddy.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.superkat.bonzibuddy.network.packets.*;
import net.superkat.bonzibuddy.network.packets.minigame.*;
import net.superkat.bonzibuddy.network.packets.room.*;

public class BonziBuddyPackets {
    public static void registerPackets() {
        //Server bound packets
        PayloadTypeRegistry.playC2S().register(BonziBuddyDoATrickC2S.ID, BonziBuddyDoATrickC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestPlayMinigameC2S.ID, RequestPlayMinigameC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestReturnToRespawnC2S.ID, RequestReturnToRespawnC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(BonziAirplaneC2S.ID, BonziAirplaneC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(CreateFriendRoomC2S.ID, CreateFriendRoomC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestSyncFriendRoomsC2S.ID, RequestSyncFriendRoomsC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(JoinFriendRoomC2S.ID, JoinFriendRoomC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(LeaveFriendRoomC2S.ID, LeaveFriendRoomC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(RemovePlayerFromRoomC2S.ID, RemovePlayerFromRoomC2S.CODEC);

        //Client bound packets
        PayloadTypeRegistry.playS2C().register(OpenBonziBuddyScreenS2C.ID, OpenBonziBuddyScreenS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(BonziBuddySyncAnimationS2C.ID, BonziBuddySyncAnimationS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(MinigameHudUpdateS2C.ID, MinigameHudUpdateS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(BonziBossBarUpdateS2C.ID, BonziBossBarUpdateS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(WaitingForPlayersS2C.ID, WaitingForPlayersS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerInMinigameUpdateS2C.ID, PlayerInMinigameUpdateS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(CreatedFriendRoomS2C.ID, CreatedFriendRoomS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncFriendRoomsS2C.ID, SyncFriendRoomsS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(RoomPlayerUpdateS2C.ID, RoomPlayerUpdateS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(OnFriendRoomJoinS2C.ID, OnFriendRoomJoinS2C.CODEC);

        PayloadTypeRegistry.playS2C().register(TriggeredAnimSyncWorkaroundS2C.ID, TriggeredAnimSyncWorkaroundS2C.CODEC);
    }
}
