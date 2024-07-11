package net.superkat.bonzibuddy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.superkat.bonzibuddy.entity.BonziBuddyEntities;
import net.superkat.bonzibuddy.item.BonziItems;
import net.superkat.bonzibuddy.minigame.DisasterLoggerLevel;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.command.BonziMinigameCommand;
import net.superkat.bonzibuddy.network.BonziBuddyServerNetworkHandler;
import net.superkat.bonzibuddy.worldgen.ConstantSpreadStructurePlacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BonziBUDDY implements ModInitializer {
	public static final String MOD_ID = "bonzibuddy";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RegistryKey<World> PROTECT_BONZIBUDDY = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MOD_ID, "protect_bonzi"));
	public static final StructurePlacementType<ConstantSpreadStructurePlacement> CONSTANT_SPREAD_PLACEMENT_TYPE = Registry.register(
			Registries.STRUCTURE_PLACEMENT,
			Identifier.of(MOD_ID, "constant_spread"),
			() -> ConstantSpreadStructurePlacement.CODEC);

	public static final GameRules.Key<GameRules.BooleanRule> TRIPLE_CHAOS_ENABLED = GameRuleRegistry.register("tripleChaosEnabled", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.IntRule> UNLOADED_INVALIDATION_SECONDS = GameRuleRegistry.register("tripleChaosUnloadedSeconds", GameRules.Category.MOBS, GameRuleFactory.createIntRule(5, 0, 30));
	public static final GameRules.Key<EnumRule<DisasterLoggerLevel>> MINIGAME_DISASTER_LOGGER_LEVEL = GameRuleRegistry.register("tripleChaosDisasterLoggerLevel", GameRules.Category.MOBS, GameRuleFactory.createEnumRule(DisasterLoggerLevel.THINGS_ARE_OKAY, (server, rule) -> {
		LOGGER.info("Changed disaster logger level to {}", rule.get());
		if(rule.get() == DisasterLoggerLevel.EVERYTHING_IS_BROKEN_THE_WORLD_IS_ENDING_THIS_IS_NOT_FINE) {
			LOGGER.info("[BonziBUDDY]: oh snappers");
		}
	}));
	public static final GameRules.Key<GameRules.IntRule> MAX_ONGOING_MINIGAMES = GameRuleRegistry.register("tripleChaosMaxOngoingGames", GameRules.Category.MOBS, GameRuleFactory.createIntRule(10, 1, 10));

    public static final SimpleParticleType PAPER_AIRPLANE = FabricParticleTypes.simple();

	@Override
	public void onInitialize() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "airplane"), PAPER_AIRPLANE);

		BonziItems.registerItems();

		BonziBuddyEntities.registerEntities();

		//Packets
		BonziBuddyServerNetworkHandler.registerServerPackets();

		//BonziMinigame command
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> BonziMinigameCommand.register(dispatcher)));

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			if(BonziMinigameApi.isBonziBuddyWorld(origin)) {

			}
		});
	}
}