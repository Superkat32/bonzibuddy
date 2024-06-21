package net.superkat.bonzibuddy.rendering.hud;

import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.MinigameHudData;
import net.superkat.bonzibuddy.network.packets.minigame.BonziBossBarUpdateS2C;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * The client-side renderer of MinigameHudData.
 * @see MinigameHudData
 */
public class MinigameHudRenderer {
    private static final Identifier GRADIENT = Identifier.of(BonziBUDDY.MOD_ID, "minigame/gradient");
    private static final Identifier BONZI_BUDDY = Identifier.of(BonziBUDDY.MOD_ID, "minigame/bonzibuddy");
    private static final Identifier RED_BONZI_BUDDY = Identifier.of(BonziBUDDY.MOD_ID, "minigame/redbonzibuddy");
    private static final Identifier GREEN_BONZI_BUDDY = Identifier.of(BonziBUDDY.MOD_ID, "minigame/greenbonzibuddy");
    private static final Identifier BLUE_BONZI_BUDDY = Identifier.of(BonziBUDDY.MOD_ID, "minigame/bluebonzibuddy");
    private static final Identifier HEALTH_BAR = Identifier.of(BonziBUDDY.MOD_ID, "minigame/healthbar");
    private static final Identifier BACKGROUND = Identifier.of(BonziBUDDY.MOD_ID, "minigame/background");
    public static final Map<UUID, MinigameHudData> minigameHuds = Maps.newLinkedHashMap();
//    public static final Map<UUID, TextTypeWriter> textTypeWriters = Maps.newLinkedHashMap();
    public static final Map<UUID, List<TextTypeWriter>> textTypeWriters = Maps.newLinkedHashMap();

    public static void registerHudRenderEvents() {
        HudRenderCallback.EVENT.register(MinigameHudRenderer::renderMinigameHuds);
    }

    public static void updateTime(UUID uuid, int time) {
        MinigameHudData hudData = getHudFromUuid(uuid);
        if(hudData != null) {
            hudData.setTime(time);
        }
    }

    public static void updateWave(UUID uuid, int wave) {
        MinigameHudData hudData = getHudFromUuid(uuid);
        if(hudData != null) {
            hudData.setWave(wave);
        }
    }

    public static void waveClear(UUID uuid) {
        MinigameHudData hudData = getHudFromUuid(uuid);
        if(hudData != null) {
            createWaveClearWriter(uuid, hudData.wave);
        }
    }

    public static void updateGracePeriod(UUID uuid, int gracePeriod) {
        MinigameHudData hudData = getHudFromUuid(uuid);
        if(hudData != null) {
            hudData.setGracePeriod(gracePeriod);
            createGracePeriodWriter(uuid, gracePeriod);
        }
    }

    public static void updateOnePlayerLeft(UUID uuid, boolean onePlayerLeft) {
        MinigameHudData hudData = getHudFromUuid(uuid);
        if(hudData != null) {
            List<TextTypeWriter> typeWriters = textTypeWriters.get(uuid);
            TextTypeWriter textTypeWriter = null;

            for (TextTypeWriter typeWriter : typeWriters) {
                if(typeWriter != null) {
                    if(Objects.equals(typeWriter.text, Text.translatable("bonzibuddy.minigame.oneplayer"))) {
                        textTypeWriter = typeWriter;
                        break;
                    }
                }
            }
            //Checks if typewriter exists AND is the one player typewriter.
            //This is done because 1) the one player left packet gets sent every second,
            //and 2) the typewriter can be overridden by another packet, like the wave clear packet
            boolean typeWriterExist = textTypeWriter != null;

            hudData.setOnePlayerLeft(onePlayerLeft);
            if (onePlayerLeft && !typeWriterExist) {
                createOnePlayerLeftWriter(uuid);
            } else {
                if(!onePlayerLeft && typeWriterExist) {
                    textTypeWriter.end();
                }
            }
        }
    }

