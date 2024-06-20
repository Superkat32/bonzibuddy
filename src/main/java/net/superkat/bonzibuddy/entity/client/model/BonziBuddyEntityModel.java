package net.superkat.bonzibuddy.entity.client.model;

import net.minecraft.util.Identifier;
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

		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

			turnHead(animatable, entityData, head, instanceId);
			handleItemsVisibility(animatable, instanceId);
//			//Add animation head turning IN ADDITION WITH normal head looking
////			head.setRotX(head.getRotX() + entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
////			head.setRotY(head.getRotY() + entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
//
//			//Ignore animation head turning
//			head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
//			head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
		}
	}


}