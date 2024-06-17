package net.superkat.bonzibuddy.entity.bonzi;

import net.minecraft.entity.mob.MobEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;
import java.util.Locale;

public interface BonziLikeEntity {
    RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("CLONE_WALK");
    RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("CLONE_ATTACK");
    RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("IDLE_MAIN");
    RawAnimation IDLE_SUNGLASSES = RawAnimation.begin().thenLoop("IDLE_SUNGLASSES");
    RawAnimation IDLE_GLOBE = RawAnimation.begin().thenLoop("IDLE_GLOBE");
    RawAnimation IDLE_SPYGLASS = RawAnimation.begin().thenLoop("IDLE_SPYGLASS");
    RawAnimation IDLE_BANANA = RawAnimation.begin().thenLoop("IDLE_BANANA");
    RawAnimation DEATH_ANIM = RawAnimation.begin().thenLoop("YIKES");
    default List<RawAnimation> idleAnimations() {
        return List.of(IDLE_SUNGLASSES, IDLE_GLOBE, IDLE_SPYGLASS, IDLE_BANANA);
    }
    List<RawAnimation> trickAnimations = List.of(IDLE_SUNGLASSES, IDLE_GLOBE, DEATH_ANIM);

    String animControllerName = "controller";
    default PlayState animController(AnimationState<GeoAnimatable> state) {
        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        } else {
            return state.setAndContinue(IDLE_ANIM);
        }
    }

    String attackAnimControllerName = "attack_controller";
    default PlayState attackAnimController(AnimationState<GeoAnimatable> state) {
        MobEntity mobEntity = (MobEntity) this;
        if(mobEntity.isAttacking()) {
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    default String getAnimString(RawAnimation anim) {
        //I think setting the language to english here will help?
        return anim.toString().toLowerCase(Locale.ENGLISH);
    }

//    boolean shouldTurnHead();
//    boolean showSunglasses();
//    void setShouldTurnHead(boolean shouldTurnHead);
//    void setShowSunglasses(boolean showSunglasses);
//    default boolean showDeathPose() {
//        return true;
//    }
//
//    boolean readyForIdleAnim();
//    void setReadyForIdleAnim(boolean readyForIdleAnim);
//    int ticksUntilAnimDone();
//    int ticksSinceIdleAnim();
//    int ticksUntilNextIdleAnim();
//    void setTicksUntilAnimDone(int ticksUntilAnimDone);
//    void setTicksSinceIdleAnim(int ticksSinceIdleAnim);
//    void setTicksUntilNextIdleAnim(int ticksUntilNextIdleAnim);
//    boolean victorySunglasses();
//    void setVictorySunglasses(boolean victorySunglasses);
//
//    AnimationState idleAnimState();
//    AnimationState idleSunglassAnimState();
//    AnimationState idleGlobeAnimState();
//    AnimationState idleSpyglassAnimState();
//    AnimationState idleBananaAnimState();
//    AnimationState walkAnimState();
//    AnimationState attackAnimState();
//    AnimationState victorySunglassesAnimState();
//    AnimationState deathAnimState();
//
//    boolean isIdle();
//    default void syncAnimation(LivingEntity bonziEntity, BonziAnimation bonziAnimation) {
//        ServerWorld world = (ServerWorld) bonziEntity.getWorld();
//        List<ServerPlayerEntity> players = world.getPlayers(player -> player.squaredDistanceTo(bonziEntity) <= 144);
//        players.forEach(player -> sendAnimationPacket(player, bonziEntity, bonziAnimation));
//    }
//
//    private void sendAnimationPacket(ServerPlayerEntity player, LivingEntity bonziEntity, BonziAnimation bonziAnimation) {
//        ServerPlayNetworking.send(player, new BonziBuddySyncAnimationS2C(bonziEntity.getId(), bonziAnimation.ordinal()));
//    }
//
//    default void playAnimation(LivingEntity bonziEntity, BonziAnimation bonziAnimation) {
//        World world = bonziEntity.getWorld();
//        setShouldTurnHead(bonziAnimation.canTurnHead);
//        setTicksUntilAnimDone(bonziAnimation.ticks() + 10);
//        setTicksSinceIdleAnim(0);
//
//        if (world.isClient) {
//            stopAnimations();
//            AnimationState animationState = getAnimationStateFromAnimation(bonziAnimation);
//            animationState.startIfNotRunning(bonziEntity.age);
//        } else {
//            syncAnimation(bonziEntity, bonziAnimation);
//            setTicksUntilNextIdleAnim(ticksUntilAnimDone() + resetIdleAnimationTicks(bonziEntity));
//            setReadyForIdleAnim(false);
//        }
//    }
//
//    default void updateAnimations(LivingEntity bonziEntity) {
//        if(!bonziEntity.isAlive() && showDeathPose()) {
//            deathAnimState().startIfNotRunning(bonziEntity.age);
//        } else if(isIdle()) {
//            //for some reason the death animation kept activating while idle, so this fixes that bug
//            deathAnimState().stop();
//            walkAnimState().stop();
//            updateIdleAnimations(bonziEntity);
//        } else {
//            stopIdleAnimations();
//            if(bonziEntity.getWorld().isClient) {
//                walkAnimState().startIfNotRunning(bonziEntity.age);
//                if(victorySunglasses()) {
//                    victorySunglassesAnimState().startIfNotRunning(bonziEntity.age);
//                }
//            }
//        }
//    }
//
//    default void updateIdleAnimations(LivingEntity bonziEntity) {
//        setTicksSinceIdleAnim(ticksSinceIdleAnim() + 1);
//        setTicksUntilNextIdleAnim(ticksUntilNextIdleAnim() - 1);
//
//        if(bonziEntity.getWorld().isClient) {
//            if(ticksSinceIdleAnim() >= ticksUntilAnimDone()) {
//                setShouldTurnHead(true);
//                setShowSunglasses(false);
//            }
//        } else {
//            //idle animation starting is all handled on the server
//            if(ticksUntilNextIdleAnim() <= 0) {
//                setReadyForIdleAnim(true);
//            }
//
//            if(readyForIdleAnim()) {
//                playRandomIdleAnimation(bonziEntity);
//            }
//        }
//    }
//
//    default void playRandomIdleAnimation(LivingEntity bonziEntity) {
//        playAnimation(bonziEntity, BonziAnimation.randomIdleAnimation());
//    }
//
//    default int resetIdleAnimationTicks(LivingEntity bonziEntity) {
//        return bonziEntity.getRandom().nextBetween(100, 300);
//    }
//
//    default void stopAnimations() {
//        stopIdleAnimations();
//        deathAnimState().stop();
//        walkAnimState().stop();
//        attackAnimState().stop();
//        victorySunglassesAnimState().stop();
//    }
//
//    default void stopIdleAnimations() {
//        idleAnimState().stop();
//        idleSunglassAnimState().stop();
//        idleGlobeAnimState().stop();
//        idleSpyglassAnimState().stop();
//        idleBananaAnimState().stop();
//    }
//
//    default AnimationState getAnimationStateFromAnimation(BonziAnimation bonziAnimation) {
//        int index = bonziAnimation.ordinal();
//        AnimationState state;
//        //sad but necessary - Minecraft uses switch statements for something similar to this anyways
//        switch(index) {
//            default -> state = idleAnimState();
//            case 1 -> {
//                state = idleSunglassAnimState();
//                setShowSunglasses(true);
//            }
//            case 2 -> state = idleGlobeAnimState();
//            case 3 -> state = idleSpyglassAnimState();
//            case 4 -> state = idleBananaAnimState();
//            case 5 -> state = walkAnimState();
//            case 6 -> state = attackAnimState();
//            case 7 -> {
//                state = victorySunglassesAnimState();
//                setShowSunglasses(true);
//                setVictorySunglasses(true);
//            }
//            case 8 -> state = deathAnimState();
//        }
//        return state;
//    }
//
//    /**
//     * Steps to add a new animation: <br> <br>
//     * 1. Export and add the animation to {@link net.superkat.bonzibuddy.entity.client.model.BonziBuddyAnimations}. <br> <br>
//     * 2. Add the enum to {@link BonziAnimation}. The time can be copy-pasted from the animation in the BonziBuddyAnimations class. <br> <br>
//     * 3. Create an AnimationState in here ({@link BonziLikeEntity#idleAnimState()}). Have all classes that implement BonziLikeEntity implement that new AnimationState. <br> <br>
//     * 4. Add the enum to {@link BonziLikeEntity#getAnimationStateFromAnimation(BonziAnimation)}. It should return the newly added AnimationState. <br> <br>
//     * 5. Add the animation to the {@link BonziLikeEntity#stopAnimations()} method. <br> <br>
//     * 6. Update the AnimationState in {@link net.superkat.bonzibuddy.entity.client.model.BonziLikeModel#updateAnimationStates(BonziLikeEntity, SinglePartEntityModel, float, float, float)}. The Animation should be the newly imported Animation in BonziBuddyAnimations. <br> <br>
//     * 7. Play the animation where you'd like! Be careful though, as another animation may accidentally take priority over the animation. Add methods as needed.
//     */
//    enum BonziAnimation {
//        IDLE_MAIN(10.0417F),
//        IDLE_SUNGLASSES(11.0F, true),
//        IDLE_GLOBE(4.0F),
//        IDLE_SPYGLASS(6.875F),
//        IDLE_BANANA(2.5F),
//        WALK(0.5F, true),
//        ATTACK(0.625F, true),
//        VICTORY_GLASSES(6.875F),
//        DEATH(0.5F);
//
//        private final int ticks;
//        private final boolean canTurnHead;
//        private static final List<BonziAnimation> IDLE_ANIMATIONS = List.of(IDLE_MAIN, IDLE_SUNGLASSES, IDLE_GLOBE, IDLE_SPYGLASS, IDLE_BANANA);
//        private static final List<BonziAnimation> VALUES = List.of(values());
//        BonziAnimation(float seconds, boolean canTurnHead) {
//            this.ticks = (int) (seconds * 20);
//            this.canTurnHead = canTurnHead;
//        }
//
//        BonziAnimation(float seconds) {
//            //copy-paste seconds from BonziBuddyAnimations to here
//            //The Animation class can't be loaded on a server, which is why manual input has to be given
//            this(seconds, false);
//        }
//
//        public int ticks() {
//            return ticks;
//        }
//
//        public boolean canTurnHead() {
//            return canTurnHead;
//        }
//
//        public static BonziAnimation randomIdleAnimation() {
//            return IDLE_ANIMATIONS.get(new Random().nextInt(IDLE_ANIMATIONS.size()));
//        }
//
//        public static BonziAnimation getFromIndex(int index) {
//            return VALUES.get(index);
//        }
//    }

}
