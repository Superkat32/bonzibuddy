package net.superkat.bonzibuddy.entity.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.util.math.RotationAxis;

public class BananaBlasterEntityModel extends EntityModel<AbstractWindChargeEntity> {
    private final ModelPart bananablaster;

    public BananaBlasterEntityModel(ModelPart root) {
        this.bananablaster = root.getChild("bananablaster");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bananablaster = modelPartData.addChild("bananablaster", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData bone = bananablaster.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -9.0F, 9.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(16, 0).cuboid(-2.0F, -7.0F, 7.0F, 4.0F, 5.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 12).cuboid(-2.0F, -5.0F, 3.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 8.0F, new Dilation(0.0F))
                .uv(16, 12).cuboid(-2.0F, -5.0F, -7.0F, 4.0F, 4.0F, 2.0F, new Dilation(0.0F))
                .uv(16, 18).cuboid(-1.0F, -6.0F, -9.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -3.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(AbstractWindChargeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.bananablaster.yaw = headYaw;
        this.bananablaster.pitch = headPitch;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.translate(0, -1.2f, 0);
        this.bananablaster.render(matrices, vertices, light, overlay);
        matrices.pop();
    }
}
