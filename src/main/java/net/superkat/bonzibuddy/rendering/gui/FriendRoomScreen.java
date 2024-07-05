package net.superkat.bonzibuddy.rendering.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.minigame.room.FriendRoom;
import net.superkat.bonzibuddy.network.packets.minigame.RequestPlayMinigameC2S;
import net.superkat.bonzibuddy.network.packets.room.LeaveFriendRoomC2S;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FriendRoomScreen extends Screen {

    public final FriendRoom room;
    public VeryFancyListWidget veryFancyListWidget = null;

    protected FriendRoomScreen(FriendRoom room) {
        super(Text.of("Friend Room"));
        this.room = room;
    }

    @Override
    protected void init() {
        boolean peaceful = this.client.player.getWorld().getDifficulty() == Difficulty.PEACEFUL;

        if(isRoomHost()) {
            VeryFancyButtonWidget playTripleChaos = new VeryFancyButtonWidget(
                    this.height - 40,
                    this.width,
                    Text.translatable("bonzibuddy.begin"),
                    (btn) -> requestPlayTripleChaos()
            ).showOutOfRange(peaceful);
            if(peaceful) {
                playTripleChaos.setTooltip(Tooltip.of(Text.translatable("bonzibuddy.peaceful")));
            }
            addDrawableChild(playTripleChaos);
        }

        VeryFancyButtonWidget leaveRoom = new VeryFancyButtonWidget(
                15,
                this.width,
                Text.translatable("bonzibuddy.leaveroom"),
                (btn) -> leaveRoom()
        );
        leaveRoom.setX(leaveRoom.getX() + leaveRoom.getWidth() + 4);
        addDrawableChild(leaveRoom);

        drawPlayers();
    }

    private boolean isRoomHost() {
        return this.client.player.getUuid().equals(this.room.hostUuid);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        Text playerName = getPlayerName(this.room.hostUuid);
        context.drawCenteredTextWithShadow(this.client.textRenderer, Text.translatable("bonzibuddy.joinableroom", playerName), this.width / 2, 10, Color.white.getRGB());
    }

    private Text getPlayerName(UUID uuid) {
        PlayerListEntry playerEntry = this.client.player.networkHandler.getPlayerListEntry(uuid);
        Text playerName = Text.translatable("bonzibuddy.roomerror");
        if(playerEntry != null) {
            playerName = this.client.inGameHud.getPlayerListHud().getPlayerName(playerEntry);
        }
        return playerName;
    }

    public List<UUID> collectPlayerEntries() {
        return this.client.player.networkHandler.getPlayerUuids().stream().toList();
    }

    public void redrawPlayers() {
        this.clearAndInit();
    }

    public void drawPlayers() {
        int buttonX = this.width / 2 - 104;
        int buttonY = 38;
        int buttonWidth = this.width / 2 + 104 - buttonX;
        int buttonHeight = 28;

        UUID hostUuid = room.hostUuid;
        UUID playerUuid = this.client.player.getUuid();
        ArrayList<UUID> allPlayers = new ArrayList<>(this.room.players.stream().toList());

        //move the host to the top
        Collections.swap(allPlayers, allPlayers.indexOf(hostUuid), 0);
        if(!isRoomHost()) {
            //move the current player beneath the host
            Collections.swap(allPlayers, allPlayers.indexOf(playerUuid), 1);
        }
        //sort rest of the players by in-game username - 1am right now can't believe this worked LOL
        allPlayers.subList(isRoomHost() ? 1 : 2, allPlayers.size()).sort((uuid1, uuid2) -> getPlayerName(uuid1).toString().compareToIgnoreCase(getPlayerName(uuid2).toString()));

        this.veryFancyListWidget = new VeryFancyListWidget(buttonX - 10, buttonY, buttonWidth + 20, this.height - 96, this.height, buttonHeight);

        for (UUID uuid : allPlayers) {
            drawPlayer(uuid, buttonY);
            buttonY += buttonHeight + 4;
        }

        this.addDrawableChild(veryFancyListWidget);

//
//        int buttonX = this.width / 2 - 104;
//        int buttonY = 38;
//        int buttonWidth = this.width / 2 + 104 - buttonX;
//        int buttonHeight = 28;
//
//        List<UUID> hostPlayer = allPlayers.stream().filter(hostUuid::equals).toList();
//        List<UUID> currentPlayer = allPlayers.stream().filter(playerUuid::equals).toList();
//        List<UUID> otherPlayers = allPlayers.stream().filter(uuid -> !uuid.equals(hostUuid) && !uuid.equals(playerUuid)).toList();
//
//        buttonY = renderList(hostPlayer, buttonX, buttonY, buttonWidth, buttonHeight);
//        if(!room.hostUuid.equals(playerUuid)) {
//            buttonY = renderList(currentPlayer, buttonX, buttonY, buttonWidth, buttonHeight);
//        }
//        buttonY = renderList(otherPlayers, buttonX, buttonY, buttonWidth, buttonHeight);
    }

    private void drawPlayer(UUID uuid, int buttonY) {
        Text playerName = getPlayerName(uuid);

        VeryFancyButtonWidget playerButton = new VeryFancyButtonWidget(
                buttonY,
                this.width,
                playerName,
                (btn) -> {

                }
        ).playerIcon(uuid).showSelected(true);
        this.veryFancyListWidget.addButton(playerButton);
    }

    public int renderList(List<UUID> players, int x, int y, int buttonWidth, int buttonHeight) {
        for (UUID playerUuid : players) {
            PlayerListEntry playerEntry = this.client.player.networkHandler.getPlayerListEntry(playerUuid);
            Text playerName = this.client.inGameHud.getPlayerListHud().getPlayerName(playerEntry);

            VeryFancyButtonWidget playerButton = new VeryFancyButtonWidget(
                    y,
                    this.width,
                    playerName,
                    (btn) -> {}
            ).playerIcon(playerUuid).showSelected(true);

            addDrawableChild(playerButton);
            y += buttonHeight + 4;
        }
        return y;
    }

    public void requestPlayTripleChaos() {
        //literally shouldn't be possible but
        if(this.room.players.isEmpty()) return;

        int[] playerIds = new int[this.room.players.size()];
        List<PlayerEntity> players = this.room.players.stream().map(uuid -> this.client.player.getWorld().getPlayerByUuid(uuid)).toList();

        for (int i = 0; i < this.room.players.size(); i++) {
            playerIds[i] = players.get(i).getId();
        }
        ClientPlayNetworking.send(new RequestPlayMinigameC2S(BonziMinigameType.TRIPLE_CHAOS, playerIds));
    }

    public void leaveRoom() {
        ClientPlayNetworking.send(new LeaveFriendRoomC2S(this.room.getHostUuid()));

        this.client.setScreen(new BrowseFriendRoomsScreen());
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
