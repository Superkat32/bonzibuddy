package net.superkat.bonzibuddy.entity.client.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WindChargeEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.client.BonziBuddyModelLayers;
import net.superkat.bonzibuddy.entity.client.model.BananaBlasterEntityModel;

public class BananaBlasterEntityRenderer extends WindChargeEntityRenderer {
    private static final float animateLimit = MathHelper.square(3.5F);
    private static final Identifier TEXTURE = Identifier.of(BonziBUDDY.MOD_ID, "textures/entity/banana_blaster_projectile.png");
    private final BananaBlasterEntityModel model;
    public BananaBlasterEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new BananaBlasterEntityModel(context.getPart(BonziBuddyModelLayers.BANANA_BLASTER_LAYER));
    }

    public void render(
            AbstractWindChargeEntity abstractWindChargeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
    ) {
        if (abstractWindChargeEntity.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(abstractWindChargeEntity) < (double) animateLimit)) {
            float h = (float)abstractWindChargeEntity.age + g;
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getBreezeWind(TEXTURE, 0, 0.0F));
//            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getBreezeWind(TEXTURE, this.getXOffset(h) % 1.0F, 0.0F));
            this.model.setAngles(abstractWindChargeEntity, 0.0F, 0.0F, h, h, 0f);
            this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
//            super.render(abstractWindChargeEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    @Override
    public Identifier getTexture(AbstractWindChargeEntity abstractWindChargeEntity) {
        return TEXTURE;
    }
}
