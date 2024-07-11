package net.superkat.bonzibuddy.network;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.bonzi.BonziBuddyEntity;
import net.superkat.bonzibuddy.minigame.BonziMinigame;
import net.superkat.bonzibuddy.minigame.TripleChaosMinigame;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.minigame.room.FriendRoom;
import net.superkat.bonzibuddy.minigame.room.FriendRoomManager;
import net.superkat.bonzibuddy.network.packets.BonziAirplaneC2S;
import net.superkat.bonzibuddy.network.packets.BonziBuddyDoATrickC2S;
import net.superkat.bonzibuddy.network.packets.minigame.RequestPlayMinigameC2S;
import net.superkat.bonzibuddy.network.packets.minigame.RequestReturnToRespawnC2S;
import net.superkat.bonzibuddy.network.packets.room.*;

import java.util.Objects;
import java.util.Set;

public class BonziBuddyServerNetworkHandler {
    public static void registerServerPackets() {
        BonziBuddyPackets.registerPackets();

        //Server bound packets
        ServerPlayNetworking.registerGlobalReceiver(BonziBuddyDoATrickC2S.ID, BonziBuddyServerNetworkHandler::onBonziBuddyDoATrick);
        ServerPlayNetworking.registerGlobalReceiver(BonziAirplaneC2S.ID, BonziBuddyServerNetworkHandler::onBonziAirplane);
        ServerPlayNetworking.registerGlobalReceiver(RequestPlayMinigameC2S.ID, BonziBuddyServerNetworkHandler::onRequestBonziMinigame);
        ServerPlayNetworking.registerGlobalReceiver(RequestReturnToRespawnC2S.ID, BonziBuddyServerNetworkHandler::onRequestReturnToRespawn);
        ServerPlayNetworking.registerGlobalReceiver(CreateFriendRoomC2S.ID, BonziBuddyServerNetworkHandler::onFriendRoomCreate);
        ServerPlayNetworking.registerGlobalReceiver(RequestSyncFriendRoomsC2S.ID, BonziBuddyServerNetworkHandler::onRequestSyncFriendRooms);
        ServerPlayNetworking.registerGlobalReceiver(JoinFriendRoomC2S.ID, BonziBuddyServerNetworkHandler::onFriendRoomJoin);
        ServerPlayNetworking.registerGlobalReceiver(LeaveFriendRoomC2S.ID, BonziBuddyServerNetworkHandler::onRoomLeave);
        ServerPlayNetworking.registerGlobalReceiver(RemovePlayerFromRoomC2S.ID, BonziBuddyServerNetworkHandler::onPlayerRemoveFromRoom);
    }

    public static void onBonziBuddyDoATrick(BonziBuddyDoATrickC2S payload, ServerPlayNetworking.Context context) {
        BonziBuddyEntity bonziBuddyEntity = (BonziBuddyEntity) context.player().getWorld().getEntityById(payload.bonziBuddyId());
        if(bonziBuddyEntity != null && bonziBuddyEntity.isAlive()) {
            bonziBuddyEntity.doATrick();
        }
    }

    public static void onBonziAirplane(BonziAirplaneC2S payload, ServerPlayNetworking.Context context) {
        BonziBuddyEntity bonzi = (BonziBuddyEntity) context.player().getWorld().getEntityById(payload.bonziBuddyId());
        if(bonzi != null && bonzi.isAlive()) {
            int numberOfAirplanes = bonzi.getWorld().random.nextBetween(1, 3);
            bonzi.throwAirplanes(numberOfAirplanes);
        }
    }

    public static void onRequestReturnToRespawn(RequestReturnToRespawnC2S payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if(player != null && player.isAlive()) {
            if(BonziMinigameApi.isBonziBuddyWorld((ServerWorld) player.getWorld())) {
                BonziMinigameApi.teleportPlayerToRespawn(player);
            }
        }
    }

