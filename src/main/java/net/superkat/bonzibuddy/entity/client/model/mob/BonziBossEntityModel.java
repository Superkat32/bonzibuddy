package net.superkat.bonzibuddy.entity.client.model.mob;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziBossEntity;
import net.superkat.bonzibuddy.entity.client.model.BonziLikeModel;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BonziBossEntityModel extends DefaultedEntityGeoModel<BonziBossEntity> implements BonziLikeModel {
    public BonziBossEntityModel() {
        super(Identifier.of(BonziBUDDY.MOD_ID, "bonzibuddy"), false);
        withAltTexture(Identifier.of(BonziBUDDY.MOD_ID, "bonziclone"));
    }

    @Override
    public void setCustomAnimations(BonziBossEntity animatable, long instanceId, AnimationState<BonziBossEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(head.getRotX() + entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(head.getRotY() + entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
