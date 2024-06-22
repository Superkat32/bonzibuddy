package net.superkat.bonzibuddy.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.superkat.bonzibuddy.minigame.api.BonziMinigamePlayer;
import net.superkat.bonzibuddy.network.packets.minigame.PlayerInMinigameUpdateS2C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

//    @Inject(
//            method = "respawnPlayer",
//            at = @At(value = "TAIL")
//    )
//    private void bonzibuddy$keepInMinigameStatus(ServerPlayerEntity player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayerEntity> cir) {
//        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
//        BonziMinigamePlayer bonziPlayer = (BonziMinigamePlayer) player;
//        BonziMinigamePlayer oldBonziPlayer = (BonziMinigamePlayer) oldPlayer;
//
//        boolean inMinigame = oldBonziPlayer.bonzibuddy$inMinigame();
//        bonziPlayer.bonzibuddy$setInMinigame(inMinigame);
//
//        ServerPlayNetworking.send(player, new PlayerInMinigameUpdateS2C(inMinigame));
//    }

    @Inject(
            method = "respawnPlayer",
            at = @At(value = "RETURN")
    )
    private void bonzibuddy$keepInMinigameStatus(ServerPlayerEntity player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        BonziMinigamePlayer oldBonziPlayer = (BonziMinigamePlayer) player;

        ServerPlayerEntity newPlayerEntity = cir.getReturnValue();
        BonziMinigamePlayer bonziPlayer = (BonziMinigamePlayer) newPlayerEntity;

        boolean inMinigame = oldBonziPlayer.bonzibuddy$inMinigame();
        bonziPlayer.bonzibuddy$setInMinigame(inMinigame);

        ServerPlayNetworking.send(newPlayerEntity, new PlayerInMinigameUpdateS2C(inMinigame));
    }

}
