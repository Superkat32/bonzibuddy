package net.superkat.bonzibuddy.entity.client.renderer.mob;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.bonzibuddy.entity.client.model.mob.BonziCloneEntityModel;
import net.superkat.bonzibuddy.entity.minigame.mob.BonziCloneEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BonziCloneEntityRenderer extends GeoEntityRenderer<BonziCloneEntity> {
    public BonziCloneEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BonziCloneEntityModel());
    }
}
