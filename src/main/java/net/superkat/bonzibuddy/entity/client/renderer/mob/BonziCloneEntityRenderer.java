package net.superkat.bonzibuddy.entity.client.renderer.mob;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziCloneEntity;
import net.superkat.bonzibuddy.entity.client.model.mob.BonziCloneEntityModel;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BonziCloneEntityRenderer extends GeoEntityRenderer<BonziCloneEntity> {
    public BonziCloneEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BonziCloneEntityModel());
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack, BonziCloneEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = animatable.scale;
        poseStack.scale(scale, scale, scale);
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
