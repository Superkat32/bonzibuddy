package net.superkat.bonzibuddy.rendering.gui;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.minigame.room.FriendRoom;
import net.superkat.bonzibuddy.network.packets.minigame.RequestPlayMinigameC2S;
import net.superkat.bonzibuddy.network.packets.room.CreateFriendRoomC2S;
import net.superkat.bonzibuddy.network.packets.room.JoinFriendRoomC2S;
import net.superkat.bonzibuddy.network.packets.room.RequestSyncFriendRoomsC2S;
import org.apache.commons.compress.utils.Lists;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BrowseFriendRoomsScreen extends Screen {
    public List<VeryFancyButtonWidget> roomButtons = Lists.newArrayList();
    public Set<FriendRoom> rooms = Sets.newHashSet();
    public VeryFancyListWidget veryFancyListWidget = null;
    public boolean roomsUpdated = false;
    private int iconSize = 24;
    private int buttonX;
    private int buttonWidth;
    private int buttonHeight;
    private int padding = 4;

    public int scrollAmount = 0;

    public BrowseFriendRoomsScreen() {
        super(Text.of("Bonzi Minigame Prep Screen"));
    }

    @Override
    protected void init() {
        int entryX = this.width / 2 - 100;
        int entryXLimit = this.width / 2 + 100;
        int xPadding = 4;
        int yPadding = 2;

        this.buttonX = entryX - xPadding;
        this.buttonWidth = (entryXLimit + xPadding + xPadding) - (entryX);
        this.buttonHeight = iconSize + yPadding + 2;

        refreshRooms();
    }

    @Override
    public void tick() {
        super.tick();
        if(roomsUpdated) {
            redrawRooms();
            roomsUpdated = false;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if(!roomsUpdated && this.children().isEmpty()) {
            context.drawCenteredTextWithShadow(this.client.textRenderer, Text.translatable("bonzibuddy.loadingfriendrooms"), this.width / 2, this.height / 2, Color.WHITE.getRGB());
        }
    }

    public void refreshRooms() {
        ClientPlayNetworking.send(new RequestSyncFriendRoomsC2S());
    }

    public void redrawRooms() {
        if(!this.children().isEmpty()) {
            roomButtons.clear();
            this.clearChildren();
        }

        this.scrollAmount = 0;
        int buttonY = 38;

        VeryFancyButtonWidget createRoom = new VeryFancyButtonWidget(
                buttonX, buttonY,
                buttonWidth, buttonHeight,
                Text.translatable("bonzibuddy.createroom"),
                (btn) -> {
                    createFriendRoom();
                }
        ).playerIcon(this.client.player.getUuid());
        this.addDrawableChild(createRoom);

        VeryFancyButtonWidget refresh = new VeryFancyButtonWidget(
                this.height - 40 - buttonHeight - 4,
                this.width,
                Text.translatable("bonzibuddy.refresh"),
                (btn) -> {
                    refreshRooms();
                }
        );
        this.addDrawableChild(refresh);

        boolean peaceful = this.client.player.getWorld().getDifficulty() == Difficulty.PEACEFUL;
        VeryFancyButtonWidget playTripleChaos = new VeryFancyButtonWidget(
                this.height - 40,
                this.width,
                Text.translatable("bonzibuddy.begin"),
                (btn) -> requestPlaySoloTripleChaos()
        ).showOutOfOrder(inRoom()).showOutOfRange(!inRoom() && peaceful);
        if(inRoom()) {
            //in theory this should never be seen but
            playTripleChaos.setTooltip(Tooltip.of(Text.translatable("bonzibuddy.inroom")));
        } else if(peaceful) {
            playTripleChaos.setTooltip(Tooltip.of(Text.translatable("bonzibuddy.peaceful")));
        }
        addDrawableChild(playTripleChaos);

        buttonY += buttonHeight + 12;

        int listHeight = this.height - 100 - buttonHeight - padding - buttonHeight - 4;
        this.veryFancyListWidget = new VeryFancyListWidget(buttonX - 10, buttonY, buttonWidth + 20, listHeight, this.height, buttonHeight + padding);
        for (FriendRoom room : rooms) {
            //to test scissor, loop here
//            for (int i = 0; i < 13; i++) {
            drawRoom(room, buttonY);
            buttonY += buttonHeight + padding;
//            }
        }
        this.addDrawableChild(this.veryFancyListWidget);
    }

    private void drawRoom(FriendRoom room, int buttonY) {
        UUID host = room.getHostUuid();
        List<UUID> roomPlayers = room.players.stream().filter(uuid -> !uuid.equals(host)).toList();

        VeryFancyButtonWidget roomButton = new VeryFancyButtonWidget(
                buttonX, buttonY,
                buttonWidth, buttonHeight,
                Text.translatable("bonzibuddy.joinableroom", getPlayerName(room.hostUuid)),
                (btn) -> {
                    joinFriendRoom(room);
                }
        ).playerIcon(host).withMiniPlayerIcons(roomPlayers);
        this.veryFancyListWidget.addButton(roomButton);
    }

    public void updateRooms(Set<FriendRoom> rooms) {
        this.rooms = rooms;
        roomsUpdated = true;
        UUID playerUuid = this.client.player.getUuid();
        for (FriendRoom room : rooms) {
            if(room.players.contains(playerUuid)) {
                this.client.setScreen(new FriendRoomScreen(room));
            }
        }
    }

    public boolean inRoom() {
        UUID playerUuid = this.client.player.getUuid();
        for (FriendRoom room : rooms) {
            if(room.players.contains(playerUuid)) {
                return true;
            }
        }
        return false;
    }

    private Text getPlayerName(UUID uuid) {
        PlayerListEntry playerEntry = this.client.player.networkHandler.getPlayerListEntry(uuid);
        Text playerName = Text.translatable("bonzibuddy.roomerror");
        if(playerEntry != null) {
            playerName = this.client.inGameHud.getPlayerListHud().getPlayerName(playerEntry);
        }
        return playerName;
    }

//    @Override
//    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
//        super.renderBackground(context, mouseX, mouseY, delta);
//    }

//    @Override
//    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
//        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
//    }

    public void requestPlaySoloTripleChaos() {
        int[] playerIds = new int[1];
        playerIds[0] = this.client.player.getId();
        ClientPlayNetworking.send(new RequestPlayMinigameC2S(BonziMinigameType.TRIPLE_CHAOS, playerIds));
    }

    public void createFriendRoom() {
        ClientPlayNetworking.send(new CreateFriendRoomC2S());
    }

    public void joinFriendRoom(FriendRoom room) {
        ClientPlayNetworking.send(new JoinFriendRoomC2S(room.hostUuid));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
