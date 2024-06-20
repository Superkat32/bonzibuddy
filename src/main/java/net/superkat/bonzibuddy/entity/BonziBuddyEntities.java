package net.superkat.bonzibuddy.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.bonzi.BonziBuddyEntity;
import net.superkat.bonzibuddy.entity.bonzi.minigame.ProtectBonziEntity;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziBossEntity;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziCloneEntity;
import org.jetbrains.annotations.Nullable;

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

    public static final EntityType<BonziBossEntity> BONZI_BOSS = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(BonziBUDDY.MOD_ID, "bonziboss"),
            EntityType.Builder.create(BonziBossEntity::new, SpawnGroup.CREATURE).dimensions(0.75f, 1.0f).build()
    );

    public static final EntityType<BananaBlasterEntity> BANANA_BLASTER_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(BonziBUDDY.MOD_ID, "bananablaster"),
            EntityType.Builder.<BananaBlasterEntity>create(BananaBlasterEntity::new, SpawnGroup.MISC).dimensions(0.75f, 0.5f).build()
    );

    public static final RegistryKey<DamageType> BANANA_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(BonziBUDDY.MOD_ID, "banana_damage"));

    public static DamageSource bananaDamageSource(Entity source, @Nullable LivingEntity attacker) {
        return damageSource(source.getWorld(), BANANA_DAMAGE, source, attacker);
    }

    private static DamageSource damageSource(World world, RegistryKey<DamageType> key, @Nullable Entity source, @Nullable Entity attacker) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), source, attacker);
    }

    public static void registerEntities() {
        //Bonzi Buddy Entity
        FabricDefaultAttributeRegistry.register(BONZI_BUDDY, BonziBuddyEntity.createMobAttributes());

        //Protectable Bonzi Buddy Entity
        FabricDefaultAttributeRegistry.register(PROTECTABLE_BONZI_BUDDY, ProtectBonziEntity.createMobAttributes());

        FabricDefaultAttributeRegistry.register(BONZI_CLONE, BonziCloneEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(BONZI_BOSS, BonziBossEntity.createMobAttributes());
    }
}
