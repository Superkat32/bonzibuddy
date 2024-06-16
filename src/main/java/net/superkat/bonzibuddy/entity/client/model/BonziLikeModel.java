package net.superkat.bonzibuddy.entity.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.AnimationState;
import net.superkat.bonzibuddy.entity.bonzi.BonziLikeEntity;

public interface BonziLikeModel {
    ModelPart head();
    ModelPart items();
    ModelPart sunglasses();

    void updateBonziAnimation(AnimationState state, Animation animation, float animationProgress);
    default void updateAnimationStates(BonziLikeEntity entity, SinglePartEntityModel<?> model, float ageInTicks, float netHeadYaw, float headPitch) {
        //this line is so important otherwise Bonzi Buddy's model freaking dies and explodes
        model.getPart().traverse().forEach(ModelPart::resetTransform);
        if(entity.shouldTurnHead()) {
            head().yaw = netHeadYaw * (float) (Math.PI / 180.0);
            head().pitch = headPitch * (float) (Math.PI / 180.0);
            items().visible = false; //workaround for hiding the items & sunglasses inside of Bonzi Buddy's model
            sunglasses().visible = entity.showSunglasses();
        } else {
            items().visible = true;
            sunglasses().visible = true;
        }

        updateIdleAnimationStates(entity, ageInTicks);
        updateBonziAnimation(entity.walkAnimState(), BonziBuddyAnimations.CLONE_WALK, ageInTicks);
        updateBonziAnimation(entity.attackAnimState(), BonziBuddyAnimations.CLONE_ATTACK, ageInTicks);
        updateBonziAnimation(entity.victorySunglassesAnimState(), BonziBuddyAnimations.IDLE_SUNGLASSES, ageInTicks);
        updateBonziAnimation(entity.deathAnimState(), BonziBuddyAnimations.YIKES, ageInTicks);
    }
    default void updateIdleAnimationStates(BonziLikeEntity entity, float ageInTicks) {
        updateBonziAnimation(entity.idleAnimState(), BonziBuddyAnimations.IDLE_MAIN, ageInTicks);
        updateBonziAnimation(entity.idleSunglassAnimState(), BonziBuddyAnimations.IDLE_SUNGLASSES, ageInTicks);
        updateBonziAnimation(entity.idleGlobeAnimState(), BonziBuddyAnimations.IDLE_GLOBE, ageInTicks);
        updateBonziAnimation(entity.idleSpyglassAnimState(), BonziBuddyAnimations.IDLE_SPYGLASS, ageInTicks);
        updateBonziAnimation(entity.idleBananaAnimState(), BonziBuddyAnimations.IDLE_BANANA, ageInTicks);
    }

    static TexturedModelData getBonziModelData() {
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

        ModelPartData banana = items.addChild("banana", ModelPartBuilder.create().uv(0, 12).cuboid(5.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, -6.0F, 1.0F));

        ModelPartData cube_r3 = banana.addChild("cube_r3", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -3.0F, -1.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -1.0F, 1.0F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r4 = banana.addChild("cube_r4", ModelPartBuilder.create().uv(2, 0).cuboid(0.0F, -3.0F, -1.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 1.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData cube_r5 = banana.addChild("cube_r5", ModelPartBuilder.create().uv(16, 12).cuboid(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 1.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r6 = banana.addChild("cube_r6", ModelPartBuilder.create().uv(18, 12).cuboid(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone2 = banana.addChild("bone2", ModelPartBuilder.create().uv(18, 31).cuboid(5.0F, -5.0F, 0.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData spyglass = items.addChild("spyglass", ModelPartBuilder.create().uv(10, 46).cuboid(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -7.0F, 1.5F, -1.5708F, 0.0F, 0.0F));

        ModelPartData bone = spyglass.addChild("bone", ModelPartBuilder.create().uv(0, 47).cuboid(-1.0F, -2.0F, -10.0F, 2.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.0F, 1.5F));

        ModelPartData head = body.addChild("head", ModelPartBuilder.create().uv(0, 12).cuboid(-3.0F, -7.0F, -1.875F, 6.0F, 7.0F, 4.0F, new Dilation(0.0F))
                .uv(24, 5).cuboid(-3.0F, -3.0F, -2.875F, 6.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(2, 0).cuboid(3.0F, -4.0F, -0.875F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-4.0F, -4.0F, -0.875F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -7.5F, 0.125F));

        ModelPartData nose = head.addChild("nose", ModelPartBuilder.create().uv(20, 0).cuboid(-1.0F, -16.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 12.0F, -1.875F));

        ModelPartData sunglasses = head.addChild("sunglasses", ModelPartBuilder.create().uv(0, 40).cuboid(-4.0F, -1.5F, -1.5F, 8.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.5F, 0.525F, 0.0F, 0.0F, -1.5708F));

        ModelPartData legs = bonzibuddy.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData leftleg = legs.addChild("leftleg", ModelPartBuilder.create().uv(32, 19).cuboid(-1.25F, 0.0F, -0.9F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F))
                .uv(24, 0).cuboid(-1.25F, 3.0F, -4.9F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.75F, -4.0F, 1.9F));

        ModelPartData rightleg = legs.addChild("rightleg", ModelPartBuilder.create().uv(0, 32).cuboid(-0.75F, 0.0F, -0.9F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 23).cuboid(-1.75F, 3.0F, -4.9F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.75F, -4.0F, 1.9F));
        return TexturedModelData.of(modelData, 64, 64);
    }
}
