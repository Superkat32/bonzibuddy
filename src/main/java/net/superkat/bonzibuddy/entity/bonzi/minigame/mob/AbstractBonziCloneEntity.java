package net.superkat.bonzibuddy.entity.bonzi.minigame.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.entity.BonziBuddyEntities;
import net.superkat.bonzibuddy.entity.bonzi.BonziLikeEntity;
import net.superkat.bonzibuddy.entity.bonzi.minigame.ProtectBonziEntity;
import net.superkat.bonzibuddy.minigame.TripleChaosMinigame;
import org.jetbrains.annotations.Nullable;
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
    @Nullable
    public TripleChaosMinigame tripleChaosMinigame = null;
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
    public boolean tryAttack(Entity target) {
        if(!this.getWorld().isClient) {
//            triggerAnim(attackAnimControllerName, getAnimString(ATTACK_ANIM));
            syncTrigAnim(attackAnimControllerName, getAnimString(ATTACK_ANIM));
        }
        return super.tryAttack(target);
    }

    @Override
    public void onDamaged(DamageSource damageSource) {
        if (damageSource.isOf(BonziBuddyEntities.BANANA_DAMAGE)) {
            for (int i = 0; i < 20; i++) {
                float velX = (float) (this.random.nextFloat() * 2.0 - 1.0);
                float velY = (float) ((this.random.nextFloat() * 2.0 - 1.0) + 0.2);
                float velZ = (float) (this.random.nextFloat() * 2.0 - 1.0);
                this.getWorld().addParticle(ParticleTypes.TOTEM_OF_UNDYING, this.getX(), this.getEyeY(), this.getZ(), velX, velY, velZ);
            }
        }
        super.onDamaged(damageSource);
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

    public void setTripleChaosMinigame(@Nullable TripleChaosMinigame minigame) {
        this.tripleChaosMinigame = minigame;
    }

    public boolean inMinigame() {
        return tripleChaosMinigame != null && tripleChaosMinigame.isLoaded() && tripleChaosMinigame.onGoing();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
