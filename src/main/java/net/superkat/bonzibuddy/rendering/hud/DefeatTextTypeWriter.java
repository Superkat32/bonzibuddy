package net.superkat.bonzibuddy.rendering.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.UUID;

public class DefeatTextTypeWriter extends TextTypeWriter {
    public DefeatTextTypeWriter(UUID hudUuid, Text text, Color color, @Nullable Color flashColor, boolean bounceIn, boolean flash, boolean removeWhenDoneTyping) {
        super(hudUuid, text, color, flashColor, bounceIn, flash, removeWhenDoneTyping);
        this.ticksAfterTyping = 120;
    }

    @Override
    protected void drawText(DrawContext context, int x, int y, int width, int height, Color textColor) {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        float fadeAmount = 1f;
        if(fadeIn) {
            fadeAmount = (float) ticks / fadeInTicks;
        } else if (fadeOut) {
            fadeAmount = (float) remainingFadeOutTicks / fadeOutTicks;
        }
        context.setShaderColor(1f, 1f, 1f, MathHelper.clamp(fadeAmount, 0f, 1.0f));
        context.drawTexture(Identifier.ofVanilla("textures/gui/inworld_menu_background.png"), 0, 0, 0, 0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight(), 32, 32);
        context.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        super.drawText(context, x, y, width, height, textColor);
    }
}
