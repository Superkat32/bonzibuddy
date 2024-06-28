package net.superkat.bonzibuddy.rendering.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.network.packets.minigame.RequestReturnToRespawnC2S;
import org.jetbrains.annotations.Nullable;

public class TextLeftIconWithTextWidget extends TextIconButtonWidget {

    public TextLeftIconWithTextWidget(int width, int height, Text text, int textureWidth, int textureHeight, Identifier texture, PressAction pressAction, @Nullable ButtonWidget.NarrationSupplier narrationSupplier) {
        super(width, height, text, textureWidth, textureHeight, texture, pressAction, narrationSupplier);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        int i = this.getX() + 2;
        int j = this.getY() + this.getHeight() / 2 - this.textureHeight / 2;
        context.drawGuiTexture(this.texture, i, j, this.textureWidth, this.textureHeight);
    }

    @Override
    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
        int i = this.getX() + this.textureWidth;
        int j = this.getX() + this.getWidth() - 4;
        int k = this.getX() + this.getWidth() / 2 + this.textureWidth;
        drawScrollableText(context, textRenderer, this.getMessage(), k, i, this.getY(), j, this.getY() + this.getHeight(), color);
    }

    public static TextIconButtonWidget createLeaveBonziWorldButton(int width, ButtonWidget.PressAction onPress, boolean hideText) {
        return new TextLeftIconWithTextWidget(
                width, //button width
                20, //height
                Text.translatable("bonzibuddy.minigame.exit"),
                20, 20, //texture width and height
                Identifier.of(BonziBUDDY.MOD_ID, "minigame/leave"),
                onPress, null //press action and no narration
        );
    }

    public static void requestReturnToSpawn() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(null);

        ClientPlayNetworking.send(new RequestReturnToRespawnC2S());
    }
}
