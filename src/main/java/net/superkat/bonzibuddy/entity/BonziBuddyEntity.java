package net.superkat.bonzibuddy.entity;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.List;

public class BonziBuddyEntity extends PathAwareEntity {
    public final AnimationState idleAnimState = new AnimationState();
    public final AnimationState idleSunglassAnimState = new AnimationState();
    public final AnimationState idleGlobeAnimState = new AnimationState();
    public final AnimationState idleSpyglassAnimState = new AnimationState();
    public final AnimationState idleBananaAnimState = new AnimationState();
    public final List<AnimationState> idleAnimations = List.of(idleAnimState, idleSunglassAnimState, idleGlobeAnimState, idleSpyglassAnimState, idleBananaAnimState);
    //take the seconds from BonziBuddyAnimations and multiply by 20 - I miss GeckoLib
    public final List<Integer> idleAnimationTimes = List.of(200, 220, 80, 140, 60);

    public int ticksUntilIdleAnim = 100;
    public int ticksSinceIdleAnim = 0;
    public int ticksUntilAnimDone = 0;
    public boolean readyForIdleAnim = false;
    public boolean shouldTurnHead = true;
    public BonziBuddyEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
    }

    @Override
    public void tick() {
        updateAnimations();
        super.tick();
    }

    public void updateAnimations() {
        if(isIdle()) {
            updateIdleAnimations();
        }
    }

    public void updateIdleAnimations() {
        ticksUntilIdleAnim--;
        if(ticksUntilIdleAnim <= 0) {
            readyForIdleAnim = true;
        }

        if(readyForIdleAnim) {
            startRandomIdleAnimation();
        } else {
            ticksSinceIdleAnim++;
            if(ticksSinceIdleAnim >= ticksUntilAnimDone) {
                shouldTurnHead = true;
            }
        }
    }

    public void startRandomIdleAnimation() {
        stopIdleAnimations();
        int idleAnimNum = this.random.nextInt(idleAnimations.size());
        AnimationState idleAnim = idleAnimations.get(idleAnimNum);
        idleAnim.startIfNotRunning(this.age);

        ticksUntilIdleAnim = this.random.nextBetween(300, 1000);
        ticksUntilAnimDone = idleAnimationTimes.get(idleAnimNum) + 10;
        ticksSinceIdleAnim = 0;
        readyForIdleAnim = false;
        shouldTurnHead = false;
    }

    public void stopAnimations() {
        stopIdleAnimations();
    }

    public void stopIdleAnimations() {
        idleAnimState.stop();
        idleAnimations.forEach(AnimationState::stop);
    }

    public boolean isIdle() {
        return this.getVelocity().length() <= 0.05;
    }
}
