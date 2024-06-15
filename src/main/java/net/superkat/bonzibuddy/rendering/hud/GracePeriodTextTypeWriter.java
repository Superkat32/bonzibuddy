package net.superkat.bonzibuddy.rendering.hud;

import net.minecraft.text.Text;

import java.awt.*;
import java.util.UUID;

public class GracePeriodTextTypeWriter extends TextTypeWriter {
    private final boolean normalBounceOut;
    public GracePeriodTextTypeWriter(UUID hudUuid, Text text, Color color, boolean bounceOut) {
        super(hudUuid, text, color, null, true, false, true);
        this.normalBounceOut = bounceOut;
        this.fadeInTicks = 3;
        this.ticksAfterTyping = 18;
    }

    @Override
    protected void bounceIn() {
        if(normalBounceOut) {
            super.bounceIn();
        } else {
            float fadeAmount = maxScale * ((float) ticks / fadeInTicks);
            this.scale = fadeAmount;
        }
    }

    @Override
    protected void bounceOut() {
        if(normalBounceOut) {
            super.bounceOut();
        } else {
            float fadeAmount = (float) remainingFadeOutTicks / fadeOutTicks;
            this.color = new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), (int) (255 * fadeAmount));
        }
    }
}
