package net.superkat.bonzibuddy.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.rendering.world.WindowsVoidRendering;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(
            method = "Lnet/minecraft/client/render/WorldRenderer;renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
            at = @At(
                    value = "TAIL")
    )
    public void bonzibuddy$injectWindowsVoidSkyEffect(Matrix4f matrix4f, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        //Check if the player is in the Protect Bonzi minigame dimension
        if(MinecraftClient.getInstance().world.getRegistryKey() == BonziBUDDY.PROTECT_BONZIBUDDY) {
            WindowsVoidRendering.renderErrorBoxes(matrix4f);
        }
    }

}
