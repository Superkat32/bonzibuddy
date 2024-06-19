package net.superkat.bonzibuddy.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigamePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements BonziMinigamePlayer {
    public boolean respawningFromMinigame = false;

    @ModifyExpressionValue(
            method = "dropInventory",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z")
    )
    private boolean bonzibuddy$dontDropInventoryInMinigame(boolean original) {
        if(inBonziMinigame((PlayerEntity) (Object) this)) {
            bonzibuddy$setRespawningFromMinigame(true);
            return true;
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "getXpToDrop",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z")
    )
    private boolean bonzibuddy$dontDropXpInMinigame(boolean original) {
        return original || inBonziMinigame((PlayerEntity) (Object) this);
    }

    @Override
    public boolean bonzibuddy$respawningFromMinigame() {
        return respawningFromMinigame;
    }

    @Override
    public void bonzibuddy$setRespawningFromMinigame(boolean respawning) {
        this.respawningFromMinigame = respawning;
    }

    @Override
    public boolean bonzibuddy$inMinigame() {
        return inBonziMinigame((PlayerEntity) (Object) this);
    }

    private boolean inBonziMinigame(PlayerEntity player) {
        if(!player.getWorld().isClient) { //should never be called on the client but just in case
            return BonziMinigameApi.playerInMinigame((ServerPlayerEntity) player);
        }
        return false;
    }
}
