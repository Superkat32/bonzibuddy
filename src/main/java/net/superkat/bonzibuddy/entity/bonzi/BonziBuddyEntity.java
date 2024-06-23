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
    public int ticksUntilIdleAnim = 100;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
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

    @Override
    public void tick() {
        if(!this.getWorld().isClient) {
            ticksUntilIdleAnim--;
            if(ticksUntilIdleAnim <= 0) {
                ticksUntilIdleAnim = this.random.nextBetween(60, 200);
                if(this.getAnimatableInstanceCache().getManagerForId(this.getId()).getAnimationControllers().get(animControllerName).getCurrentRawAnimation() == null) {
                    playRandomIdleAnimation();
                }
            }
        }
        super.tick();
    }

    public void playRandomIdleAnimation() {
        int i = this.getWorld().random.nextInt(idleAnimations().size());
        RawAnimation anim = idleAnimations().get(i);
//        triggerAnim(animControllerName, getAnimString(anim));
        syncTrigAnim(animControllerName, getAnimString(anim), true);
    }

    @Override
    public boolean deathAnim() {
        return true;
    }

    public void doATrick() {
        int i = this.getWorld().random.nextInt(trickAnimations.size());
        RawAnimation anim = trickAnimations.get(i);
        if(anim == BonziLikeEntity.DEATH_ANIM) {
            //reroll making the odds of this animation much less likely
            //With 4 other trick animations, the odds are 1/3125 (1 in 5^5), or 0.00032% chances
            i = this.getWorld().random.nextInt(idleAnimations().size());
            anim = idleAnimations().get(i);
        }
//        triggerAnim(animControllerName, getAnimString(anim));
        syncTrigAnim(animControllerName, getAnimString(anim));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, animControllerName, 5, this::animController)
                .triggerableAnim(getAnimString(IDLE_SUNGLASSES), IDLE_SUNGLASSES)
                .triggerableAnim(getAnimString(IDLE_GLOBE), IDLE_GLOBE)
                .triggerableAnim(getAnimString(IDLE_SPYGLASS), IDLE_SPYGLASS)
                .triggerableAnim(getAnimString(IDLE_BANANA), IDLE_BANANA)
                .triggerableAnim(getAnimString(TRICK_GLOBE), TRICK_GLOBE)
                .triggerableAnim(getAnimString(TRICK_SHRINK), TRICK_SHRINK)
                .triggerableAnim(getAnimString(DEATH_ANIM), DEATH_ANIM)
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
