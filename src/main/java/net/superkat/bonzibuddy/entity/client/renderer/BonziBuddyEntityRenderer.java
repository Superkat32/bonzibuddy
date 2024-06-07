package net.superkat.bonzibuddy.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.BonziBuddyEntity;
import net.superkat.bonzibuddy.entity.client.BonziBuddyModelLayers;
import net.superkat.bonzibuddy.entity.client.model.BonziBuddyEntityModel;

public class BonziBuddyEntityRenderer extends MobEntityRenderer<BonziBuddyEntity, BonziBuddyEntityModel<BonziBuddyEntity>> {
    private static final Identifier TEXTURE = Identifier.of(BonziBUDDY.MOD_ID, "textures/entity/bonzibuddy.png");
    public BonziBuddyEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BonziBuddyEntityModel(context.getPart(BonziBuddyModelLayers.BONZI_BUDDY_LAYER)), 0.6f);
    }

    @Override
    public Identifier getTexture(BonziBuddyEntity entity) {
        return TEXTURE;
    }
}
