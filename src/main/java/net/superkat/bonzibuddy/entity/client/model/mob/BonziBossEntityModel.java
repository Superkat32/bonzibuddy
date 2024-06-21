package net.superkat.bonzibuddy.entity.client.model.mob;

import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziBossEntity;
import net.superkat.bonzibuddy.entity.client.model.BonziLikeModel;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BonziBossEntityModel extends DefaultedEntityGeoModel<BonziBossEntity> implements BonziLikeModel {
    private final Identifier redBonziBossTexture = buildFormattedTexturePath(Identifier.of(BonziBUDDY.MOD_ID, "redbonziboss"));
    private final Identifier greenBonziBossTexture = buildFormattedTexturePath(Identifier.of(BonziBUDDY.MOD_ID, "greenbonziboss"));
    private final Identifier blueBonziBossTexture = buildFormattedTexturePath(Identifier.of(BonziBUDDY.MOD_ID, "bluebonziboss"));
    public BonziBossEntityModel() {
        super(Identifier.of(BonziBUDDY.MOD_ID, "bonzibuddy"), false);
        withAltTexture(Identifier.of(BonziBUDDY.MOD_ID, "bonziclone"));
    }

    @Override
    public Identifier getTextureResource(BonziBossEntity animatable) {
        if(animatable.isRed()) {
            return redBonziBossTexture;
        } else if (animatable.isGreen()) {
            return greenBonziBossTexture;
        } else if (animatable.isBlue()) {
            return blueBonziBossTexture;
        }
        return super.getTextureResource(animatable);
    }

    @Override
    public void setCustomAnimations(BonziBossEntity animatable, long instanceId, AnimationState<BonziBossEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            turnHead(animatable, entityData, head, instanceId);
            handleItemsVisibility(animatable, instanceId);
        }
    }
}
