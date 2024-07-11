package net.superkat.bonzibuddy.entity.client.renderer.mob;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziCloneEntity;
import net.superkat.bonzibuddy.entity.client.model.mob.BonziCloneEntityModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class BonziCloneEntityRenderer extends GeoEntityRenderer<BonziCloneEntity> {
    public BonziCloneEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BonziCloneEntityModel());
    }

    @Override
    public Color getRenderColor(BonziCloneEntity animatable, float partialTick, int packedLight) {
        int red = animatable.color.getRed();
        int green = animatable.color.getGreen();
        int blue = animatable.color.getBlue();
        return Color.ofRGB(red, green, blue);
    }
}
