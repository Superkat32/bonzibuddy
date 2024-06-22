package net.superkat.bonzibuddy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.superkat.bonzibuddy.entity.BonziBuddyEntities;
import net.superkat.bonzibuddy.item.BonziItems;
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

	@Override
	public void onInitialize() {
		//TODO
		/*
		Test and fix in multiplayer - Enemy health and damage needs buffing while in multiplayer
		Bonzi buddy screen
		If time allows, minigame nbt, otherwise release
		 */

		BonziItems.registerItems();

		BonziBuddyEntities.registerEntities();

		//Packets
		BonziBuddyServerNetworkHandler.registerServerPackets();

		//BonziMinigame command
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> BonziMinigameCommand.register(dispatcher)));
	}
}