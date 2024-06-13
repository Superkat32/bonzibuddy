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


    private int ticks = 0;
    private float maxScale = 4f;
    private float scale = maxScale;
    private boolean fadeIn;
    private int fadeInTicks;
    private boolean fadeOut;
    private int fadeOutTicks;
    private int remainingFadeOutTicks;
    private int totalFlashTicks;
    private int flashTicks;
    private boolean flashedColor = false;
    public TextTypeWriter(UUID hudUuid, Text text, Color color, @Nullable Color flashColor, boolean bounceIn, boolean flash, boolean removeWhenDoneTyping, int fadeInTicks, int fadeOutTicks) {
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
                //this was way easier than I thought it would be - pretty shrimple really
                float fadeAmount = (float) fadeInTicks / ticks + maxScale - 1;
                this.scale = fadeAmount;
            } else {
                //this was way more difficult to make than it should have been
                float fadeAmount = (float) ticks / fadeInTicks;
                int scissorX = (int) ((width / 2) + x * (scale));
                int scissorY = (int) ((height / 2) + y * (scale));
                int scissorX2 = (int) (scissorX + textWidth * scale);
                int scissorY2 = (int) (scissorY + textHeight * scale);
//                context.fill(scissorX, scissorY, scissorX2, scissorY2, Color.white.getRGB());
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
                float fadeAmount = maxScale / remainingFadeOutTicks + maxScale;
                this.scale = fadeAmount;
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
            if(removeWhenDoneTyping && ticks == fadeInTicks + 60) {
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


        //make the text bigger and more dramatic
        context.getMatrices().translate(width / 2f, height / 2f, 0);
        context.getMatrices().scale(scale, scale, scale);
        context.drawTextWithShadow(client.textRenderer, this.text, x, y, textColor.getRGB());
        if(scissor) {
            context.disableScissor();
        }

        context.getMatrices().pop();
    }

    public void end() {
        fadeOut = true;
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
