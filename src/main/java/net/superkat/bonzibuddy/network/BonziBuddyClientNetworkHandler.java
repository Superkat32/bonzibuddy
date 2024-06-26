package net.superkat.bonzibuddy.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.superkat.bonzibuddy.entity.bonzi.BonziLikeEntity;
import net.superkat.bonzibuddy.minigame.MinigameHudData;
import net.superkat.bonzibuddy.minigame.api.BonziMinigamePlayer;
import net.superkat.bonzibuddy.network.packets.BonziBuddySyncAnimationS2C;
import net.superkat.bonzibuddy.network.packets.OpenBonziBuddyScreenS2C;
import net.superkat.bonzibuddy.network.packets.TriggeredAnimSyncWorkaroundS2C;
import net.superkat.bonzibuddy.network.packets.minigame.BonziBossBarUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.MinigameHudUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.PlayerInMinigameUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.WaitingForPlayersS2C;
import net.superkat.bonzibuddy.rendering.gui.BonziBuddyScreen;
import net.superkat.bonzibuddy.rendering.hud.MinigameHudRenderer;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.UUID;

public class BonziBuddyClientNetworkHandler {
    @Environment(EnvType.CLIENT)
    public static void registerClientPackets() {
        //Client bound packets
        ClientPlayNetworking.registerGlobalReceiver(OpenBonziBuddyScreenS2C.ID, BonziBuddyClientNetworkHandler::onBonziBuddyScreen);
        ClientPlayNetworking.registerGlobalReceiver(BonziBuddySyncAnimationS2C.ID, BonziBuddyClientNetworkHandler::onBonziBuddySyncAnimation);

        ClientPlayNetworking.registerGlobalReceiver(MinigameHudUpdateS2C.ID, BonziBuddyClientNetworkHandler::oneMinigameHudUpdate);
        ClientPlayNetworking.registerGlobalReceiver(BonziBossBarUpdateS2C.ID, BonziBuddyClientNetworkHandler::onBonziBossbarUpdate);
        ClientPlayNetworking.registerGlobalReceiver(WaitingForPlayersS2C.ID, BonziBuddyClientNetworkHandler::onWaitingForPlayers);
        ClientPlayNetworking.registerGlobalReceiver(PlayerInMinigameUpdateS2C.ID, BonziBuddyClientNetworkHandler::onInMinigameUpdate);

        ClientPlayNetworking.registerGlobalReceiver(TriggeredAnimSyncWorkaroundS2C.ID, BonziBuddyClientNetworkHandler::animSync);
    }

    public static void animSync(TriggeredAnimSyncWorkaroundS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        LivingEntity entity = (LivingEntity) client.world.getEntityById(payload.entityid());
        if(entity != null && entity instanceof BonziLikeEntity) {
            if(entity instanceof GeoEntity geoEntity) {
                if(payload.idle()) {
                    if(!geoEntity.getAnimatableInstanceCache().getManagerForId(payload.entityid()).getAnimationControllers().get(BonziLikeEntity.animControllerName).hasAnimationFinished()) {
                        return;
                    }
                }
                geoEntity.triggerAnim(payload.controller(), payload.anim());
            }
        }
    }

    public static void onBonziBuddyScreen(OpenBonziBuddyScreenS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        client.setScreen(new BonziBuddyScreen(context.player().getWorld(), payload.bonziBuddyId()));
    }

    public static void onBonziBuddySyncAnimation(BonziBuddySyncAnimationS2C payload, ClientPlayNetworking.Context context) {
//        MinecraftClient client = context.client();
//        LivingEntity entity = (LivingEntity) client.world.getEntityById(payload.bonziBuddyId());
//        int animationIndex = payload.bonziAnimationNumber();
//        if(entity != null && entity.isAlive() && entity instanceof BonziLikeEntity bonziEntity) {
//            BonziLikeEntity.BonziAnimation animation = BonziLikeEntity.BonziAnimation.getFromIndex(animationIndex);
//            bonziEntity.playAnimation(entity, animation);
//        }
    }

    public static void onWaitingForPlayers(WaitingForPlayersS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        client.inGameHud.setOverlayMessage(Text.translatable("bonzibuddy.minigame.waitingforplayers"), false);
    }

    public static void onInMinigameUpdate(PlayerInMinigameUpdateS2C payload, ClientPlayNetworking.Context context) {
        PlayerEntity player = context.player();
        BonziMinigamePlayer minigamePlayer = (BonziMinigamePlayer) player;
        minigamePlayer.bonzibuddy$setInMinigame(payload.inMinigame());
    }

    public static void onBonziBossbarUpdate(BonziBossBarUpdateS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        UUID hudUuid = payload.hudUuid();
        float percent = payload.percent();
        BonziBossBarUpdateS2C.BonziBoss type = payload.bonziBoss();
        MinigameHudRenderer.updateBossPercent(hudUuid, percent, type);
    }
    
    public static void oneMinigameHudUpdate(MinigameHudUpdateS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        UUID hudUuid = payload.uuid;
        MinigameHudUpdateS2C.Action action = payload.action;
        switch(action) {
            case ADD -> {
                MinigameHudData minigameHud = new MinigameHudData(payload);
                MinigameHudRenderer.minigameHuds.put(hudUuid, minigameHud);
            }
            case UPDATE_TIME -> {
                MinigameHudRenderer.updateTime(hudUuid, payload.time);
            }
            case UPDATE_WAVE -> {
                MinigameHudRenderer.updateWave(hudUuid, payload.wave);
            }
            case WAVE_CLEAR -> {
                MinigameHudRenderer.waveClear(hudUuid);
            }
            case UPDATE_GRACE_PERIOD -> {
                MinigameHudRenderer.updateGracePeriod(hudUuid, payload.gracePeriod);
            }
            case UPDATE_ONE_PLAYER_LEFT -> {
                MinigameHudRenderer.updateOnePlayerLeft(hudUuid, payload.onePlayerLeft);
            }
            case BOSS_DEFEATED -> {
                MinigameHudRenderer.updateDefeatedBoss(hudUuid, payload.defeatedBoss);
            }
            case VICTORY -> {
                MinigameHudRenderer.victory(hudUuid);
            }
            case DEFEAT -> {
                MinigameHudRenderer.defeat(hudUuid);
            }
            case REMOVE -> {
                MinigameHudRenderer.minigameHuds.remove(hudUuid);
            }
        }
    }
}
