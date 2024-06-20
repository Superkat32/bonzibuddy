package net.superkat.bonzibuddy.entity.client.model;

import net.minecraft.util.math.MathHelper;
import net.superkat.bonzibuddy.entity.bonzi.BonziLikeEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public interface BonziLikeModel {

    default void turnHead(GeoEntity animatable, EntityModelData entityData, GeoBone head, long instanceId) {
        if(animationPlaying(animatable, instanceId)) {
            //Turns the head from the animation instead
//			head.setRotX((head.getRotX() + (entityData.headPitch() / 2f)) * MathHelper.RADIANS_PER_DEGREE);
//			head.setRotY((head.getRotY() + (entityData.netHeadYaw() / 2f)) * MathHelper.RADIANS_PER_DEGREE);
        } else {
            //turn head from the LookAtEntity goal
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }

    default boolean animationPlaying(GeoEntity animatable, long instanceId) {
        AnimationController<?> controller = getDefaultAnimationController(animatable, instanceId);
        return controller.getAnimationState() != AnimationController.State.STOPPED && controller.getCurrentRawAnimation() != BonziLikeEntity.WALK_ANIM;
    }

    default void handleItemsVisibility(GeoEntity animatable, long instanceId) {
        DefaultedGeoModel<? extends GeoAnimatable> model = (DefaultedGeoModel<? extends GeoAnimatable>) this;

        GeoBone sunglasses = model.getAnimationProcessor().getBone("sunglasses");
        sunglasses.setHidden(!showSunglasses(animatable, instanceId));

        GeoBone globe = model.getAnimationProcessor().getBone("globe");
        globe.setHidden(!showGlobe(animatable, instanceId));

        GeoBone spyglass = model.getAnimationProcessor().getBone("spyglass");
        spyglass.setHidden(!showSpyglass(animatable, instanceId));

        GeoBone banana = model.getAnimationProcessor().getBone("banana");
        banana.setHidden(!showBanana(animatable, instanceId));
    }

    default boolean showSunglasses(GeoEntity animatable, long instanceId) {
        return getCurrentAnim(animatable, instanceId) == BonziLikeEntity.IDLE_SUNGLASSES && animationPlaying(animatable, instanceId);
    }

    default boolean showGlobe(GeoEntity animatable, long instanceId) {
        return getCurrentAnim(animatable, instanceId) == BonziLikeEntity.IDLE_GLOBE && animationPlaying(animatable, instanceId);
    }

    default boolean showSpyglass(GeoEntity animatable, long instanceId) {
        return getCurrentAnim(animatable, instanceId) == BonziLikeEntity.IDLE_SPYGLASS && animationPlaying(animatable, instanceId);
    }

    default boolean showBanana(GeoEntity animatable, long instanceId) {
        return getCurrentAnim(animatable, instanceId) == BonziLikeEntity.IDLE_BANANA && animationPlaying(animatable, instanceId);
    }

    default RawAnimation getCurrentAnim(GeoEntity animatable, long instanceId) {
        return getDefaultAnimationController(animatable, instanceId).getCurrentRawAnimation();
    }

    default AnimationController<?> getDefaultAnimationController(GeoEntity animatable, long instanceId) {
        return animatable.getAnimatableInstanceCache().getManagerForId(instanceId).getAnimationControllers().get(BonziLikeEntity.animControllerName);
    }
}
