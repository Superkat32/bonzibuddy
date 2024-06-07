package net.superkat.bonzibuddy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.entity.BonziBuddyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BonziBUDDY implements ModInitializer {
	public static final String MOD_ID = "bonzibuddy";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final EntityType<BonziBuddyEntity> BONZI_BUDDY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(MOD_ID, "bonzibuddy"),
			EntityType.Builder.create(BonziBuddyEntity::new, SpawnGroup.CREATURE).dimensions(0.75f, 1.0f).build()
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		FabricDefaultAttributeRegistry.register(BONZI_BUDDY, BonziBuddyEntity.createMobAttributes());
	}
}