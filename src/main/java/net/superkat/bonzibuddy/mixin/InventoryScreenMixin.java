package net.superkat.bonzibuddy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @WrapOperation(
            method = "drawBackground(Lnet/minecraft/client/gui/DrawContext;FII)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V")
    )
    protected void bonzibuddy$drawBackground(DrawContext context, Identifier texture, int x, int y, int u, int v, int width, int height, Operation<Void> original) {
        if(this.client != null) {
            if(this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                Identifier attempt_texture = Identifier.of("bonzibuddy", texture.getPath());
                MinecraftClient.getInstance().getTextureManager().bindTexture(attempt_texture);
                // horrible hack to check if bonzi override exists
                if(!(MinecraftClient.getInstance().getTextureManager().getOrDefault(attempt_texture, null) instanceof NativeImageBackedTexture)) {
                    texture = attempt_texture;
                    y = y-12;
                    x = x-1;
                    width = width + 4;
                    height = height + 12;
                }
            }
        }
        original.call(context, texture, x, y, u, v, width, height);
    }

    @WrapOperation(
            method = "drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I")
    )
    protected int bonzibuddy$drawForeground(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow, Operation<Integer> original) {
        if(this.client != null) {
            if(this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                x = x - 92;
                y = y - 11;
                color = -1;
            }
        }
        original.call(instance, textRenderer, text, x, y, color, shadow);
        return 0;
    }
}
