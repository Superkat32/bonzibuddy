package net.superkat.bonzibuddy.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.superkat.bonzibuddy.minigame.api.BonziMinigamePlayer;
import net.superkat.bonzibuddy.rendering.gui.MinigameDeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Shadow public abstract ClientWorld getWorld();

    @Inject(
            method = "onDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"),
            cancellable = true
    )
    public void bonzibuddy$setMinigameDeathScreen(DeathMessageS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = (PlayerEntity) this.getWorld().getEntityById(packet.playerId());
        if(player != null && player == client.player) {
            BonziMinigamePlayer bonziPlayer = (BonziMinigamePlayer) player;
            if(bonziPlayer.bonzibuddy$inMinigame()) {
                client.setScreen(new MinigameDeathScreen(packet.message(), 4));
                ci.cancel();
            }
        }
    }

}
