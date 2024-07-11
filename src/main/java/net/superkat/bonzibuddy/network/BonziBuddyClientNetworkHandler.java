package net.superkat.bonzibuddy.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.superkat.bonzibuddy.entity.bonzi.BonziLikeEntity;
import net.superkat.bonzibuddy.minigame.MinigameHudData;
import net.superkat.bonzibuddy.minigame.api.BonziMinigamePlayer;
import net.superkat.bonzibuddy.minigame.room.FriendRoom;
import net.superkat.bonzibuddy.minigame.room.FriendRoomManager;
import net.superkat.bonzibuddy.network.packets.BonziBuddySyncAnimationS2C;
import net.superkat.bonzibuddy.network.packets.OpenBonziBuddyScreenS2C;
import net.superkat.bonzibuddy.network.packets.TriggeredAnimSyncWorkaroundS2C;
import net.superkat.bonzibuddy.network.packets.minigame.BonziBossBarUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.MinigameHudUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.PlayerInMinigameUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.WaitingForPlayersS2C;
import net.superkat.bonzibuddy.network.packets.room.CreatedFriendRoomS2C;
import net.superkat.bonzibuddy.network.packets.room.OnFriendRoomJoinS2C;
import net.superkat.bonzibuddy.network.packets.room.RoomPlayerUpdateS2C;
import net.superkat.bonzibuddy.network.packets.room.SyncFriendRoomsS2C;
import net.superkat.bonzibuddy.rendering.gui.BonziBuddyScreen;
import net.superkat.bonzibuddy.rendering.gui.BrowseFriendRoomsScreen;
import net.superkat.bonzibuddy.rendering.gui.FriendRoomScreen;
import net.superkat.bonzibuddy.rendering.hud.MinigameHudRenderer;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.HashSet;
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
        ClientPlayNetworking.registerGlobalReceiver(CreatedFriendRoomS2C.ID, BonziBuddyClientNetworkHandler::onFriendRoomCreation);
        ClientPlayNetworking.registerGlobalReceiver(SyncFriendRoomsS2C.ID, BonziBuddyClientNetworkHandler::onFriendRoomSync);
        ClientPlayNetworking.registerGlobalReceiver(RoomPlayerUpdateS2C.ID, BonziBuddyClientNetworkHandler::onRoomPlayerUpdate);
        ClientPlayNetworking.registerGlobalReceiver(OnFriendRoomJoinS2C.ID, BonziBuddyClientNetworkHandler::onRoomJoin);

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
        client.setScreen(new BonziBuddyScreen(context.player().getWorld(), payload.bonziBuddyId(), payload.tripleChaosEnabled()));
    }

    public static void onFriendRoomCreation(CreatedFriendRoomS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        if(client.currentScreen instanceof BrowseFriendRoomsScreen friendRoomsScreen) {
            friendRoomsScreen.refreshRooms();
        }
    }

    public static void onFriendRoomSync(SyncFriendRoomsS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        FriendRoomManager.syncRooms(payload);

        UUID selfUuid = client.player.getUuid();
        for (FriendRoom room : FriendRoomManager.rooms.values()) {
            if(room.players.contains(selfUuid)) {
                if(FriendRoomManager.currentRoom == null) {
                    //player joined room
                    client.player.playSoundToPlayer(SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.PLAYERS, 1f, 1f);
                }
                FriendRoomManager.currentRoom = room;
            }
        }

        //safety check
        if(FriendRoomManager.currentRoom != null && !FriendRoomManager.currentRoom.players.contains(selfUuid)) {
            FriendRoomManager.currentRoom = null;
        }

        if(client.currentScreen instanceof BrowseFriendRoomsScreen friendRoomsScreen) {
            friendRoomsScreen.updateRooms(new HashSet<>(FriendRoomManager.rooms.values()));
        }
    }

    public static void onRoomPlayerUpdate(RoomPlayerUpdateS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        FriendRoom room = FriendRoomManager.currentRoom;
        if(room != null) {
            UUID hostUuid = room.getHostUuid();
            UUID selfUuid = client.player.getUuid();
            if(hostUuid.equals(payload.roomUuid())) {
                UUID playerUpdated = payload.playerUpdated();
                boolean playerJoined = payload.playerJoined();

                boolean leftRoom = false;
                boolean roomDisbanded = playerUpdated.equals(hostUuid);

                if(playerJoined) {
                    room.addPlayer(playerUpdated);
                    client.player.playSoundToPlayer(SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.PLAYERS, 1f, 1f);
                } else {
                    room.removePlayer(playerUpdated);
                    SoundEvent leaveSound = SoundEvents.ITEM_AXE_WAX_OFF;

                    if(roomDisbanded) {
                        leaveSound = SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK;
                        leftRoom = true;
                    } else if (playerUpdated.equals(selfUuid)) {
                        leftRoom = true;
                    }

                    client.player.playSoundToPlayer(leaveSound, SoundCategory.PLAYERS, 1f, 1f);

                    if(leftRoom) {
                        FriendRoomManager.currentRoom = null;
                    }
                }

                if(client.currentScreen instanceof FriendRoomScreen friendRoomScreen) {
                    if(leftRoom) {
                        client.setScreen(new BrowseFriendRoomsScreen());
                    } else {
                        friendRoomScreen.redrawPlayers();
                    }
                } else if(!(client.currentScreen instanceof BrowseFriendRoomsScreen)){
                    if(playerJoined) {
                        client.inGameHud.getChatHud().addMessage(Text.translatable("bonzibuddy.playerjoin", getPlayerName(playerUpdated)));
                    } else {
                        if(roomDisbanded) {
                            client.inGameHud.getChatHud().addMessage(Text.translatable("bonzibuddy.disbanded", getPlayerName(playerUpdated)));
                        } else {
                            client.inGameHud.getChatHud().addMessage(Text.translatable("bonzibuddy.playerleave", getPlayerName(playerUpdated)));
                        }
                    }
                }
            }
        }
    }

    private static Text getPlayerName(UUID playerUuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerListEntry playerEntry = client.player.networkHandler.getPlayerListEntry(playerUuid);
        Text playerName = Text.of("");
        if(playerEntry != null) {
            playerName = client.inGameHud.getPlayerListHud().getPlayerName(playerEntry);
        }
        return playerName;
    }

    public static void onRoomJoin(OnFriendRoomJoinS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        if(client.currentScreen instanceof BrowseFriendRoomsScreen friendRoomsScreen) {
            friendRoomsScreen.refreshRooms();
        }
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
        MinigameHudRenderer.ticksSinceUpdate = 0;
    }
}
