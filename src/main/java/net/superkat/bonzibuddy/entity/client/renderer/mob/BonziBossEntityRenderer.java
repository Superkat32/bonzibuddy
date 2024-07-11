package net.superkat.bonzibuddy.entity.client.renderer.mob;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziBossEntity;
import net.superkat.bonzibuddy.entity.client.model.mob.BonziBossEntityModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BonziBossEntityRenderer extends GeoEntityRenderer<BonziBossEntity> {
    public BonziBossEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BonziBossEntityModel());
    }

    public BonziBossEntityRenderer(EntityRendererFactory.Context renderManager, BonziBossEntityModel model) {
        super(renderManager, model);
    }
}
