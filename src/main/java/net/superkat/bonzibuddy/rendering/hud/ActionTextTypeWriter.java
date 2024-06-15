package net.superkat.bonzibuddy.rendering.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.UUID;

public class ActionTextTypeWriter extends TextTypeWriter {
    private int maxSizeIndexTicks;
    private int sizeIndexTicks;
    private int sizeIndex = 0;
    public ActionTextTypeWriter(UUID hudUuid, Text text, Color color, @Nullable Color shadowColor, boolean removeWhenDoneTyping) {
        super(hudUuid, text, color, shadowColor, true, true, removeWhenDoneTyping);
        this.fadeInTicks = 20;
        this.totalFlashTicks = 20;
        this.ticksAfterTyping = 160;
        maxSizeIndexTicks = 3;
    }

    @Override
    protected void drawText(DrawContext context, int x, int y, int width, int height, Color textColor) {
        MinecraftClient client = MinecraftClient.getInstance();

        String string = text.getString();
        List<Character> chars = string.chars().mapToObj(e -> (char) e).toList();

        sizeIndexTicks++;
        if(sizeIndexTicks >= maxSizeIndexTicks) {
            sizeIndexTicks = 0;
            sizeIndex++;
            if(sizeIndex > chars.size()) {
                sizeIndex = 0;
            }
        }

        context.getMatrices().translate(width / 2f, height / 2f, 0);
        context.getMatrices().scale(scale, scale, scale);
        for (int i = 0; i < chars.size(); i++) {
            context.getMatrices().push();

            Character letter = chars.get(i);
            float rotationAmount = MathHelper.sin(this.ticks + i * 3);
            if(i == sizeIndex - 1) {
                context.getMatrices().scale(1.03f, 1.03f, 1.03f);
            } else if(i == sizeIndex) {
                context.getMatrices().scale(1.05f, 1.05f, 1.05f);
            } else if (i == sizeIndex + 1) {
                context.getMatrices().scale(1.03f, 1.03f, 1.03f);
            }

            context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationAmount), x, y, 0);
            context.drawTextWithShadow(client.textRenderer, letter.toString(), x, y, textColor.getRGB());
            x += client.textRenderer.getWidth(letter.toString());
            context.getMatrices().pop();
        }
    }

    @Override
    public void bounceIn() {
        float fadeAmount = (float) ticks / fadeInTicks;
        this.scale = maxScale * easeOutElastic(fadeAmount);
    }

    @Override
    protected void bounceOut() {
        float fadeAmount = maxScale / remainingFadeOutTicks + maxScale;
        this.scale = fadeAmount;
    }

    private float easeOutElastic(float size) {
        float c4 = (float) ((2 * Math.PI) / 3);
        return (float) (Math.pow(2, -10 * size) * Math.sin((size * 10 - 0.75) * c4) + 1);
    }
}
