package net.superkat.bonzibuddy.entity.client.model.mob;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.superkat.bonzibuddy.entity.mob.BonziCloneEntity;

import java.awt.*;

public class BonziCloneEntityModel extends SinglePartEntityModel<BonziCloneEntity> {
    public Color entityColor;
    private final ModelPart bonzibuddy;
    private final ModelPart body;
    private final ModelPart arms;
    //	private final ModelPart leftarm;
//	private final ModelPart lefthand;
//	private final ModelPart rightarm;
//	private final ModelPart righthand;
    private final ModelPart items;
    //	private final ModelPart globe;
//	private final ModelPart banana;
//	private final ModelPart bone2;
//	private final ModelPart spyglass;
//	private final ModelPart bone;
    private final ModelPart head;
    //	private final ModelPart nose;
    private final ModelPart sunglasses;
    //	private final ModelPart legs;
//	private final ModelPart leftleg;
//	private final ModelPart rightleg;
    public BonziCloneEntityModel(ModelPart root) {
        this.bonzibuddy = root.getChild("bonzibuddy");
        this.body = bonzibuddy.getChild("body");
        this.arms = body.getChild("arms");
//		this.leftarm = root.getChild("leftarm");
//		this.lefthand = root.getChild("lefthand");
//		this.rightarm = root.getChild("rightarm");
//		this.righthand = root.getChild("righthand");
        this.items = arms.getChild("items");
//		this.globe = root.getChild("globe");
//		this.banana = root.getChild("banana");
//		this.bone2 = root.getChild("bone2");
//		this.spyglass = items.getChild("spyglass");
//		this.bone = root.getChild("bone");
        this.head = body.getChild("head");
//		this.nose = root.getChild("nose");
        this.sunglasses = head.getChild("sunglasses");
//		this.legs = root.getChild("legs");
//		this.leftleg = root.getChild("leftleg");
//		this.rightleg = root.getChild("rightleg");
    }

    @Override
    public ModelPart getPart() {
        return this.bonzibuddy;
    }

    @Override
    public void setAngles(BonziCloneEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        //This method gets called a few lines before the render method.
        //Even though this may seem slightly "hacky", I think it is nicer than the tropical fish's implementation,
        //which sets a variable in the model from the renderer, which gets used in the model's render method.
        this.entityColor = entity.color;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        super.render(matrices, vertices, light, overlay, entityColor.getRGB());
    }
}
