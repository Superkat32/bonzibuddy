package net.superkat.bonzibuddy.entity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.network.packets.BonziBuddySyncAnimationS2C;
import net.superkat.bonzibuddy.network.packets.OpenBonziBuddyScreenS2C;

import java.util.List;
import java.util.Random;

public class BonziBuddyEntity extends PathAwareEntity {
    public final AnimationState idleAnimState = new AnimationState();
    public final AnimationState idleSunglassAnimState = new AnimationState();
    public final AnimationState idleGlobeAnimState = new AnimationState();
    public final AnimationState idleSpyglassAnimState = new AnimationState();
    public final AnimationState idleBananaAnimState = new AnimationState();

    public int ticksUntilNextIdleAnim = 100;
    public int ticksSinceIdleAnim = 0;
    public int ticksUntilAnimDone = 0;
    public boolean readyForIdleAnim = false;
    public boolean shouldTurnHead = true;
    public boolean showSunglasses = false;
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
        updateAnimations();
        super.tick();
    }

    public void doATrick() {
        playRandomIdleAnimation();
    }

    public void syncAnimation(BonziAnimation bonziAnimation) {
        ServerWorld serverWorld = (ServerWorld) this.getWorld();
        List<ServerPlayerEntity> players = serverWorld.getPlayers(player -> player.squaredDistanceTo(this) <= 16);
        players.forEach(player -> sendAnimationPacket(player, bonziAnimation));
    }

    private void sendAnimationPacket(ServerPlayerEntity player, BonziAnimation bonziAnimation) {
        ServerPlayNetworking.send(player, new BonziBuddySyncAnimationS2C(this.getId(), bonziAnimation.ordinal()));
    }

    public void playAnimation(BonziAnimation bonziAnimation) {
        shouldTurnHead = bonziAnimation.canTurnHead;
        ticksUntilAnimDone = bonziAnimation.ticks + 10;
        ticksSinceIdleAnim = 0;

        if (this.getWorld().isClient) {
            stopAnimations();
            AnimationState animationState = getAnimationStateFromAnimation(bonziAnimation);
            animationState.startIfNotRunning(this.age);
        } else {
            syncAnimation(bonziAnimation);
            ticksUntilNextIdleAnim = ticksUntilAnimDone + this.random.nextBetween(100, 300);
            readyForIdleAnim = false;
        }
    }

    public void updateAnimations() {
        if(isIdle()) {
            updateIdleAnimations();
        } else {
            stopIdleAnimations();
        }
    }

    public void updateIdleAnimations() {
        ticksSinceIdleAnim++;
        ticksUntilNextIdleAnim--;

        if(this.getWorld().isClient) {
            if(ticksSinceIdleAnim >= ticksUntilAnimDone) {
                shouldTurnHead = true;
                showSunglasses = false;
            }
        } else {
            //idle animation starting is all handled on the server
            if(ticksUntilNextIdleAnim <= 0) {
                readyForIdleAnim = true;
            }

            if(readyForIdleAnim) {
                playRandomIdleAnimation();
            }
        }
    }

    public void playRandomIdleAnimation() {
        playAnimation(BonziAnimation.randomIdleAnimation());
    }

    public void stopAnimations() {
        stopIdleAnimations();
    }

    public void stopIdleAnimations() {
        idleAnimState.stop();
        idleSunglassAnimState.stop();
        idleGlobeAnimState.stop();
        idleSpyglassAnimState.stop();
        idleBananaAnimState.stop();
    }

    public boolean isIdle() {
        return this.getVelocity().length() <= 0.1;
    }

    private AnimationState getAnimationStateFromAnimation(BonziAnimation bonziAnimation) {
        int index = bonziAnimation.ordinal();
        AnimationState state;
        //sad but necessary - Minecraft uses switch statements for something similar to this anyways
        switch(index) {
            default -> state = idleAnimState;
            case 1 -> {
                state = idleSunglassAnimState;
                showSunglasses = true;
            }
            case 2 -> state = idleGlobeAnimState;
            case 3 -> state = idleSpyglassAnimState;
            case 4 -> state = idleBananaAnimState;
        }
        return state;
    }
    public enum BonziAnimation {
        IDLE_MAIN(10.0417F),
        IDLE_SUNGLASSES(11.0F, true),
        IDLE_GLOBE(4.0F),
        IDLE_SPYGLASS(6.875F),
        IDLE_BANANA(2.5F);
        private final int ticks;
        private final boolean canTurnHead;
        private static final List<BonziAnimation> IDLE_ANIMATIONS = List.of(IDLE_MAIN, IDLE_SUNGLASSES, IDLE_GLOBE, IDLE_SPYGLASS, IDLE_BANANA);
        private static final List<BonziAnimation> VALUES = List.of(values());
        BonziAnimation(float seconds, boolean canTurnHead) {
            this.ticks = (int) (seconds * 20);
            this.canTurnHead = canTurnHead;
        }
        BonziAnimation(float seconds) {
            //copy-paste seconds from BonziBuddyAnimations to here
            //The Animation class can't be loaded on a server, which is why manual input has to be given
            this(seconds, false);
        }

        public static BonziAnimation randomIdleAnimation() {
            return IDLE_ANIMATIONS.get(new Random().nextInt(IDLE_ANIMATIONS.size()));
        }

        public static BonziAnimation getFromIndex(int index) {
            return VALUES.get(index);
        }
    }
}
