package net.superkat.bonzibuddy.entity.bonzi.minigame.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.entity.bonzi.BonziLikeEntity;
import net.superkat.bonzibuddy.entity.bonzi.minigame.ProtectBonziEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Class to contain methods for interacting with the (Catastrophic Clones?) Bonzi minigame
 */
public abstract class AbstractBonziCloneEntity extends HostileEntity implements GeoEntity, BonziLikeEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    boolean shouldTurnHead = true;
    boolean showSunglasses = false;
    public int ticksUntilNextIdleAnim = 100;
    public int ticksSinceIdleAnim = 0;
    public int ticksUntilAnimDone = 0;
    public boolean readyForIdleAnim = false;
    public boolean victorySunglasses = false;
    public AbstractBonziCloneEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1, false));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8f, 0.5f));
        this.goalSelector.add(3, new LookAtEntityGoal(this, ProtectBonziEntity.class, 16f, 0.5f));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, ProtectBonziEntity.class, false));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean tryAttack(Entity target) {
        if(!this.getWorld().isClient) {
            triggerAnim(attackAnimControllerName, getAnimString(ATTACK_ANIM));
        }
        return super.tryAttack(target);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::animController)
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
