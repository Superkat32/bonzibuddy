package net.superkat.bonzibuddy.entity.bonzi;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.network.packets.OpenBonziBuddyScreenS2C;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BonziBuddyEntity extends PathAwareEntity implements GeoEntity, BonziLikeEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    boolean shouldTurnHead = true;
    boolean showSunglasses = false;
    public int ticksUntilNextIdleAnim = 100;
    public int ticksSinceIdleAnim = 0;
    public int ticksUntilAnimDone = 0;
    public boolean readyForIdleAnim = false;
    public boolean victorySunglasses = false;
    public BonziBuddyEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if(!this.getWorld().isClient) {
            ServerPlayNetworking.send((ServerPlayerEntity) player, new OpenBonziBuddyScreenS2C(this.getId()));
        }
        return super.interactMob(player, hand);
    }

    public void playRandomIdleAnimation() {
        int i = this.getWorld().random.nextInt(idleAnimations().size());
        RawAnimation anim = idleAnimations().get(i);
        triggerAnim("controller", getAnimString(anim));
    }

    public void doATrick() {
        playRandomIdleAnimation();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, animControllerName, 5, this::animController)
                .triggerableAnim(getAnimString(IDLE_SUNGLASSES), IDLE_SUNGLASSES)
                .triggerableAnim(getAnimString(IDLE_GLOBE), IDLE_GLOBE)
                .triggerableAnim(getAnimString(IDLE_SPYGLASS), IDLE_SPYGLASS)
                .triggerableAnim(getAnimString(IDLE_BANANA), IDLE_BANANA)
        );
        controllers.add(new AnimationController<>(this, attackAnimControllerName, 5, this::attackAnimController)
                .triggerableAnim(getAnimString(ATTACK_ANIM), ATTACK_ANIM)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}