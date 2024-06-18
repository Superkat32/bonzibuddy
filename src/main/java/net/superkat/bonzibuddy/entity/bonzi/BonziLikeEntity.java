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
}