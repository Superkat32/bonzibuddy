package net.superkat.bonzibuddy.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.BonziMinigameManager;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements BonziMinigameWorld {
    @Shadow
    public abstract PersistentStateManager getPersistentStateManager();
    public BonziMinigameManager bonziMinigameManager;

    @Inject(
            method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/village/raid/RaidManager;tick()V")
    )
    public void bonzibuddy$tickMinigameManager(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(bonzibuddy$getMinigameManager() != null) {
            bonzibuddy$getMinigameManager().tick();
        } else {
            //creates the minigame manager
            this.bonziMinigameManager = this.getPersistentStateManager().getOrCreate(BonziMinigameManager.getPersistentStateType((ServerWorld) (Object) this), BonziBUDDY.MOD_ID);
        }
    }

    @Override
    public BonziMinigameManager bonzibuddy$getMinigameManager() {
        return bonziMinigameManager;
    }
}
