package net.superkat.bonzibuddy.entity.client.renderer.mob;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.client.BonziBuddyModelLayers;
import net.superkat.bonzibuddy.entity.client.model.mob.BonziCloneEntityModel;
import net.superkat.bonzibuddy.entity.mob.BonziCloneEntity;

public class BonziCloneEntityRenderer extends MobEntityRenderer<BonziCloneEntity, BonziCloneEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(BonziBUDDY.MOD_ID, "textures/entity/bonziclone.png");
    public BonziCloneEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BonziCloneEntityModel(context.getPart(BonziBuddyModelLayers.BONZI_BUDDY_LAYER)), 0.6f);
    }

    @Override
    public Identifier getTexture(BonziCloneEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(BonziCloneEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
