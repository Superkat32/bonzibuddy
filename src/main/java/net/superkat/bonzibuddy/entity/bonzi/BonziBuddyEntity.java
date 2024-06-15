package net.superkat.bonzibuddy.entity.bonzi;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.network.packets.OpenBonziBuddyScreenS2C;

public class BonziBuddyEntity extends PathAwareEntity implements BonziLikeEntity {
    public final AnimationState idleAnimState = new AnimationState();
    public final AnimationState idleSunglassAnimState = new AnimationState();
    public final AnimationState idleGlobeAnimState = new AnimationState();
    public final AnimationState idleSpyglassAnimState = new AnimationState();
    public final AnimationState idleBananaAnimState = new AnimationState();
    public final AnimationState walkAnimState = new AnimationState();
    public final AnimationState attackAnimState = new AnimationState();
    public final AnimationState deathAnimState = new AnimationState();
    boolean shouldTurnHead = true;
    boolean showSunglasses = false;
    public int ticksUntilNextIdleAnim = 100;
    public int ticksSinceIdleAnim = 0;
    public int ticksUntilAnimDone = 0;
    public boolean readyForIdleAnim = false;
    public BonziBuddyEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if(!this.getWorld().isClient) {
            ServerPlayNetworking.send((ServerPlayerEntity) player, new OpenBonziBuddyScreenS2C(this.getId()));
        }
        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        updateAnimations(this);
        super.tick();
    }

    public void doATrick() {
        playRandomIdleAnimation(this);
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
    public AnimationState deathAnimState() {
        return deathAnimState;
    }
}
