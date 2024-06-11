package net.superkat.bonzibuddy.minigame.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;

public class BonziMinigameCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("bonziminigames")
                        .requires(source -> source.hasPermissionLevel(3))
                        .then(CommandManager.literal("start")
                                .executes(context -> executeStart(context.getSource()))
                        )
        );
    }

    private static int executeStart(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = player.getServerWorld();
        if(world != null) {
            BonziMinigameApi.startBonziMinigame(BonziMinigameType.CATASTROPHIC_CLONES, world, player.getBlockPos());
            source.sendFeedback(() -> Text.literal("Not a problem. Starting new Bonzi Minigame"), false);
            return 1;
        } else {
            source.sendError(Text.literal("Failed to start new Bonzi Minigame"));
        }

        return -1;
    }
}
