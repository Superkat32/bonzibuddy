package net.superkat.bonzibuddy.entity;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.AbstractBonziCloneEntity;

import java.util.Optional;
import java.util.function.Function;

public class BananaBlasterEntity extends AbstractWindChargeEntity {
    float gravity = 0.01f;

    private static final ExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
            true, false, Optional.of(1.22F), Registries.BLOCK.getEntryList(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
    );

    public BananaBlasterEntity(EntityType<? extends AbstractWindChargeEntity> entityType, World world) {
        super(entityType, world);
    }

    public BananaBlasterEntity(PlayerEntity player, World world, double x, double y, double z) {
        super(BonziBuddyEntities.BANANA_BLASTER_PROJECTILE, world, player, x, y, z);
    }

    public BananaBlasterEntity(World world, double x, double y, double z, Vec3d velocity) {
        this(null, world, x, y, z);
        this.accelerationPower = 0.1;
        this.setVelocity(velocity.normalize().multiply(accelerationPower));
        this.velocityDirty = true;
    }

    @Override
    public void tick() {
        super.tick();
        this.setVelocity(this.getVelocity().add(0, -gravity, 0));
        gravity += 0.001f;
        if(this.getWorld().isClient) {
            this.getWorld().addParticle(ParticleTypes.WAX_ON, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    protected boolean canHit(Entity entity) {
        if(entity instanceof AbstractBonziCloneEntity) {
            return true;
        }
        return super.canHit(entity);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity source = this.getOwner();
        LivingEntity attacker = source instanceof LivingEntity livingEntity ? livingEntity : null;
        Entity hitEntity = entityHitResult.getEntity();
        if (attacker != null) {
            attacker.onAttacking(hitEntity);
        }

        float damageAmount = 1.0f;
        if(hitEntity instanceof AbstractBonziCloneEntity) {
            damageAmount = 50f;
        }

        DamageSource damageSource = BonziBuddyEntities.bananaDamageSource(this, attacker);
        if (hitEntity.damage(damageSource, damageAmount) && hitEntity instanceof LivingEntity livingEntity3) {
            EnchantmentHelper.onTargetDamaged((ServerWorld)this.getWorld(), livingEntity3, damageSource);
        }

        this.getWorld().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.ENTITY_ALLAY_DEATH,
                SoundCategory.NEUTRAL,
                0.5f,
                2f
        );
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockPos hitPos = blockHitResult.getBlockPos();
        BlockState blockState = this.getWorld().getBlockState(hitPos);
        this.getWorld().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.ENTITY_IRON_GOLEM_REPAIR,
                SoundCategory.NEUTRAL,
                0.65F,
                2f
        );
        super.onBlockHit(blockHitResult);
    }

    @Override
    protected void createExplosion(Vec3d pos) {

    }
}
