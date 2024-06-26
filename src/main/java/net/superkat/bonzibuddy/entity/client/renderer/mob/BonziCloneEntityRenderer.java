package net.superkat.bonzibuddy.entity.client.renderer.mob;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziCloneEntity;
import net.superkat.bonzibuddy.entity.client.model.mob.BonziCloneEntityModel;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class BonziCloneEntityRenderer extends GeoEntityRenderer<BonziCloneEntity> {
    public BonziCloneEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BonziCloneEntityModel());
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack, BonziCloneEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = (float) animatable.getAttributes().getValue(EntityAttributes.GENERIC_SCALE);
        poseStack.scale(scale, scale, scale);
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    public Color getRenderColor(BonziCloneEntity animatable, float partialTick, int packedLight) {
        int red = animatable.color.getRed();
        int green = animatable.color.getGreen();
        int blue = animatable.color.getBlue();
        return Color.ofRGB(red, green, blue);
    }
}
