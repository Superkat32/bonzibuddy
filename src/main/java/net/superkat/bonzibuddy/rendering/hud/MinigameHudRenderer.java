package net.superkat.bonzibuddy.rendering.hud;

import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.superkat.bonzibuddy.minigame.MinigameHudData;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Map;
import java.util.UUID;

public class MinigameHudRenderer {
    public static final Map<UUID, MinigameHudData> minigameHuds = Maps.newLinkedHashMap();

    public static void registerHudRenderEvents() {
        HudRenderCallback.EVENT.register(MinigameHudRenderer::renderMinigameHuds);
    }

    public static void updateTime(UUID uuid, int time) {
        MinigameHudData hudData = getHudFromUuid(uuid);
        if(hudData != null) {
            hudData.setTime(time);
        }
    }

    @Nullable
    public static MinigameHudData getHudFromUuid(UUID uuid) {
        return minigameHuds.get(uuid);
    }

    public static void renderMinigameHuds(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = context.getScaledWindowWidth();
        int y = 12;
        //TODO - make this look better in game and contain more info
        for (MinigameHudData minigameHud : minigameHuds.values()) {
            int x = width / 2 - client.textRenderer.getWidth(String.valueOf(minigameHud.time)) / 2;
            context.drawTextWithShadow(client.textRenderer, String.valueOf(minigameHud.time), x, y, Color.WHITE.getRGB());
            y += 10;
        }
    }

}
