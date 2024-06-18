package net.superkat.bonzibuddy.entity.client.renderer.mob;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziBossEntity;
import net.superkat.bonzibuddy.entity.client.model.mob.BonziBossEntityModel;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BonziBossEntityRenderer extends GeoEntityRenderer<BonziBossEntity> {
    public BonziBossEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BonziBossEntityModel());
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack, BonziBossEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = (float) animatable.getAttributes().getValue(EntityAttributes.GENERIC_SCALE);
        poseStack.scale(scale, scale, scale);
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
