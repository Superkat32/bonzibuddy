package net.superkat.bonzibuddy.rendering.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.minigame.RequestPlayMinigameC2S;

import java.util.UUID;

public class PrepBonziMinigameScreen extends Screen {

    public PrepBonziMinigameScreen() {
        super(Text.of("Bonzi Minigame Prep Screen"));
    }

    @Override
    protected void init() {
        drawPlayer(this.client.player.getUuid(), 38);

        VeryFancyButtonWidget browseFriendRooms = new VeryFancyButtonWidget(
                this.height - 76,
                this.width,
                Text.translatable("bonzibuddy.multiplayer"),
                (btn) -> browseFriendRooms()
        );
        addDrawableChild(browseFriendRooms);

        boolean peaceful = this.client.player.getWorld().getDifficulty() == Difficulty.PEACEFUL;
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

    private void drawPlayer(UUID uuid, int buttonY) {
        Text playerName = getPlayerName(uuid);

        VeryFancyButtonWidget playerButton = new VeryFancyButtonWidget(
                buttonY,
                this.width,
                playerName,
                (btn) -> {

                }
        ).playerIcon(uuid).showSelected(true);

        this.addDrawableChild(playerButton);
    }

    private Text getPlayerName(UUID uuid) {
        PlayerListEntry playerEntry = this.client.player.networkHandler.getPlayerListEntry(uuid);
        Text playerName = Text.translatable("bonzibuddy.roomerror");
        if(playerEntry != null) {
            playerName = this.client.inGameHud.getPlayerListHud().getPlayerName(playerEntry);
        }
        return playerName;
    }

    public void browseFriendRooms() {
        this.client.setScreen(new BrowseFriendRoomsScreen());
    }

    public void requestPlayTripleChaos() {
        int[] playerIds = new int[1];
        playerIds[0] = this.client.player.getId();
        ClientPlayNetworking.send(new RequestPlayMinigameC2S(BonziMinigameType.TRIPLE_CHAOS, playerIds));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
