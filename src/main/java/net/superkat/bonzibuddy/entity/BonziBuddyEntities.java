package net.superkat.bonzibuddy.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.bonzi.BonziBuddyEntity;
import net.superkat.bonzibuddy.entity.minigame.ProtectBonziEntity;
import net.superkat.bonzibuddy.entity.minigame.mob.BonziCloneEntity;

public class BonziBuddyEntities {
    public static final EntityType<BonziBuddyEntity> BONZI_BUDDY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(BonziBUDDY.MOD_ID, "bonzibuddy"),
            EntityType.Builder.create(BonziBuddyEntity::new, SpawnGroup.CREATURE).dimensions(0.75f, 1.0f).build()
    );

    public static final EntityType<ProtectBonziEntity> PROTECTABLE_BONZI_BUDDY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(BonziBUDDY.MOD_ID, "protectbonzi"),
            EntityType.Builder.create(ProtectBonziEntity::new, SpawnGroup.CREATURE).dimensions(0.75f, 1.0f).build()
    );

    public static final EntityType<BonziCloneEntity> BONZI_CLONE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(BonziBUDDY.MOD_ID, "bonziclone"),
            EntityType.Builder.create(BonziCloneEntity::new, SpawnGroup.CREATURE).dimensions(0.75f, 1.0f).build()
    );

    public static void registerEntities() {
        //Bonzi Buddy Entity
        FabricDefaultAttributeRegistry.register(BONZI_BUDDY, BonziBuddyEntity.createMobAttributes());

        //Protectable Bonzi Buddy Entity
        FabricDefaultAttributeRegistry.register(PROTECTABLE_BONZI_BUDDY, ProtectBonziEntity.createMobAttributes());

        FabricDefaultAttributeRegistry.register(BONZI_CLONE, BonziCloneEntity.createMobAttributes());
    }
}
