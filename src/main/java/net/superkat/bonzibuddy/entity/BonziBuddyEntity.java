package net.superkat.bonzibuddy.entity;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class BonziBuddyEntity extends PathAwareEntity {
    public final AnimationState idleAnimState = new AnimationState();
    public final AnimationState sunglassAnimState = new AnimationState();
    public BonziBuddyEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        //test check for if moving
        if(this.getVelocity().length() <= 0.05) {
            this.sunglassAnimState.stop();
            this.idleAnimState.startIfNotRunning(this.age);
        } else {
            this.idleAnimState.stop();
            this.sunglassAnimState.startIfNotRunning(this.age);
        }

        super.tick();
    }
}
