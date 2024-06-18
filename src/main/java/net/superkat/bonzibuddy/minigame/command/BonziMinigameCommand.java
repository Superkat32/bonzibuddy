package net.superkat.bonzibuddy.minigame.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.BonziMinigame;
import net.superkat.bonzibuddy.minigame.MinigameHudData;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.minigame.MinigameHudUpdateS2C;

import java.util.List;

public class BonziMinigameCommand {
    private static MinigameHudData hudData = placeholderHudData();
    public static final SuggestionProvider<ServerCommandSource> BONZI_MINIGAMES = SuggestionProviders.register(
            Identifier.of(BonziBUDDY.MOD_ID, "bonzi_minigames"),
            (context, builder) -> CommandSource.suggestMatching(
                    new String[]{"abstract", "catastrophic_clones", "triple_chaos"}, builder
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
                            ).then(CommandManager.literal("hud")
                                        .executes(context -> executeHud(context.getSource()))
                                        .then(CommandManager.literal("delete").executes(context -> removeHud(context.getSource()))
                                        ).then(CommandManager.literal("onePlayerLeft").executes(context -> onePlayerLeft(context.getSource()))
                                        ).then(CommandManager.literal("bossDefeated").executes(context -> bossDefeated(context.getSource()))
                                        ).then(CommandManager.literal("wave").executes(context -> waveUpdate(context.getSource()))
                                        ).then(CommandManager.literal("victory").executes(context -> victory(context.getSource()))
                                        ).then(CommandManager.literal("defeat").executes(context -> defeat(context.getSource()))
                                        ).then(CommandManager.literal("graceperiod").executes(context -> gracePeriod(context.getSource())))
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
                case "triple_chaos" -> minigameType = BonziMinigameType.TRIPLE_CHAOS;
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

    private static int executeHud(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = source.getWorld();
        if(world != null) {
            MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.ADD);
            ServerPlayNetworking.send(player, packet);
            source.sendFeedback(() -> Text.literal("Created placeholder hud!"), false);
            return 1;
        }
        return -1;
    }

    private static int removeHud(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = source.getWorld();
        if(world != null) {
            MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.REMOVE);
            ServerPlayNetworking.send(player, packet);
            source.sendFeedback(() -> Text.literal("Removed all placeholder huds!"), false);
            return 1;
        }
        return -1;
    }

    private static int onePlayerLeft(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = source.getWorld();
        if(world != null) {
            hudData.onePlayerLeft = !hudData.onePlayerLeft;
            MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.UPDATE_ONE_PLAYER_LEFT);
            ServerPlayNetworking.send(player, packet);
            source.sendFeedback(() -> Text.literal("Updated placeholder hud!"), false);
            return 1;
        }
        return -1;
    }

    private static int bossDefeated(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = source.getWorld();
        if(world != null) {
            String bossDefeated = "Blue Bonzi Buddy";
            hudData.setDefeatedBoss(bossDefeated);
            MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.BOSS_DEFEATED);
            ServerPlayNetworking.send(player, packet);
            source.sendFeedback(() -> Text.literal("Updated placeholder hud!"), false);
            return 1;
        }
        return -1;
    }

    private static int victory(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = source.getWorld();
        if(world != null) {
            MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.VICTORY);
            ServerPlayNetworking.send(player, packet);
            source.sendFeedback(() -> Text.literal("Updated placeholder hud!"), false);
            return 1;
        }
        return -1;
    }

    private static int defeat(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = source.getWorld();
        if(world != null) {
            MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.DEFEAT);
            ServerPlayNetworking.send(player, packet);
            source.sendFeedback(() -> Text.literal("Updated placeholder hud!"), false);
            return 1;
        }
        return -1;
    }

    private static int waveUpdate(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = source.getWorld();
        if(world != null) {
            MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.WAVE_CLEAR);
            ServerPlayNetworking.send(player, packet);
            hudData.wave++;
            MinigameHudUpdateS2C packet2 = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.UPDATE_WAVE);
            ServerPlayNetworking.send(player, packet2);
            source.sendFeedback(() -> Text.literal("Updated placeholder hud!"), false);
            return 1;
        }
        return -1;
    }

    private static int gracePeriod(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = source.getWorld();
        if(world != null) {
            if(hudData.gracePeriod <= 0) {
                hudData.gracePeriod = 10;
            } else {
                hudData.gracePeriod--;
            }
            MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.UPDATE_GRACE_PERIOD);
            ServerPlayNetworking.send(player, packet);
            source.sendFeedback(() -> Text.literal("Updated placeholder hud!"), false);
            return 1;
        }
        return -1;
    }

    private static MinigameHudData placeholderHudData() {
        return new MinigameHudData(MathHelper.randomUuid(), BonziMinigameType.TRIPLE_CHAOS, "Bonzi Minigame", 70, 1, 0, false, "", 1f, 1f, 1f);
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