    public static void onFriendRoomCreate(CreateFriendRoomC2S payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if(player != null && player.isAlive()) {
            FriendRoom room = FriendRoomManager.createRoom(player.getUuid());
            ServerPlayNetworking.send(player, new CreatedFriendRoomS2C());
        }
    }

    public static void onFriendRoomJoin(JoinFriendRoomC2S payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if(player != null && player.isAlive()) {
            FriendRoomManager.playerJoinRoom(player, payload.roomUuid());
        }
    }

    public static void onRoomLeave(LeaveFriendRoomC2S payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if(player != null && player.isAlive()) {
            FriendRoomManager.playerLeaveRoom(player, payload.roomUuid());
        }
    }

    public static void onPlayerRemoveFromRoom(RemovePlayerFromRoomC2S payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity host = context.player();
        if(host != null && host.isAlive()) {
            FriendRoomManager.removePlayer(host, payload.player());
        }
    }

    public static void onRequestSyncFriendRooms(RequestSyncFriendRoomsC2S payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if(player != null && player.isAlive()) {
            FriendRoomManager.syncRooms(player);
        }
    }

    public static void onRequestBonziMinigame(RequestPlayMinigameC2S payload, ServerPlayNetworking.Context context) {
        BonziMinigameType type = payload.minigameType();
        if(type != BonziMinigameType.ABSTRACT) {
            ServerWorld world = context.player().getServerWorld();
            ServerWorld bonziWorld = Objects.requireNonNull(context.player().getServer()).getWorld(BonziBUDDY.PROTECT_BONZIBUDDY);

            if(bonziWorld != null) {
                boolean tripleChaosEnabled = world.getGameRules().getBoolean(BonziBUDDY.TRIPLE_CHAOS_ENABLED);
                if(!tripleChaosEnabled) {
                    BonziBUDDY.LOGGER.error("Can't start Bonzi Minigame! They are disabled!");
                    return;
                }

                Difficulty worldDifficulty = bonziWorld.getDifficulty();
                if(worldDifficulty == Difficulty.PEACEFUL) {
                    BonziBUDDY.LOGGER.error("Can't start Bonzi Minigame! The difficulty is in peaceful!");
                    return;
                }

                int[] playerIds = payload.playerIds();
                Set<ServerPlayerEntity> players = Sets.newHashSet();
                for (int playerId : playerIds) {
                    ServerPlayerEntity player = (ServerPlayerEntity) world.getEntityById(playerId);
                    players.add(player);
                }

                int maxMinigames = world.getGameRules().getInt(BonziBUDDY.MAX_ONGOING_MINIGAMES);
                if(BonziMinigameApi.getAllMinigames(bonziWorld).size() >= maxMinigames) {
                    BonziBUDDY.LOGGER.error("Can't start Bonzi Minigame! Ongoing minigame limit reached!");
                    players.forEach(player -> player.sendMessageToClient(Text.translatable("bonzibuddy.error.maxlimit"), false));
                    return;
                }

                BlockPos startPos = BonziMinigameApi.getAvailableMinigameBlockpos(bonziWorld);
                if(startPos != BlockPos.ORIGIN) {
                    BonziMinigame startedBonziMinigame = BonziMinigameApi.startBonziMinigame(type, bonziWorld, startPos);
                    if(startedBonziMinigame instanceof TripleChaosMinigame chaosMinigame) {
                        int difficulty = worldDifficulty.ordinal() + players.size() / 2;
                        chaosMinigame.setDifficultyLevel(difficulty);
                    }

                    BonziMinigameApi.teleportPlayersToMinigame(startedBonziMinigame, players.stream().toList());
                } else {
                    BonziBUDDY.LOGGER.error("Can't start Bonzi Minigame! Couldn't find structure location!");
                    players.forEach(player -> player.sendMessageToClient(Text.translatable("bonzibuddy.error.nolocation"), false));
                }
            } else {
                BonziBUDDY.LOGGER.error("Can't start Bonzi Minigame! (World is null or some other errorMessage?)");
            }
        }
    }
}
