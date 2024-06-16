package net.superkat.bonzibuddy.entity.minigame.mob;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.entity.bonzi.BonziLikeEntity;
import net.superkat.bonzibuddy.entity.minigame.ProtectBonziEntity;

/**
 * Class to contain methods for interacting with the (Catastrophic Clones?) Bonzi minigame
 */
public abstract class AbstractBonziCloneEntity extends HostileEntity implements BonziLikeEntity {
    public final AnimationState idleAnimState = new AnimationState();
    public final AnimationState idleSunglassAnimState = new AnimationState();
    public final AnimationState idleGlobeAnimState = new AnimationState();
    public final AnimationState idleSpyglassAnimState = new AnimationState();
    public final AnimationState idleBananaAnimState = new AnimationState();
    public final AnimationState walkAnimState = new AnimationState();
    public final AnimationState attackAnimState = new AnimationState();
    public final AnimationState victorySunglassesAnimState = new AnimationState();
    public final AnimationState deathAnimState = new AnimationState();
    boolean shouldTurnHead = true;
    boolean showSunglasses = false;
    public int ticksUntilNextIdleAnim = 100;
    public int ticksSinceIdleAnim = 0;
    public int ticksUntilAnimDone = 0;
    public boolean readyForIdleAnim = false;
    public boolean victorySunglasses = false;
    public AbstractBonziCloneEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1, false));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8f, 0.5f));
        this.goalSelector.add(3, new LookAtEntityGoal(this, ProtectBonziEntity.class, 16f, 0.5f));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, ProtectBonziEntity.class, false));
    }

    @Override
    public void tick() {
        updateAnimations(this);
        super.tick();
    }

    @Override
    public boolean tryAttack(Entity target) {
        if(!this.getWorld().isClient) {
            playAnimation(this, BonziAnimation.ATTACK);
        }
        return super.tryAttack(target);
    }

    @Override
    public int resetIdleAnimationTicks(LivingEntity bonziEntity) {
        return bonziEntity.getRandom().nextBetween(30, 150);
    }

    public boolean isIdle() {
        return this.getVelocity().length() <= 0.1;
    }

    @Override
    public boolean shouldTurnHead() {
        return shouldTurnHead;
    }

    @Override
    public boolean showSunglasses() {
        return showSunglasses;
    }

    @Override
    public void setShouldTurnHead(boolean shouldTurnHead) {
        this.shouldTurnHead = shouldTurnHead;
    }

    @Override
    public void setShowSunglasses(boolean showSunglasses) {
        this.showSunglasses = showSunglasses;
    }

    @Override
    public boolean showDeathPose() {
        return false;
    }

    @Override
    public boolean readyForIdleAnim() {
        return readyForIdleAnim;
    }

    @Override
    public void setReadyForIdleAnim(boolean readyForIdleAnim) {
        this.readyForIdleAnim = readyForIdleAnim;
    }

    @Override
    public int ticksUntilAnimDone() {
        return ticksUntilAnimDone;
    }

    @Override
    public int ticksSinceIdleAnim() {
        return ticksSinceIdleAnim;
    }

    @Override
    public int ticksUntilNextIdleAnim() {
        return ticksUntilNextIdleAnim;
    }

    @Override
    public void setTicksUntilAnimDone(int ticksUntilAnimDone) {
        this.ticksUntilAnimDone = ticksUntilAnimDone;
    }

    @Override
    public void setTicksSinceIdleAnim(int ticksSinceIdleAnim) {
        this.ticksSinceIdleAnim = ticksSinceIdleAnim;
    }

    @Override
    public void setTicksUntilNextIdleAnim(int ticksUntilNextIdleAnim) {
        this.ticksUntilNextIdleAnim = ticksUntilNextIdleAnim;
    }

    @Override
    public boolean victorySunglasses() {
        return this.victorySunglasses;
    }

    @Override
    public void setVictorySunglasses(boolean victorySunglasses) {
        this.victorySunglasses = victorySunglasses;
    }

    @Override
    public AnimationState idleAnimState() {
        return idleAnimState;
    }

    @Override
    public AnimationState idleSunglassAnimState() {
        return idleSunglassAnimState;
    }

    @Override
    public AnimationState idleGlobeAnimState() {
        return idleGlobeAnimState;
    }

    @Override
    public AnimationState idleSpyglassAnimState() {
        return idleSpyglassAnimState;
    }

    @Override
    public AnimationState idleBananaAnimState() {
        return idleBananaAnimState;
    }

    @Override
    public AnimationState walkAnimState() {
        return walkAnimState;
    }

    @Override
    public AnimationState attackAnimState() {
        return attackAnimState;
    }

    @Override
    public AnimationState victorySunglassesAnimState() {
        return victorySunglassesAnimState;
    }

    @Override
    public AnimationState deathAnimState() {
        return deathAnimState;
    }
}
