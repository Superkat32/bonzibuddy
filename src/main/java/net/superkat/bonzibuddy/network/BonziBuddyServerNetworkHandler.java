package net.superkat.bonzibuddy.network;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.bonzi.BonziBuddyEntity;
import net.superkat.bonzibuddy.minigame.BonziMinigame;
import net.superkat.bonzibuddy.minigame.TripleChaosMinigame;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.BonziAirplaneC2S;
import net.superkat.bonzibuddy.network.packets.BonziBuddyDoATrickC2S;
import net.superkat.bonzibuddy.network.packets.minigame.RequestPlayMinigameC2S;
import net.superkat.bonzibuddy.network.packets.minigame.RequestReturnToRespawnC2S;

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

    public static void onRequestBonziMinigame(RequestPlayMinigameC2S payload, ServerPlayNetworking.Context context) {
        BonziMinigameType type = payload.minigameType();
        if(type != BonziMinigameType.ABSTRACT) {
            ServerWorld world = context.player().getServerWorld();
            ServerWorld bonziWorld = Objects.requireNonNull(context.player().getServer()).getWorld(BonziBUDDY.PROTECT_BONZIBUDDY);
            if(bonziWorld != null) {
                Difficulty worldDifficulty = bonziWorld.getDifficulty();
                if(worldDifficulty != Difficulty.PEACEFUL) {
                    int[] playerIds = payload.playerIds();
                    Set<ServerPlayerEntity> players = Sets.newHashSet();
                    for (int playerId : playerIds) {
                        ServerPlayerEntity player = (ServerPlayerEntity) world.getEntityById(playerId);
                        players.add(player);
                    }
                    BlockPos startPos = BonziMinigameApi.getAvailableMinigameBlockpos(bonziWorld);
                    BonziMinigame startedBonziMinigame = BonziMinigameApi.startBonziMinigame(type, bonziWorld, startPos);
                    if(startedBonziMinigame instanceof TripleChaosMinigame chaosMinigame) {
                        int difficulty = worldDifficulty.ordinal() + players.size() / 2;
                        chaosMinigame.setDifficultyLevel(difficulty);
                    }

                    BonziMinigameApi.teleportPlayersToMinigame(startedBonziMinigame, players.stream().toList());
                } else {
                    BonziBUDDY.LOGGER.warn("Can't start Bonzi Minigame! The difficulty is in peaceful!");
                }
            }
        }
    }
}
