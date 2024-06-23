package net.superkat.bonzibuddy.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.bonzi.BonziBuddyEntity;
import net.superkat.bonzibuddy.minigame.BonziMinigame;
import net.superkat.bonzibuddy.minigame.TripleChaosMinigame;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.BonziBuddyDoATrickC2S;
import net.superkat.bonzibuddy.network.packets.minigame.RequestPlayMinigameC2S;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Objects;

public class BonziBuddyServerNetworkHandler {
    public static void registerServerPackets() {
        BonziBuddyPackets.registerPackets();

        //Server bound packets
        ServerPlayNetworking.registerGlobalReceiver(BonziBuddyDoATrickC2S.ID, BonziBuddyServerNetworkHandler::onBonziBuddyDoATrick);
        ServerPlayNetworking.registerGlobalReceiver(RequestPlayMinigameC2S.ID, BonziBuddyServerNetworkHandler::onRequestBonziMinigame);
    }

    public static void onBonziBuddyDoATrick(BonziBuddyDoATrickC2S payload, ServerPlayNetworking.Context context) {
        BonziBuddyEntity bonziBuddyEntity = (BonziBuddyEntity) context.player().getWorld().getEntityById(payload.bonziBuddyId());
        if(bonziBuddyEntity != null && bonziBuddyEntity.isAlive()) {
            bonziBuddyEntity.doATrick();
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
                    Vec3d playerPos = context.player().getPos();
                    int[] playerIds = payload.playerIds();
                    ArrayList<ServerPlayerEntity> players = Lists.newArrayList();
                    for (int playerId : playerIds) {
                        ServerPlayerEntity player = (ServerPlayerEntity) world.getEntityById(playerId);
                        players.add(player);
                    }
//                    List<ServerPlayerEntity> players = world.getPlayers(player -> player.squaredDistanceTo(playerPos) <= 100);

                    BlockPos startPos = BonziMinigameApi.getAvailableMinigameBlockpos(bonziWorld);
                    BonziMinigame startedBonziMinigame = BonziMinigameApi.startBonziMinigame(type, bonziWorld, startPos);
                    if(startedBonziMinigame instanceof TripleChaosMinigame chaosMinigame) {
                        int difficulty = worldDifficulty.ordinal() + players.size() / 2;
                        chaosMinigame.setDifficultyLevel(difficulty);
                    }

                    BonziMinigameApi.teleportPlayersToMinigame(startedBonziMinigame, players);
                } else {
                    BonziBUDDY.LOGGER.warn("Can't start Bonz Minigame! The difficulty is in peaceful!");
                }
            }
        }
    }
}
