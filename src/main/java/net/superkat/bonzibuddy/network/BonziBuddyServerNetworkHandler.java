package net.superkat.bonzibuddy.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.BonziBuddyEntity;
import net.superkat.bonzibuddy.minigame.BonziMinigame;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.BonziBuddyDoATrickC2S;
import net.superkat.bonzibuddy.network.packets.minigame.RequestPlayMinigameC2S;

import java.util.List;
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
                Vec3d playerPos = context.player().getPos();
                //FIXME - Allow the starting player to choose the players to play with
                List<ServerPlayerEntity> players = world.getPlayers(player -> player.squaredDistanceTo(playerPos) <= 100);

                BlockPos startPos = BonziMinigameApi.getAvailableMinigameBlockpos(bonziWorld);
                BonziMinigame startedBonziMinigame = BonziMinigameApi.startBonziMinigame(type, bonziWorld, startPos);

                BonziMinigameApi.teleportPlayersToMinigame(startedBonziMinigame, players);
            }
        }
    }
}
