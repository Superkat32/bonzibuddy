package net.superkat.bonzibuddy.minigame.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.BonziMinigame;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;

import java.util.List;

public class BonziMinigameCommand {
    public static final SuggestionProvider<ServerCommandSource> BONZI_MINIGAMES = SuggestionProviders.register(
            Identifier.of(BonziBUDDY.MOD_ID, "bonzi_minigames"),
            (context, builder) -> CommandSource.suggestMatching(
                    new String[]{"abstract", "catastrophic_clones"}, builder
            )
    );
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("bonzibuddy")
                        .requires(source -> source.hasPermissionLevel(3))
                        .then(CommandManager.literal("minigames")
                            .then(CommandManager.literal("start")
                                    .then(CommandManager.argument("type", StringArgumentType.word())
                                            .suggests(BONZI_MINIGAMES)
                                            .executes(context -> executeStart(context.getSource(), StringArgumentType.getString(context, "type")))
                                    )
                            ).then(CommandManager.literal("list")
                                        .executes(context -> executeList(context.getSource()))
                            ).then(CommandManager.literal("remove")
                                        .then(CommandManager.argument("id", IntegerArgumentType.integer())
                                                .executes(context -> executeRemove(context.getSource(), IntegerArgumentType.getInteger(context, "id"))))
                            ).then(CommandManager.literal("removeall")
                                        .executes(context -> executeRemoveAll(context.getSource()))
                            ).then(CommandManager.literal("test")
                                .then(CommandManager.literal("teleportPlayerToRespawn")
                                        .executes(context -> executeTpRespawn(context.getSource()))
                                )
                            )
                        )
        );
    }

    private static int executeStart(ServerCommandSource source, String type) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = player.getServerWorld();
        if(world != null) {
            BonziMinigameType minigameType;
            switch (type) {
                default -> {
                    source.sendError(Text.literal("Could not find a matching minigame type! Did you type it right? Was it added to the command class?"));
                    return -1;
                }
                case "abstract" -> minigameType = BonziMinigameType.ABSTRACT;
                case "catastrophic_clones" -> minigameType = BonziMinigameType.CATASTROPHIC_CLONES;
            }
            BonziMinigameApi.startBonziMinigame(minigameType, world, player.getBlockPos());
            source.sendFeedback(() -> Text.literal("Not a problem. Starting new Bonzi Minigame. Type: " + type.replace("_", " ")), false);
            return 1;
        } else {
            source.sendError(Text.literal("Failed to start new Bonzi Minigame"));
        }

        return -1;
    }

    private static int executeList(ServerCommandSource source) throws CommandSyntaxException {
        ServerWorld world = source.getWorld();
        if(world != null) {
            List<BonziMinigame> minigames = BonziMinigameApi.getAllMinigames(world);
            source.sendFeedback(() -> Text.literal("Searching for minigames...").formatted(Formatting.BOLD), false);
            if(minigames != null && !minigames.isEmpty()) {
                minigames.forEach(minigame -> {
                    source.sendFeedback(() -> Text.literal(""), false);
                    source.sendFeedback(() -> Text.literal("Minigame found!").formatted(Formatting.GOLD, Formatting.BOLD), false);
                    source.sendFeedback(() -> Text.literal("Type: " + minigame.getMinigameType().toString().formatted(Formatting.AQUA)), false);
                    source.sendFeedback(() -> Text.literal("ID: " + minigame.getId()).formatted(Formatting.WHITE), false);
                    source.sendFeedback(() -> Text.literal("Status: " + minigame.getStatus()).formatted(Formatting.DARK_AQUA), false);
                    source.sendFeedback(() -> Text.literal("Position: " + minigame.getStartPos().toShortString()).formatted(Formatting.GREEN), false);
                    source.sendFeedback(() -> Text.literal("Ticks Running: " + minigame.ticksSinceStart).formatted(Formatting.RED), false);
                    source.sendFeedback(() -> Text.literal("Player Count: " + minigame.getNearbyPlayers().size()).formatted(Formatting.LIGHT_PURPLE), false);
                });
                return 1;
            } else {
                source.sendFeedback(() -> Text.literal("No minigames are currently active!"), false);
                return 1;
            }
        }
        return -1;
    }

    private static int executeRemove(ServerCommandSource source, int id) throws CommandSyntaxException {
        ServerWorld world = source.getWorld();
        if(world != null) {
            BonziMinigame minigame = BonziMinigameApi.getMinigameById(world, id);
            if(minigame == null) {
                source.sendError(Text.literal("A minigame with that ID doesn't exist!"));
                return -1;
            }
            BonziMinigameApi.removeBonziMinigame(minigame);
            source.sendFeedback(() -> Text.literal("Removed minigame with ID: " + id), false);
            return 1;
        }
        return -1;
    }

    private static int executeRemoveAll(ServerCommandSource source) throws CommandSyntaxException {
        ServerWorld world = source.getWorld();
        if(world != null) {
            if(BonziMinigameApi.removeAllMinigames(world)) {
                source.sendFeedback(() -> Text.literal("Removed all minigames!"), false);
                return 1;
            } else {
                source.sendFeedback(() -> Text.literal("There are no currently active minigames!"), false);
                return -1;
            }
        }
        return -1;
    }

    private static int executeTpRespawn(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = player.getServerWorld();
        if(world != null) {
            List<ServerPlayerEntity> players = List.of(player);
            BonziMinigameApi.teleportPlayersToRespawn(players);
            source.sendFeedback(() -> Text.literal("Not a problem. Sent you to your respawn location!"), false);
            return 1;
        }
        return -1;
    }
}
