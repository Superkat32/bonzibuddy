package net.superkat.bonzibuddy.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.bonzibuddy.entity.bonzi.BonziBuddyEntity;
import net.superkat.bonzibuddy.entity.client.model.BonziBuddyEntityModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BonziBuddyEntityRenderer extends GeoEntityRenderer<BonziBuddyEntity> {
    public BonziBuddyEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BonziBuddyEntityModel());
    }
}
