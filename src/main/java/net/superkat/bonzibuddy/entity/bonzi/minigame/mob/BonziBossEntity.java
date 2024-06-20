package net.superkat.bonzibuddy.entity.bonzi.minigame.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class BonziBossEntity extends AbstractBonziCloneEntity {
    public BonziBossEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48)
                .add(EntityAttributes.GENERIC_SCALE, 5.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 16)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.25)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1024);
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if(inMinigame()) {
            tripleChaosMinigame.updateBossHealth(this);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if(inMinigame()) {
            tripleChaosMinigame.bossDefeated(this);
        }
    }

    @Override
    protected void tickInVoid() {
        this.kill();
    }
}
