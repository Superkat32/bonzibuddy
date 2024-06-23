package net.superkat.bonzibuddy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
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
	}
}