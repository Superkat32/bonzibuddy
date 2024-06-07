package net.superkat.bonzibuddy.entity.client.model;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

/**
 * Made with Blockbench 4.10.3
 * Exported for Minecraft version 1.19 or later with Yarn mappings
 * @author Superkat
 */
public class BonziBuddyAnimations {
	public static final Animation idlemain = Animation.Builder.create(10.0417F).looping()
		.addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, -52.5F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.9167F, AnimationHelper.createRotationalVector(0.0F, -52.5F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(2.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(4.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(4.6667F, AnimationHelper.createRotationalVector(-5.3453F, -14.7669F, 2.664F), Transformation.Interpolations.LINEAR),
			new Keyframe(5.6667F, AnimationHelper.createRotationalVector(-5.35F, -14.77F, 2.66F), Transformation.Interpolations.LINEAR),
			new Keyframe(5.8333F, AnimationHelper.createRotationalVector(5.0582F, -27.2252F, 1.5297F), Transformation.Interpolations.LINEAR),
			new Keyframe(6.0F, AnimationHelper.createRotationalVector(4.086F, 15.9729F, 4.6841F), Transformation.Interpolations.LINEAR),
			new Keyframe(6.1667F, AnimationHelper.createRotationalVector(5.6472F, -37.182F, 0.4286F), Transformation.Interpolations.LINEAR),
			new Keyframe(6.3333F, AnimationHelper.createRotationalVector(4.086F, 15.9729F, 4.6841F), Transformation.Interpolations.CUBIC),
			new Keyframe(6.5F, AnimationHelper.createRotationalVector(-5.35F, -14.77F, 2.66F), Transformation.Interpolations.CUBIC),
			new Keyframe(7.125F, AnimationHelper.createRotationalVector(-5.3453F, -14.7669F, 2.664F), Transformation.Interpolations.LINEAR),
			new Keyframe(7.7083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.0F, -15.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.9167F, AnimationHelper.createRotationalVector(0.0F, -15.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(2.375F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(4.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(4.6667F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(7.125F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(7.7083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(8.4167F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.625F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.7917F, AnimationHelper.createTranslationalVector(0.0F, 0.3F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(9.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("arms", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(8.4167F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.625F, AnimationHelper.createTranslationalVector(0.0F, 0.7F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.7917F, AnimationHelper.createTranslationalVector(0.0F, 0.7F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(9.1667F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("leftarm", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(4.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(4.6667F, AnimationHelper.createRotationalVector(17.2522F, -20.8384F, -18.4734F), Transformation.Interpolations.LINEAR),
			new Keyframe(7.125F, AnimationHelper.createRotationalVector(17.2522F, -20.8384F, -18.4734F), Transformation.Interpolations.LINEAR),
			new Keyframe(7.6667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.0417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.4167F, AnimationHelper.createRotationalVector(-100.0F, 0.0F, 120.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(9.1667F, AnimationHelper.createRotationalVector(-100.0F, 0.0F, 120.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(9.5417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("rightarm", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(4.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(4.6667F, AnimationHelper.createRotationalVector(-91.2702F, -30.9817F, -55.3943F), Transformation.Interpolations.LINEAR),
			new Keyframe(7.125F, AnimationHelper.createRotationalVector(-91.2702F, -30.9817F, -55.3943F), Transformation.Interpolations.LINEAR),
			new Keyframe(7.6667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.0417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.4167F, AnimationHelper.createRotationalVector(-110.0F, 0.0F, -120.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(9.1667F, AnimationHelper.createRotationalVector(-110.0F, 0.0F, -120.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(9.5417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.build();

	public static final Animation idlesunglasses = Animation.Builder.create(11.0F)
		.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(2.5F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(2.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("leftarm", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.4167F, AnimationHelper.createRotationalVector(34.7458F, -35.6195F, -27.315F), Transformation.Interpolations.LINEAR),
			new Keyframe(2.0417F, AnimationHelper.createRotationalVector(34.7458F, -35.6195F, -27.315F), Transformation.Interpolations.LINEAR),
			new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.4167F, AnimationHelper.createRotationalVector(34.7458F, -35.6195F, -27.315F), Transformation.Interpolations.LINEAR),
			new Keyframe(10.0417F, AnimationHelper.createRotationalVector(34.7458F, -35.6195F, -27.315F), Transformation.Interpolations.LINEAR),
			new Keyframe(10.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("rightarm", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.4583F, AnimationHelper.createRotationalVector(33.001F, 34.2962F, 56.3477F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.75F, AnimationHelper.createRotationalVector(35.7691F, -6.7553F, 45.6806F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.0F, AnimationHelper.createRotationalVector(35.7691F, -6.7553F, 45.6806F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.5F, AnimationHelper.createRotationalVector(32.165F, 51.0519F, 62.8667F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.0F, AnimationHelper.createRotationalVector(-57.7801F, -3.5702F, -68.3406F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.25F, AnimationHelper.createRotationalVector(-70.4473F, -13.917F, -50.9969F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.3333F, AnimationHelper.createRotationalVector(-70.4473F, -13.917F, -50.9969F), Transformation.Interpolations.CUBIC),
			new Keyframe(8.75F, AnimationHelper.createRotationalVector(-57.7801F, -3.5702F, -68.3406F), Transformation.Interpolations.CUBIC),
			new Keyframe(9.3333F, AnimationHelper.createRotationalVector(32.165F, 51.0519F, 62.8667F), Transformation.Interpolations.CUBIC),
			new Keyframe(9.625F, AnimationHelper.createRotationalVector(35.7691F, -6.7553F, 45.6806F), Transformation.Interpolations.CUBIC),
			new Keyframe(9.9167F, AnimationHelper.createRotationalVector(35.7691F, -6.7553F, 45.6806F), Transformation.Interpolations.CUBIC),
			new Keyframe(10.2083F, AnimationHelper.createRotationalVector(33.001F, 34.2962F, 56.3477F), Transformation.Interpolations.LINEAR),
			new Keyframe(10.625F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("head", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 15.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 15.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(2.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(4.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(4.9583F, AnimationHelper.createRotationalVector(0.0F, 30.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(5.5417F, AnimationHelper.createRotationalVector(0.0F, 30.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(5.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("sunglasses", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 90.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -40.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -82.5F), Transformation.Interpolations.LINEAR),
			new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -262.5F), Transformation.Interpolations.LINEAR),
			new Keyframe(2.25F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -270.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -270.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.75F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -262.5F), Transformation.Interpolations.LINEAR),
			new Keyframe(9.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -82.5F), Transformation.Interpolations.LINEAR),
			new Keyframe(9.625F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -40.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(10.625F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 90.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("sunglasses", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0F, AnimationHelper.createTranslationalVector(-3.0F, -3.0F, -1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.5F, AnimationHelper.createTranslationalVector(-6.5F, -1.4F, -5.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(1.75F, AnimationHelper.createTranslationalVector(-1.22F, 0.7F, -6.25F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.0F, AnimationHelper.createTranslationalVector(-1.5F, 6.6F, -5.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(2.25F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, -1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.2917F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, -1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.75F, AnimationHelper.createTranslationalVector(-1.5F, 6.6F, -5.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(9.0417F, AnimationHelper.createTranslationalVector(-1.22F, 0.7F, -6.25F), Transformation.Interpolations.CUBIC),
			new Keyframe(9.3333F, AnimationHelper.createTranslationalVector(-6.5F, -1.4F, -5.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(9.625F, AnimationHelper.createTranslationalVector(-3.0F, -3.0F, -1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(10.625F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("sunglasses", new Transformation(Transformation.Targets.SCALE, 
			new Keyframe(0.75F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(9.625F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(9.9167F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.build();
}