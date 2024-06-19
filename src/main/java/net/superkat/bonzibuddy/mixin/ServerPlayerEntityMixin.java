package net.superkat.bonzibuddy.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.superkat.bonzibuddy.minigame.BonziMinigame;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigamePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @ModifyExpressionValue(
            method = "copyFrom",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z")
    )
    private boolean bonzibuddy$keepInventoryDuringMinigame(boolean original, @Local(argsOnly = true) ServerPlayerEntity oldPlayer) {
        BonziMinigamePlayer player = (BonziMinigamePlayer) oldPlayer;

        if(player.bonzibuddy$respawningFromMinigame()) {
            player.bonzibuddy$setRespawningFromMinigame(false);
            return true;
        }
        return original || player.bonzibuddy$inMinigame();
    }

    @ModifyReturnValue(
            method = "getRespawnTarget",
            at = @At(value = "RETURN")
    )
    private TeleportTarget bonzibuddy$respawnAtMinigame(TeleportTarget original, @Local(argsOnly = true) boolean alive, @Local(argsOnly = true) TeleportTarget.PostDimensionTransition postDimensionTransition) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        BonziMinigamePlayer bonziPlayer = (BonziMinigamePlayer) player;
        if(!alive) {
            if(bonziPlayer.bonzibuddy$respawningFromMinigame()) {
                BonziMinigame minigame  = BonziMinigameApi.getMinigameAt((ServerWorld) player.getWorld(), player.getBlockPos());
                if(minigame != null) {
                    BlockPos spawnPos = minigame.getStartPos().add(0, 2, 0);
                    return new TeleportTarget((ServerWorld) player.getWorld(), spawnPos.toCenterPos(), Vec3d.ZERO, 0f, 0f, postDimensionTransition);
                }
            }
        }
        return original;
    }
}
