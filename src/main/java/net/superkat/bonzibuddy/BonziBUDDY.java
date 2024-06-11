package net.superkat.bonzibuddy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.superkat.bonzibuddy.entity.BonziBuddyEntity;
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

	public static final EntityType<BonziBuddyEntity> BONZI_BUDDY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(MOD_ID, "bonzibuddy"),
			EntityType.Builder.create(BonziBuddyEntity::new, SpawnGroup.CREATURE).dimensions(0.75f, 1.0f).build()
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		//Bonzi Buddy Entity
		FabricDefaultAttributeRegistry.register(BONZI_BUDDY, BonziBuddyEntity.createMobAttributes());

		//Packets
		BonziBuddyServerNetworkHandler.registerServerPackets();

		//BonziMinigame command
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> BonziMinigameCommand.register(dispatcher)));
	}
}