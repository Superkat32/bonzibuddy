package net.superkat.bonzibuddy.entity.bonzi.minigame.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class BonziBossEntity extends AbstractBonziCloneEntity {

    private static final TrackedData<Boolean> IS_RED = DataTracker.registerData(BonziBossEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_GREEN = DataTracker.registerData(BonziBossEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_BLUE = DataTracker.registerData(BonziBossEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

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
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_RED, false);
        builder.add(IS_GREEN, false);
        builder.add(IS_BLUE, false);
    }

    public boolean isRed() {
        return this.getDataTracker().get(IS_RED);
    }

    public void setIsRed(boolean isRed) {
        this.getDataTracker().set(IS_RED, isRed);
    }

    public boolean isGreen() {
        return this.getDataTracker().get(IS_GREEN);
    }

    public void setIsGreen(boolean isGreen) {
        this.getDataTracker().set(IS_GREEN, isGreen);
    }

    public boolean isBlue() {
        return this.getDataTracker().get(IS_BLUE);
    }

    public void setIsBlue(boolean isBlue) {
        this.getDataTracker().set(IS_BLUE, isBlue);
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
