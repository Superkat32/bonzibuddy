package net.superkat.bonzibuddy.rendering.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class TextTypeWriter {
    public final UUID hudUuid;
    public final Text text;
    public Color color;
    @Nullable
    public Color flashColor;
    public boolean flash;
    public final boolean bounceIn;
    public final boolean removeWhenDoneTyping;
    public boolean readyForRemoval = false;


    protected int ticks = 0;
    protected float maxScale = 4f;
    protected float scale = maxScale;
    protected boolean fadeIn;
    protected int fadeInTicks;
    protected boolean fadeOut;
    protected int fadeOutTicks;
    protected int remainingFadeOutTicks;
    protected int totalFlashTicks;
    protected int flashTicks;
    protected boolean flashedColor = false;
    protected int ticksAfterTyping;
    public TextTypeWriter(UUID hudUuid, Text text, Color color, @Nullable Color flashColor, boolean bounceIn, boolean flash, boolean removeWhenDoneTyping, int fadeInTicks, int fadeOutTicks, int ticksAfterTyping) {
        this.hudUuid = hudUuid;
        this.text = text;
        this.color = color;
        this.flashColor = flashColor;
        this.flash = flash;
        this.bounceIn = bounceIn;
        this.removeWhenDoneTyping = removeWhenDoneTyping;
        this.fadeInTicks = fadeInTicks;
        this.fadeOutTicks = fadeOutTicks;
        this.remainingFadeOutTicks = fadeOutTicks;
        this.fadeIn = true;
        this.fadeOut = false;
        this.totalFlashTicks = 50;
        this.ticksAfterTyping = ticksAfterTyping;
    }
    public TextTypeWriter(UUID hudUuid, Text text, Color color, @Nullable Color flashColor, boolean bounceIn, boolean flash, boolean removeWhenDoneTyping) {
        this.hudUuid = hudUuid;
        this.text = text;
        this.color = color;
        this.flashColor = flashColor;
        this.flash = flash;
        this.bounceIn = bounceIn;
        this.removeWhenDoneTyping = removeWhenDoneTyping;
        //scale should be accounted for
        int cutTicks = bounceIn ? 10 : (int) (((StringVisitable)this.text).getString().length() * scale);
        this.fadeInTicks = cutTicks;
        this.fadeOutTicks = cutTicks;
        this.remainingFadeOutTicks = fadeOutTicks;
        this.fadeIn = true;
        this.fadeOut = false;
        this.totalFlashTicks = 50;
        this.ticksAfterTyping = 60;
    }


    public void tick(DrawContext context) {
        ticks++;
        MinecraftClient client = MinecraftClient.getInstance();
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        int textWidth = client.textRenderer.getWidth(this.text);
        int textHeight = client.textRenderer.getWrappedLinesHeight(this.text, 216);
        int x = -textWidth / 2;
        int y = -20;
        context.getMatrices().push();

        Color textColor = this.color;
        boolean scissor = false;

        if(fadeIn) {
            //check for cut in end
            if(ticks >= fadeInTicks) {
                fadeIn = false;
            }
            if(bounceIn) {
                this.bounceIn();
            } else {
                //this was way more difficult to make than it should have been
                float fadeAmount = (float) ticks / fadeInTicks;
                int scissorX = (int) ((width / 2) + x * (scale));
                int scissorY = (int) ((height / 2) + y * (scale));
                int scissorX2 = (int) (scissorX + textWidth * scale);
                int scissorY2 = (int) (scissorY + textHeight * scale);
                context.enableScissor(scissorX, scissorY, (int) (scissorX2 * fadeAmount), scissorY2);
                scissor = true;
            }
        } else if(fadeOut) {
            remainingFadeOutTicks--;
            //check for cut out end
            if(remainingFadeOutTicks <= 0) {
                readyForRemoval = true;
            }

            if(bounceIn) {
                this.bounceOut();
            } else {
                float fadeAmount = (float) remainingFadeOutTicks / fadeOutTicks;
                int scissorX = (int) ((width / 2) + x * (scale));
                int scissorY = (int) ((height / 2) + y * (scale));
                int scissorX2 = (int) (scissorX + textWidth * scale);
                int scissorY2 = (int) (scissorY + textHeight * scale);
                context.enableScissor(scissorX, scissorY, (int) (scissorX2 * fadeAmount), scissorY2);
                scissor = true;
            }
        } else {
            //3 seconds between fade in and out
            if(removeWhenDoneTyping && ticks == fadeInTicks + ticksAfterTyping) {
                end();
            }

            if(flash && this.flashColor != null) {
                flashTicks++;
                textColor = transitionColor(flashedColor ? this.flashColor : this.color, flashedColor ? this.color : this.flashColor, flashTicks, totalFlashTicks);

                if(flashTicks >= totalFlashTicks) {
                    flashTicks = 0;
                    flashedColor = !flashedColor;
                }
            }
        }


        drawText(context, x, y, width, height, textColor);
        if(scissor) {
            context.disableScissor();
        }

        context.getMatrices().pop();
    }

    public void end() {
        fadeOut = true;
    }

    protected void drawText(DrawContext context, int x, int y, int width, int height, Color textColor) {
        //make the text bigger and more dramatic
        context.getMatrices().translate(width / 2f, height / 2f, 0);
        context.getMatrices().scale(scale, scale, scale);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, this.text, x, y, textColor.getRGB());
    }

    protected void bounceIn() {
        //this was way easier than I thought it would be - pretty shrimple really
        float fadeAmount = (float) fadeInTicks / ticks + maxScale - 1;
        this.scale = fadeAmount;
    }

    protected void bounceOut() {
        float fadeAmount = maxScale / remainingFadeOutTicks + maxScale;
        this.scale = fadeAmount;
    }

    private Color transitionColor(Color original, Color fadeTo, int fadeTick, int fadeTicks) {
        int red = fadeTo.getRed() - original.getRed();
        int green = fadeTo.getGreen() - original.getGreen();
        int blue = fadeTo.getBlue() - original.getBlue();
        return new Color(
                original.getRed() + ((red * fadeTick) / fadeTicks),
                original.getGreen() + ((green * fadeTick) / fadeTicks),
                original.getBlue() + ((blue * fadeTick) / fadeTicks)
        );
    }
}
