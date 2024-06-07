package net.superkat.bonzibuddy.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class BonziBuddyEntity extends PathAwareEntity {
    public BonziBuddyEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }
}
