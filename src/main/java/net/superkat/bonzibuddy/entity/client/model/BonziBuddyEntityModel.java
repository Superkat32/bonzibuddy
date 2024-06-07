package net.superkat.bonzibuddy.entity.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.superkat.bonzibuddy.entity.BonziBuddyEntity;

// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class BonziBuddyEntityModel<T extends BonziBuddyEntity> extends SinglePartEntityModel<T> {
	private final ModelPart root;
//	private final ModelPart body;
//	private final ModelPart arms;
//	private final ModelPart leftarm;
//	private final ModelPart lefthand;
//	private final ModelPart rightarm;
//	private final ModelPart righthand;
//	private final ModelPart items;
//	private final ModelPart globe;
//	private final ModelPart banana;
//	private final ModelPart spyglass;
//	private final ModelPart bone;
//	private final ModelPart head;
//	private final ModelPart nose;
//	private final ModelPart sunglasses;
//	private final ModelPart legs;
//	private final ModelPart leftleg;
//	private final ModelPart rightleg;
	public BonziBuddyEntityModel(ModelPart root) {
		this.root = root.getChild("bonzibuddy"); //main folder in the blockbench project
//		this.body = this.root.getChild("body");
//		this.arms = this.body.getChild("arms");
//		this.leftarm = this.arms.getChild("leftarm");
//		this.lefthand = this.leftarm.getChild("lefthand");
//		this.rightarm = this.arms.getChild("rightarm");
//		this.righthand = this.rightarm.getChild("righthand");
//		this.items = this.arms.getChild("items");
//		this.globe = this.items.getChild("globe");
//		this.banana = this.items.getChild("banana");
//		this.spyglass = this.items.getChild("spyglass");
//		this.bone = this.spyglass.getChild("bone");
//		this.head = this.body.getChild("head");
//		this.nose = this.head.getChild("nose");
//		this.sunglasses = this.head.getChild("sunglasses");
//		this.legs = this.root.getChild("legs");
//		this.leftleg = this.legs.getChild("leftleg");
//		this.rightleg = this.legs.getChild("rightleg");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bonzibuddy = modelPartData.addChild("bonzibuddy", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData body = bonzibuddy.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -7.5F, -1.75F, 8.0F, 8.0F, 4.0F, new Dilation(0.0F))
		.uv(20, 12).cuboid(-4.0F, -5.5F, -2.75F, 8.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -4.5F, 1.75F));

		ModelPartData arms = body.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.5F, -1.75F));

		ModelPartData leftarm = arms.addChild("leftarm", ModelPartBuilder.create(), ModelTransform.pivot(4.0F, -11.0F, 2.0F));

		ModelPartData cube_r1 = leftarm.addChild("cube_r1", ModelPartBuilder.create().uv(10, 31).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 1.5736F, -1.8192F, -0.9599F, 0.0F, 0.0F));

		ModelPartData lefthand = leftarm.addChild("lefthand", ModelPartBuilder.create().uv(0, 28).cuboid(-5.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 2.7207F, -3.4575F, -0.9599F, 0.0F, 0.0F));

		ModelPartData rightarm = arms.addChild("rightarm", ModelPartBuilder.create(), ModelTransform.pivot(-4.0F, -11.0F, 2.0F));

		ModelPartData cube_r2 = rightarm.addChild("cube_r2", ModelPartBuilder.create().uv(24, 27).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 1.5736F, -1.8192F, -0.9599F, 0.0F, 0.0F));

		ModelPartData righthand = rightarm.addChild("righthand", ModelPartBuilder.create().uv(12, 27).cuboid(1.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.7207F, -3.4575F, -0.9599F, 0.0F, 0.0F));

		ModelPartData items = arms.addChild("items", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData globe = items.addChild("globe", ModelPartBuilder.create().uv(16, 19).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -7.0F, 1.8F));

		ModelPartData banana = items.addChild("banana", ModelPartBuilder.create().uv(0, 12).cuboid(5.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(18, 31).cuboid(5.0F, -5.0F, 0.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, -6.0F, 1.0F));

		ModelPartData cube_r3 = banana.addChild("cube_r3", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -3.0F, -1.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -1.0F, 1.0F, 0.0F, 0.0F, -0.2618F));

		ModelPartData cube_r4 = banana.addChild("cube_r4", ModelPartBuilder.create().uv(2, 0).cuboid(0.0F, -3.0F, -1.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 1.0F, 0.0F, 0.0F, 0.2618F));

		ModelPartData cube_r5 = banana.addChild("cube_r5", ModelPartBuilder.create().uv(16, 12).cuboid(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 1.0F, -0.2618F, 0.0F, 0.0F));

		ModelPartData cube_r6 = banana.addChild("cube_r6", ModelPartBuilder.create().uv(18, 12).cuboid(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

		ModelPartData spyglass = items.addChild("spyglass", ModelPartBuilder.create().uv(10, 46).cuboid(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -7.0F, 1.5F, -1.5708F, 0.0F, 0.0F));

		ModelPartData bone = spyglass.addChild("bone", ModelPartBuilder.create().uv(0, 47).cuboid(-1.0F, -2.0F, -10.0F, 2.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 1.5F));

		ModelPartData head = body.addChild("head", ModelPartBuilder.create().uv(0, 12).cuboid(-3.0F, -7.0F, -1.875F, 6.0F, 7.0F, 4.0F, new Dilation(0.0F))
		.uv(24, 5).cuboid(-3.0F, -3.0F, -2.875F, 6.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(2, 0).cuboid(3.0F, -4.0F, -0.875F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-4.0F, -4.0F, -0.875F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -7.5F, 0.125F));

		ModelPartData nose = head.addChild("nose", ModelPartBuilder.create().uv(20, 0).cuboid(-1.0F, -16.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 12.0F, -1.875F));

		ModelPartData sunglasses = head.addChild("sunglasses", ModelPartBuilder.create().uv(0, 40).cuboid(-4.0F, -1.5F, -1.5F, 8.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.5F, 0.525F, 0.0F, 0.0F, -1.5708F));

		ModelPartData legs = bonzibuddy.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData leftleg = legs.addChild("leftleg", ModelPartBuilder.create().uv(32, 19).cuboid(-1.25F, -1.5F, -1.5F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F))
		.uv(24, 0).cuboid(-1.25F, 1.5F, -5.5F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.75F, -2.5F, 2.5F));

		ModelPartData rightleg = legs.addChild("rightleg", ModelPartBuilder.create().uv(0, 32).cuboid(2.0F, -4.0F, 1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 23).cuboid(1.0F, -1.0F, -3.0F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public ModelPart getPart() {
		return root;
	}

	@Override
	public void setAngles(BonziBuddyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		//this line is so important otherwise Bonzi Buddy's model freaking dies and explodes
		this.getPart().traverse().forEach(ModelPart::resetTransform);

		this.updateAnimation(entity.idleAnimState, BonziBuddyAnimations.idlemain, ageInTicks);
		this.updateAnimation(entity.sunglassAnimState, BonziBuddyAnimations.idlesunglasses, ageInTicks);
	}

}