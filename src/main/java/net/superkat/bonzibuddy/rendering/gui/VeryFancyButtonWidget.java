package net.superkat.bonzibuddy.rendering.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import org.apache.commons.compress.utils.Lists;

import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class VeryFancyButtonWidget extends ButtonWidget {

    public static Identifier FALLBACK = Identifier.of(BonziBUDDY.MOD_ID, "minigame/bonzibuddy");
    public static Identifier SELECTED = Identifier.of(BonziBUDDY.MOD_ID, "selected");
    public static Identifier OUT_OF_RANGE = Identifier.of(BonziBUDDY.MOD_ID, "outofrange");

    public UUID playerIconUuid;
    public List<UUID> miniPlayerIconUuids = Lists.newArrayList();
    public int miniIconSize = 8;
    public Identifier icon;
    public int iconSize = 24;
    public boolean playSound = true;

    public boolean showCheckmark = false;
    public boolean showPending = false;
    public boolean showOutOfRange = false;

    public VeryFancyButtonWidget(int y, int screenWidth, Text message, PressAction onPress) {
        super(screenWidth / 2 - 104, y, screenWidth / 2 + 104 - (screenWidth / 2 - 104), 28, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
    }

    public VeryFancyButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
    }

    public VeryFancyButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message, onPress, narrationSupplier);
    }

    public VeryFancyButtonWidget showSelected(boolean show) {
        this.showCheckmark = show;
        return this;
    }

    public VeryFancyButtonWidget showPending(boolean show) {
        this.showPending = show;
        return this;
    }

    public VeryFancyButtonWidget showOutOfRange(boolean show) {
        this.showOutOfRange = show;
        return this;
    }

    public VeryFancyButtonWidget playerIcon(UUID playerUuid) {
        this.playerIconUuid = playerUuid;
        return this;
    }

    public VeryFancyButtonWidget icon(Identifier icon) {
        this.icon = icon;
        return this;
    }

    public VeryFancyButtonWidget withMiniPlayerIcons(List<UUID> players) {
        this.miniPlayerIconUuids = players;
        return this;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        if(isHovered()) {
            context.fill(this.getX() - 2, this.getY() - 2, this.getX() + this.getWidth() + 2, this.getY() + this.getHeight() + 2, Color.WHITE.getRGB());
        }

        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), Color.GRAY.getRGB());

        Color backgroundColor = Color.GRAY;
        if(showCheckmark) {
            backgroundColor = new Color(0, 255, 0, 155);
        } else if (showPending) {
            backgroundColor = new Color(255, 208, 42, 155);
        } else if (showOutOfRange) {
            backgroundColor = new Color(122, 27, 27, 155);
        }
        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), backgroundColor.getRGB());

        context.fill(this.getX(), this.getY() + this.getHeight() / 2 - 2, this.getX() + this.getWidth() + 2, this.getY() + this.getHeight() + 2, new Color(0, 0, 0, 128).getRGB());

        if(playerIconUuid != null) {
            PlayerListEntry playerEntry = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(playerIconUuid);
            renderPlayerFace(context, playerEntry, this.getX() + 4, this.getY() + 2, this.iconSize);
        } else if(icon != null) {
            context.drawGuiTexture(icon, this.getX() + 4, this.getY() + 2, iconSize, iconSize);
        }

        if(!miniPlayerIconUuids.isEmpty()) {
            int iconX = this.getX() + iconSize + 6;
            for (UUID player : miniPlayerIconUuids) {
                PlayerListEntry playerEntry = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(player);
                renderPlayerFace(context, playerEntry, iconX, this.getY() + iconSize + 2 - miniIconSize, this.miniIconSize);
                iconX += miniIconSize + 2;
            }
        }

        if(showCheckmark) {
            context.drawGuiTexture(SELECTED, this.getX() + 4, this.getY() + 2, iconSize, iconSize);
        } else if(showPending) {

        } else if(showOutOfRange) {
            context.drawGuiTexture(OUT_OF_RANGE, this.getX() + 20, this.getY() - 2, 12, 12);
        }

        context.drawText(MinecraftClient.getInstance().textRenderer, getMessage(), this.getX() + iconSize + 8, this.getY() + iconSize / 4 + 2, Color.WHITE.getRGB(), true);
    }

    private void renderPlayerFace(DrawContext context, PlayerListEntry playerEntry, int x, int y, int size) {
        Supplier<SkinTextures> skinTexturesSupplier = playerEntry::getSkinTextures;
        PlayerSkinDrawer.draw(context, skinTexturesSupplier.get(), x, y, size);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        if(playSound) {
            super.playDownSound(soundManager);
        }
    }
}
