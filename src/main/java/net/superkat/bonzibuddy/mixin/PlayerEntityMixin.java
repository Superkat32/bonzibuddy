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
    public boolean inMinigame = false;

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

    //removed for now because ModFest server will be using adventure mode
    //Allows singleplayer users to edit the map to their liking - Minecraft style.
//    @ModifyReturnValue(
//            method = "isBlockBreakingRestricted",
//            at = @At("RETURN")
//    )
//    private boolean bonzibuddy$noModifyingBlocksInMinigameWorld(boolean original, @Local(argsOnly = true)GameMode gameMode) {
//        World world = ((PlayerEntity)(Object)this).getWorld();
//        if (!world.isClient()) {
//            ServerWorld serverWorld = (ServerWorld) world;
//            if(BonziMinigameApi.isBonziBuddyWorld(serverWorld)) {
//                if(gameMode.isSurvivalLike()) {
//                    return true;
//                }
//            }
//        }
//        return original;
//    }

    @Override
    public boolean bonzibuddy$respawningFromMinigame() {
        return respawningFromMinigame;
    }

    @Override
    public void bonzibuddy$setRespawningFromMinigame(boolean respawning) {
        this.respawningFromMinigame = respawning;
    }

    @Override
    public void bonzibuddy$setInMinigame(boolean inMinigame) {
        //Should really only be called and used on the client - Server can check directly
        this.inMinigame = inMinigame;
    }

    @Override
    public boolean bonzibuddy$inMinigame() {
        return inBonziMinigame((PlayerEntity) (Object) this);
    }

    private boolean inBonziMinigame(PlayerEntity player) {
        if(!player.getWorld().isClient) {
            //Can check from the server
            return BonziMinigameApi.playerInMinigame((ServerPlayerEntity) player);
        } else {
            //Relies on packets sent from the server for the client
            return inMinigame;
        }
    }
}