    public static void updateDefeatedBoss(UUID uuid, String defeatedBoss) {
        MinigameHudData hudData = getHudFromUuid(uuid);
        if(hudData != null) {
            hudData.setDefeatedBoss(defeatedBoss);
            if (!defeatedBoss.isEmpty()) {
                createDefeatedBossWriter(uuid, defeatedBoss);
                MinecraftClient client = MinecraftClient.getInstance();
                client.player.playSound(SoundEvents.ITEM_TRIDENT_THUNDER.value(), 1f, 1.5f);
                client.player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_HURT, 1f, 1.25f);
                client.player.playSound(SoundEvents.ENTITY_WARDEN_DEATH, 1f, 1.7f);
                client.player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_HURT, 1f, 1.7f);
            }
        }
    }

    public static void victory(UUID uuid) {
        MinigameHudData hudData = getHudFromUuid(uuid);
        if(hudData != null) {
            createVictoryTypeWriter(uuid);
        }
    }

    public static void defeat(UUID uuid) {
        MinigameHudData hudData = getHudFromUuid(uuid);
        if(hudData != null) {
            createDefeatTypeWriter(uuid);
        }
    }

    public static void updateBossPercent(UUID uuid, float percent, BonziBossBarUpdateS2C.BonziBoss type) {
        MinigameHudData hudData = getHudFromUuid(uuid);
        if(hudData != null) {
            switch (type) {
                case RED -> hudData.setRedBonziPercent(percent);
                case GREEN -> hudData.setGreenBonziPercent(percent);
                case BLUE -> hudData.setBlueBonziPercent(percent);
            }
        }
    }

    @Nullable
    public static MinigameHudData getHudFromUuid(UUID uuid) {
        return minigameHuds.get(uuid);
    }

    public static void renderMinigameHuds(DrawContext context, RenderTickCounter tickCounter) {
        int y = 8;
        for (MinigameHudData minigameHud : minigameHuds.values()) {
            renderMinigameHud(context, tickCounter, minigameHud, y);
            y += 36;
        }
    }

    private static void renderMinigameHud(DrawContext context, RenderTickCounter tickCounter, MinigameHudData minigameHud, int y) {
        //this whole method can be summed up in one word: confusion
        MinecraftClient client = MinecraftClient.getInstance();
        int windowWidth = context.getScaledWindowWidth();
        int initY = y;

        int width = 172;
        //I'm really pushing the height here to be as small as possible,
        //but it can really help in crucial gameplay moments having the extra room to see
        int height = 41;
        int gradientX = windowWidth / 2 - width / 2;

        int nameWidth = client.textRenderer.getWidth(String.valueOf(minigameHud.name));
        int nameX = gradientX + 4;
        int nameY = y - 4;

        //draw background
        context.drawGuiTexture(BACKGROUND, gradientX, initY, 0, width, height);
        context.fill(nameX, initY, nameX + nameWidth, initY + 2, new Color(42, 32, 59, 255).getRGB());

        //draw name
        context.drawTextWithShadow(client.textRenderer, String.valueOf(minigameHud.name), nameX, nameY, Color.WHITE.getRGB());

        //draw objective
        switch (minigameHud.type) {
            case CATASTROPHIC_CLONES -> {
                //draw wave number
                Text waveText = Text.translatable("bonzibuddy.minigame.wave", minigameHud.wave);
                int waveWidth = client.textRenderer.getWidth(waveText);
                int waveX = windowWidth / 2 - waveWidth / 2 - 40;
                y += 8;
                context.drawTextWithShadow(client.textRenderer, waveText, waveX, y, Color.WHITE.getRGB());

                //draw time
                //Add a Win98 themed clock icon?
                int timeWidth = client.textRenderer.getWidth(String.valueOf(minigameHud.time));
                int timeX = windowWidth / 2 - timeWidth / 2 + 40;
                context.drawTextWithShadow(client.textRenderer, String.valueOf(minigameHud.time), timeX, y, Color.WHITE.getRGB());

                //draw bonzi's face
                int bonziWidth = 16;
                int bonziHeight = 16;
                int padding = 2; //padding from edge of icon
                int bonziX = gradientX + padding + 4;
                y += 4;
                //outline
                int outlineWidth = 1;
                //TODO - special outline with darker color on bottom corner
                //TODO - outline color based on health
                context.fill(bonziX - outlineWidth, y - outlineWidth, bonziX + bonziWidth + outlineWidth, y + bonziHeight + outlineWidth, Color.WHITE.getRGB());
                context.drawGuiTexture(BONZI_BUDDY, bonziX, y, bonziWidth, bonziHeight);

                //draw bonzi's health - idk it just sorta works
                int healthX = bonziX + bonziWidth + padding * 2;
                int healthWidth = width - bonziWidth - padding * 4 - 8;
                int healthHeight = 4;
                y += 2;
                int gradientY = y + 5;
                context.fill(healthX - outlineWidth, gradientY - outlineWidth, healthX + healthWidth + outlineWidth, gradientY + healthHeight + outlineWidth, Color.WHITE.getRGB());
                context.drawGuiTexture(GRADIENT, healthX, gradientY, healthWidth, healthHeight);
                //TODO - add me
            }
            case TRIPLE_CHAOS -> {
                //draw time - Win98 themed clock icon?
                int timeWidth = client.textRenderer.getWidth(String.valueOf(minigameHud.time));
                int timeX = windowWidth / 2 - timeWidth / 2 + 40;
                context.drawTextWithShadow(client.textRenderer, String.valueOf(minigameHud.time), timeX, nameY, Color.WHITE.getRGB());



                y += 6;
                drawHealthBar(context, RED_BONZI_BUDDY, gradientX, y, 8, 8, width, minigameHud.redBonziPercent);
                y += 11;
                drawHealthBar(context, GREEN_BONZI_BUDDY, gradientX, y, 8, 8, width, minigameHud.greenBonziPercent);
                y += 11;
                drawHealthBar(context, BLUE_BONZI_BUDDY, gradientX, y, 8, 8, width, minigameHud.blueBonziPercent);
            }
        }

        //check for TextTypeWriter
        textTypeWriters.computeIfAbsent(minigameHud.uuid, list -> Lists.newArrayList());
        List<TextTypeWriter> typeWriters = textTypeWriters.get(minigameHud.uuid);
        boolean shouldScale = typeWriters.size() > 1;
        Iterator<TextTypeWriter> typeWriterIterator = typeWriters.iterator();

        context.getMatrices().push();
        if(shouldScale) {
            context.getMatrices().translate(windowWidth / 4f, height / 2f + 8, 0);
            context.getMatrices().scale(0.5f, 0.5f, 0.5f);
        }

        while (typeWriterIterator.hasNext()) {
            TextTypeWriter typeWriter = typeWriterIterator.next();

            if(typeWriter != null) {
                if(shouldScale && typeWriter instanceof DefeatTextTypeWriter) {
                    //couldn't be bothered to fix the math on the background scaling
                    ((DefeatTextTypeWriter) typeWriter).renderBackground = false;
                }

                typeWriter.tick(context);
                context.getMatrices().translate(0, 32, 0);

                if(typeWriter.readyForRemoval) {
                    typeWriterIterator.remove();
                }
            }
        }
        context.getMatrices().pop();
    }

    private static void drawHealthBar(DrawContext context, Identifier icon, int x, int y, int iconWidth, int iconHeight, int width, float percent) {
        Color outlineColor;
        if(percent <= 0) {
            outlineColor = Color.black;
        } else {
            //turn more red as the boss' health goes down
            outlineColor = new Color(255, (int) (255 * percent), (int) (255 * percent));
        }

        //draw bonzi's face
        int padding = 2; //padding from edge of icon
        int iconX = x + padding + 4;
        //outline
        int outlineWidth = 1;
        context.fill(iconX - outlineWidth, y - outlineWidth, iconX + iconWidth + outlineWidth, y + iconHeight + outlineWidth, outlineColor.getRGB());
        context.drawGuiTexture(icon, iconX, y, iconWidth, iconHeight);

        //draw bonzi's health - idk it just sorta works
        int healthX = iconX + iconWidth + padding * 2;
        int healthWidth = width - iconWidth - padding * 4 - 8;
        int healthHeight = 4;
        int gradientY = y + 2;
        context.fill(healthX - outlineWidth, gradientY - outlineWidth, healthX + healthWidth + outlineWidth, gradientY + healthHeight + outlineWidth, outlineColor.getRGB());
        context.drawGuiTexture(GRADIENT, healthX, gradientY, healthWidth, healthHeight);

        int percentX2 = healthX + healthWidth;
        int percentX = (int) (healthX + (healthWidth * percent));
        context.fill(percentX, gradientY, percentX2, gradientY + healthHeight, new Color(19, 12, 15).getRGB());
    }

    private static void addTypeWriter(UUID uuid, TextTypeWriter typeWriter) {
        textTypeWriters.get(uuid).add(typeWriter);
    }

    private static void addTypeWriter(UUID uuid, TextTypeWriter typeWriter, int index) {
        textTypeWriters.get(uuid).add(index, typeWriter);
    }

    private static void createWaveClearWriter(UUID uuid, int wave) {
        addTypeWriter(uuid, new TextTypeWriter(uuid, Text.translatable("bonzibuddy.minigame.waveclear", wave), new Color(154, 108, 246, 255), null, false, false, true));
    }
    private static void createGracePeriodWriter(UUID uuid, int gracePeriod) {
        boolean go = gracePeriod <= 0;
        Text text = go ? Text.translatable("bonzibuddy.minigame.go") : Text.of(String.valueOf(gracePeriod));
        addTypeWriter(uuid, new GracePeriodTextTypeWriter(uuid, text, new Color(143, 108, 246, 255), go));
    }
    private static void createOnePlayerLeftWriter(UUID uuid) {
        addTypeWriter(uuid, new TextTypeWriter(uuid, Text.translatable("bonzibuddy.minigame.oneplayer"), new Color(255, 91, 84, 255), new Color(213, 25, 17, 255), true, true, false));
    }
    private static void createDefeatedBossWriter(UUID uuid, String bossDefeated) {
        addTypeWriter(uuid, new ActionTextTypeWriter(uuid, Text.translatable("bonzibuddy.minigame.bossdefeated", bossDefeated), new Color(255, 212, 0, 255), new Color(255, 166, 0, 255), true));
    }
    private static void createVictoryTypeWriter(UUID uuid) {
        addTypeWriter(uuid, new TextTypeWriter(uuid, Text.translatable("bonzibuddy.minigame.victory"), new Color(130, 73, 243, 255), new Color(154, 108, 246, 255), true, true, true, 60, 10, 300), 0);
    }
    private static void createDefeatTypeWriter(UUID uuid) {
        addTypeWriter(uuid, new DefeatTextTypeWriter(uuid, Text.translatable("bonzibuddy.minigame.defeat"), new Color(213, 25, 17, 255), null, true, false, true), 0);
    }

}
