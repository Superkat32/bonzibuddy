package net.superkat.bonzibuddy.rendering.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.minigame.RequestPlayMinigameC2S;
import org.apache.commons.compress.utils.Lists;

import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class PrepBonziMinigameScreen extends Screen {

    public Identifier SELECTED = Identifier.of(BonziBUDDY.MOD_ID, "selected");
    public Identifier OUT_OF_RANGE = Identifier.of(BonziBUDDY.MOD_ID, "outofrange");

    List<PlayerEntity> players = Lists.newArrayList();
    public BlockPos bonziPos;
    private int iconSize = 24;
    private int entryX = this.width / 2 - 100;
    private int entryXLimit = this.width / 2 + 100;
    private int xPadding = 4;
    private int yPadding = 2;

    public int scrollAmount = 0;

    public PrepBonziMinigameScreen(BlockPos bonziPos) {
        super(Text.of("Bonzi Minigame Prep Screen"));
        this.bonziPos = bonziPos;
    }

    @Override
    protected void init() {
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

        VeryFancyButtonWidget browseFriendRooms = new VeryFancyButtonWidget(
                this.height - 76,
                this.width,
                Text.translatable("bonzibuddy.multiplayer"),
                (btn) -> browseFriendRooms()
        );
        addDrawableChild(browseFriendRooms);

        players.add(this.client.player);
        entryX = this.width / 2 - 100;
        entryXLimit = this.width / 2 + 100;
    }

    @Override
    public void tick() {
        super.tick();
        this.players.removeIf(player -> !playerInRange(player.getUuid()));
    }

    public List<UUID> collectPlayerEntries() {
        return this.client.player.networkHandler.getPlayerUuids().stream().toList();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderAllPlayers(context, mouseX, mouseY, entryX, 40);
    }

    public void renderAllPlayers(DrawContext context, int mouseX, int mouseY, int x, int y) {
        List<UUID> allPlayers = collectPlayerEntries();

        UUID mainUuid = this.client.player.getUuid();
        List<UUID> currentPlayer = allPlayers.stream().filter(mainUuid::equals).toList();
        List<UUID> selectablePlayers = selectablePlayers();
        List<UUID> unselectablePlayers = unselectablePlayers();

        context.enableScissor(x - xPadding - 2, y - yPadding - 2, entryXLimit + xPadding + 2, this.height - 54);

        y += scrollAmount;
        y = renderPlayerList(context, currentPlayer, mouseX, mouseY, x, y);
        y = renderPlayerList(context, selectablePlayers, mouseX, mouseY, x, y);
        y = renderPlayerList(context, unselectablePlayers, mouseX, mouseY, x, y);

        context.disableScissor();
    }

    private List<UUID> selectablePlayers() {
        List<UUID> allPlayers = collectPlayerEntries();
        UUID mainUuid = this.client.player.getUuid();
        List<UUID> selectablePlayers = allPlayers.stream().filter(uuid -> playerInRange(uuid) && !mainUuid.equals(uuid)).toList();
        return selectablePlayers;
    }

    private List<UUID> unselectablePlayers() {
        List<UUID> allPlayers = collectPlayerEntries();
        UUID mainUuid = this.client.player.getUuid();
        List<UUID> unselectablePlayers = allPlayers.stream().filter(uuid -> !playerInRange(uuid) && !mainUuid.equals(uuid)).toList();
        return unselectablePlayers;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
    }

    private PlayerEntity playerFromUuid(UUID uuid) {
        return this.client.player.getWorld().getPlayerByUuid(uuid);
    }

    private boolean playerInRange(UUID uuid) {
        PlayerEntity player = playerFromUuid(uuid);
        if(player == null) return false;
        return player.squaredDistanceTo(Vec3d.of(bonziPos)) <= 16 * 16; //16 block radius
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int searchY = 40 + iconSize + yPadding * 2 + 4 + scrollAmount;
        if(mouseY >= 40 - yPadding - 2 && mouseY <= this.height - 54) {
            for (UUID uuid : selectablePlayers()) {
                if(isMouseOverEntry(mouseX, mouseY, entryX, searchY) && playerInRange(uuid)) {
                    PlayerEntity player = playerFromUuid(uuid);
                    if(player != null && player != this.client.player) {
                        if(players.contains(player)) {
                            players.remove(player);
                        } else {
                            players.add(player);
                        }
                        this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                }
                searchY += iconSize + yPadding * 2 + 4;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean isMouseOverEntry(double mouseX, double mouseY, int entryX, int entryY) {
        return mouseX >= entryX && mouseX <= entryXLimit &&
                mouseY >= entryY && mouseY <= entryY + iconSize + yPadding;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int playerAmount = this.collectPlayerEntries().size();
        if(playerAmount > 5) {
            int entryHeight = iconSize + yPadding * 2 + 4;
            this.scrollAmount = MathHelper.clamp(this.scrollAmount + (int) verticalAmount * 10,-entryHeight * playerAmount + (this.height - 54 - 40), 0);
        } else {
            scrollAmount = 0;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    /**
     * @return The y of next entry to be rendered, if any is to be rendered.
     */
    public int renderPlayerList(DrawContext context, List<UUID> playerUuids, double mouseX, double mouseY, int x, int y) {
        for (UUID uuid : playerUuids) {
            boolean selected = players.contains(playerFromUuid(uuid));
            boolean outOfRange = !playerInRange(uuid);

            if(isMouseOverEntry(mouseX, mouseY, x, y)) {
                context.fill(x - xPadding - 2, y - yPadding - 2, entryXLimit + xPadding + 2, y + iconSize + yPadding + 2, Color.WHITE.getRGB());
            }


            context.fill(x - xPadding, y - yPadding, entryXLimit + xPadding, y + iconSize + yPadding, Color.GRAY.getRGB());

            if(selected) {
                context.fill(x - xPadding, y - yPadding, entryXLimit + xPadding, y + iconSize + yPadding, new Color(0, 255, 0, 155).getRGB());
            } else if (outOfRange) {
                context.fill(x - xPadding, y - yPadding, entryXLimit + xPadding, y + iconSize + yPadding, new Color(122, 27, 27, 155).getRGB());
            }

            context.fill(x - xPadding, y - yPadding + iconSize / 2, entryXLimit + xPadding + 2, y + iconSize + yPadding + 2, new Color(0, 0, 0, 128).getRGB());


            PlayerListEntry playerEntry = this.client.player.networkHandler.getPlayerListEntry(uuid);
            renderPlayerFace(context, playerEntry, x, y, iconSize);

            if(selected) {
                context.drawGuiTexture(SELECTED, x, y, iconSize, iconSize);
            } else if (outOfRange) {
                context.drawGuiTexture(OUT_OF_RANGE, x + 16, y - 4, 12, 12);
            }

            Text playerName = this.client.inGameHud.getPlayerListHud().getPlayerName(playerEntry);
            context.drawText(this.client.textRenderer, playerName, x + iconSize + xPadding, y + iconSize / 4, Color.WHITE.getRGB(), true);

            y += iconSize + yPadding * 2 + 4;
        }
        return y;
    }

    private void renderPlayerFace(DrawContext context, PlayerListEntry playerEntry, int x, int y, int size) {
        Supplier<SkinTextures> skinTexturesSupplier = playerEntry::getSkinTextures;
        PlayerSkinDrawer.draw(context, skinTexturesSupplier.get(), x, y, size);
    }

    public void browseFriendRooms() {
        this.client.setScreen(new BrowseFriendRoomsScreen());
    }

    public void requestPlayTripleChaos() {
        if(players.isEmpty()) return;

        int[] playerIds = new int[players.size()];

        for (int i = 0; i < players.size(); i++) {
            playerIds[i] = players.get(i).getId();
        }
        ClientPlayNetworking.send(new RequestPlayMinigameC2S(BonziMinigameType.TRIPLE_CHAOS, playerIds));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
