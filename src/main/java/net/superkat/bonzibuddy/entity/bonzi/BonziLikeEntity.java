package net.superkat.bonzibuddy.entity.bonzi;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.network.packets.BonziBuddySyncAnimationS2C;

import java.util.List;
import java.util.Random;

public interface BonziLikeEntity {
    boolean shouldTurnHead();
    boolean showSunglasses();
    void setShouldTurnHead(boolean shouldTurnHead);
    void setShowSunglasses(boolean showSunglasses);

    boolean readyForIdleAnim();
    void setReadyForIdleAnim(boolean readyForIdleAnim);
    int ticksUntilAnimDone();
    int ticksSinceIdleAnim();
    int ticksUntilNextIdleAnim();
    void setTicksUntilAnimDone(int ticksUntilAnimDone);
    void setTicksSinceIdleAnim(int ticksSinceIdleAnim);
    void setTicksUntilNextIdleAnim(int ticksUntilNextIdleAnim);

    AnimationState idleAnimState();
    AnimationState idleSunglassAnimState();
    AnimationState idleGlobeAnimState();
    AnimationState idleSpyglassAnimState();
    AnimationState idleBananaAnimState();
    AnimationState walkAnimState();
    AnimationState attackAnimState();
    AnimationState deathAnimState();

    boolean isIdle();
    default void syncAnimation(LivingEntity bonziEntity, BonziAnimation bonziAnimation) {
        ServerWorld world = (ServerWorld) bonziEntity.getWorld();
        List<ServerPlayerEntity> players = world.getPlayers(player -> player.squaredDistanceTo(bonziEntity) <= 16);
        players.forEach(player -> sendAnimationPacket(player, bonziEntity, bonziAnimation));
    }

    private void sendAnimationPacket(ServerPlayerEntity player, LivingEntity bonziEntity, BonziAnimation bonziAnimation) {
        ServerPlayNetworking.send(player, new BonziBuddySyncAnimationS2C(bonziEntity.getId(), bonziAnimation.ordinal()));
    }

    default void playAnimation(LivingEntity bonziEntity, BonziAnimation bonziAnimation) {
        World world = bonziEntity.getWorld();
        setShouldTurnHead(bonziAnimation.canTurnHead);
        setTicksUntilAnimDone(bonziAnimation.ticks() + 10);
        setTicksSinceIdleAnim(0);

        if (world.isClient) {
            stopAnimations();
            AnimationState animationState = getAnimationStateFromAnimation(bonziAnimation);
            animationState.startIfNotRunning(bonziEntity.age);
        } else {
            syncAnimation(bonziEntity, bonziAnimation);
            setTicksUntilNextIdleAnim(ticksUntilAnimDone() + resetIdleAnimationTicks(bonziEntity));
            setReadyForIdleAnim(false);
        }
    }

    default void updateAnimations(LivingEntity bonziEntity) {
        if(!bonziEntity.isAlive()) {
            deathAnimState().startIfNotRunning(bonziEntity.age);
        } else if(isIdle()) {
            //for some reason the death animation kept activating while idle, so this fixes that bug
            deathAnimState().stop();
            walkAnimState().stop();
            updateIdleAnimations(bonziEntity);
        } else {
            stopIdleAnimations();
            if(bonziEntity.getWorld().isClient) {
                walkAnimState().startIfNotRunning(bonziEntity.age);
            }
        }
    }

    default void updateIdleAnimations(LivingEntity bonziEntity) {
        setTicksSinceIdleAnim(ticksSinceIdleAnim() + 1);
        setTicksUntilNextIdleAnim(ticksUntilNextIdleAnim() - 1);

        if(bonziEntity.getWorld().isClient) {
            if(ticksSinceIdleAnim() >= ticksUntilAnimDone()) {
                setShouldTurnHead(true);
                setShowSunglasses(false);
            }
        } else {
            //idle animation starting is all handled on the server
            if(ticksUntilNextIdleAnim() <= 0) {
                setReadyForIdleAnim(true);
            }

            if(readyForIdleAnim()) {
                playRandomIdleAnimation(bonziEntity);
            }
        }
    }

    default void playRandomIdleAnimation(LivingEntity bonziEntity) {
        playAnimation(bonziEntity, BonziAnimation.randomIdleAnimation());
    }

    default int resetIdleAnimationTicks(LivingEntity bonziEntity) {
        return bonziEntity.getRandom().nextBetween(100, 300);
    }

    default void stopAnimations() {
        stopIdleAnimations();
        deathAnimState().stop();
        walkAnimState().stop();
        attackAnimState().stop();
    }

    default void stopIdleAnimations() {
        idleAnimState().stop();
        idleSunglassAnimState().stop();
        idleGlobeAnimState().stop();
        idleSpyglassAnimState().stop();
        idleBananaAnimState().stop();
    }

    default AnimationState getAnimationStateFromAnimation(BonziAnimation bonziAnimation) {
        int index = bonziAnimation.ordinal();
        AnimationState state;
        //sad but necessary - Minecraft uses switch statements for something similar to this anyways
        switch(index) {
            default -> state = idleAnimState();
            case 1 -> {
                state = idleSunglassAnimState();
                setShowSunglasses(true);
            }
            case 2 -> state = idleGlobeAnimState();
            case 3 -> state = idleSpyglassAnimState();
            case 4 -> state = idleBananaAnimState();
            case 5 -> state = walkAnimState();
            case 6 -> state = attackAnimState();
            case 7 -> state = deathAnimState();
        }
        return state;
    }

    /**
     * Steps to add a new animation: <br> <br>
     * 1. Export and add the animation to {@link net.superkat.bonzibuddy.entity.client.model.BonziBuddyAnimations}. <br> <br>
     * 2. Add the enum to {@link BonziAnimation}. The time can be copy-pasted from the animation in the BonziBuddyAnimations class. <br> <br>
     * 3. Create an AnimationState in here ({@link BonziLikeEntity#idleAnimState()}). Have all classes that implement BonziLikeEntity implement that new AnimationState. <br> <br>
     * 4. Add the enum to {@link BonziLikeEntity#getAnimationStateFromAnimation(BonziAnimation)}. It should return the newly added AnimationState. <br> <br>
     * 5. Add the animation to the {@link BonziLikeEntity#stopAnimations()} method. <br> <br>
     * 6. Update the AnimationState in {@link net.superkat.bonzibuddy.entity.client.model.BonziLikeModel#updateAnimationStates(BonziLikeEntity, SinglePartEntityModel, float, float, float)}. The Animation should be the newly imported Animation in BonziBuddyAnimations. <br> <br>
     * 7. Play the animation where you'd like!
     */
    enum BonziAnimation {
        IDLE_MAIN(10.0417F),
        IDLE_SUNGLASSES(11.0F, true),
        IDLE_GLOBE(4.0F),
        IDLE_SPYGLASS(6.875F),
        IDLE_BANANA(2.5F),
        WALK(0.5F, true),
        ATTACK(0.625F, true),
        DEATH(0.5F);

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

        public int ticks() {
            return ticks;
        }

        public boolean canTurnHead() {
            return canTurnHead;
        }

        public static BonziAnimation randomIdleAnimation() {
            return IDLE_ANIMATIONS.get(new Random().nextInt(IDLE_ANIMATIONS.size()));
        }

        public static BonziAnimation getFromIndex(int index) {
            return VALUES.get(index);
        }
    }

}
