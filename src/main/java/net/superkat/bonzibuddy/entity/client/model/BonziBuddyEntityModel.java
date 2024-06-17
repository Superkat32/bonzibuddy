package net.superkat.bonzibuddy.entity.client.model;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.bonzi.BonziBuddyEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BonziBuddyEntityModel extends DefaultedEntityGeoModel<BonziBuddyEntity> implements BonziLikeModel {
	public BonziBuddyEntityModel() {
		super(Identifier.of(BonziBUDDY.MOD_ID, "bonzibuddy"), false);
	}

	@Override
	public void setCustomAnimations(BonziBuddyEntity animatable, long instanceId, AnimationState<BonziBuddyEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);
		GeoBone head = getAnimationProcessor().getBone("head");

		//FIXME - add boolean for hiding items in BonziLikeEntity

//		boolean showSunglasses = animationState.isCurrentAnimation(BonziLikeEntity.IDLE_SUNGLASSES);
//		boolean showGlobe = animationState.isCurrentAnimation(BonziLikeEntity.IDLE_GLOBE);
//		boolean showSpyglass = animationState.isCurrentAnimation(BonziLikeEntity.IDLE_SPYGLASS);
//		boolean showBanana = animationState.isCurrentAnimation(BonziLikeEntity.IDLE_BANANA);
//		getAnimationProcessor().getBone("sunglasses").setHidden(!showSunglasses);
//		getAnimationProcessor().getBone("globe").setHidden(!showGlobe);
//		getAnimationProcessor().getBone("spyglass").setHidden(!showSpyglass);
//		getAnimationProcessor().getBone("banana").setHidden(!showBanana);

		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

			//Add animation head turning IN ADDITION WITH normal head looking
//			head.setRotX(head.getRotX() + entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
//			head.setRotY(head.getRotY() + entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);

			//Ignore animation head turning
			head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
			head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
		}
	}
}