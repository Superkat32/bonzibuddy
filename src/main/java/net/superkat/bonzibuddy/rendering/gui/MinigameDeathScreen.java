package net.superkat.bonzibuddy.rendering.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class MinigameDeathScreen extends Screen {
    public int secondsUntilRespawn = 4;
    public int ticksSinceDeath = 0;
    public final Text deathMessage;

    public MinigameDeathScreen(@Nullable Text deathMessage, int secondsUntilRespawn) {
        super(Text.translatable("deathScreen.title"));
        this.deathMessage = deathMessage;
        this.secondsUntilRespawn = secondsUntilRespawn;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.getMatrices().push();
        context.getMatrices().scale(2.0F, 2.0F, 2.0F);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2 / 2, 30, 16777215);
        context.getMatrices().scale(2.0F, 2.0F, 2.0F);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("bonzibuddy.minigame.respawning", this.secondsUntilRespawn), this.width / 2 / 2 / 2, 30, 16777215);
        context.getMatrices().pop();
        if (this.deathMessage != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.deathMessage, this.width / 2, 85, 16777215);
        }
    }

    @Override
    public void tick() {
        ticksSinceDeath++;
        if(ticksSinceDeath % 20 == 0) {
            secondsUntilRespawn--;
        }
        if(secondsUntilRespawn <= 0) {
            respawn();
        }
    }

    public void respawn() {
        this.client.player.requestRespawn();
        this.client.setScreen(null);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        fillBackgroundGradient(context, this.width, this.height);
    }

    static void fillBackgroundGradient(DrawContext context, int width, int height) {
        context.fillGradient(0, 0, width, height, 1615855616, -1602211792);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
