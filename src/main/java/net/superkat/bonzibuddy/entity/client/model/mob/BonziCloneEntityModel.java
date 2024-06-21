package net.superkat.bonzibuddy.entity.client.model.mob;

import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziCloneEntity;
import net.superkat.bonzibuddy.entity.client.model.BonziLikeModel;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BonziCloneEntityModel extends DefaultedEntityGeoModel<BonziCloneEntity> implements BonziLikeModel {
    private final Identifier bananaBlasterDroppableTexture = buildFormattedTexturePath(Identifier.of(BonziBUDDY.MOD_ID, "bonzibananaclone"));
    public BonziCloneEntityModel() {
        super(Identifier.of(BonziBUDDY.MOD_ID, "bonzibuddy"), false);
        withAltTexture(Identifier.of(BonziBUDDY.MOD_ID, "bonziclone"));
    }

    @Override
    public Identifier getTextureResource(BonziCloneEntity animatable) {
        if(animatable.scale >= 2) {
            return bananaBlasterDroppableTexture;
        }
        return super.getTextureResource(animatable);
    }

    @Override
    public void setCustomAnimations(BonziCloneEntity animatable, long instanceId, AnimationState<BonziCloneEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            turnHead(animatable, entityData, head, instanceId);
            handleItemsVisibility(animatable, instanceId);
        }
    }
}
