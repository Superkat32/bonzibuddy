package net.superkat.bonzibuddy.entity.client.model.mob;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.superkat.bonzibuddy.entity.bonzi.BonziLikeEntity;
import net.superkat.bonzibuddy.entity.client.model.BonziBuddyAnimations;
import net.superkat.bonzibuddy.entity.client.model.BonziLikeModel;
import net.superkat.bonzibuddy.entity.minigame.mob.BonziCloneEntity;

import java.awt.*;

public class BonziCloneEntityModel extends SinglePartEntityModel<BonziCloneEntity> implements BonziLikeModel {
    public Color entityColor;
    private final ModelPart bonzibuddy;
    private final ModelPart body;
    private final ModelPart arms;
    private final ModelPart items;
    private final ModelPart head;
    private final ModelPart sunglasses;
    public BonziCloneEntityModel(ModelPart root) {
        this.bonzibuddy = root.getChild("bonzibuddy");
        this.body = bonzibuddy.getChild("body");
        this.arms = body.getChild("arms");
        this.items = arms.getChild("items");
        this.head = body.getChild("head");
        this.sunglasses = head.getChild("sunglasses");
    }

    @Override
    public ModelPart getPart() {
        return this.bonzibuddy;
    }

    @Override
    public void setAngles(BonziCloneEntity entity, float limbAngle, float limbDistance, float ageInTicks, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        //This method gets called a few lines before the render method.
        //Even though this may seem slightly "hacky", I think it is nicer than the tropical fish's implementation,
        //which sets a variable in the model from the renderer, which gets used in the model's render method.
        this.entityColor = entity.color;

        updateAnimationStates(entity, this, ageInTicks, headYaw, headPitch);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        super.render(matrices, vertices, light, overlay, entityColor.getRGB());
    }

    @Override
    public ModelPart head() {
        return this.head;
    }

    @Override
    public ModelPart items() {
        return this.items;
    }

    @Override
    public ModelPart sunglasses() {
        return this.sunglasses;
    }

    @Override
    public void updateBonziAnimation(AnimationState state, Animation animation, float animationProgress) {
        this.updateAnimation(state, animation, animationProgress);
    }

    @Override
    public void updateIdleAnimationStates(BonziLikeEntity entity, float ageInTicks) {
        //Limit the idle animations
        updateBonziAnimation(entity.idleSunglassAnimState(), BonziBuddyAnimations.IDLE_SUNGLASSES, ageInTicks);
        updateBonziAnimation(entity.idleSpyglassAnimState(), BonziBuddyAnimations.IDLE_SPYGLASS, ageInTicks);
        updateBonziAnimation(entity.idleBananaAnimState(), BonziBuddyAnimations.IDLE_BANANA, ageInTicks);
    }
}
