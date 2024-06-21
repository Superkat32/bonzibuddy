package net.superkat.bonzibuddy.entity.bonzi.minigame.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.item.BonziItems;
import software.bernie.geckolib.animatable.GeoAnimatable;

import java.awt.*;
import java.util.Objects;

public class BonziCloneEntity extends AbstractBonziCloneEntity implements GeoAnimatable {
    public float scale;
    public Color color;
    public BonziCloneEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        setScale(MathHelper.clamp(this.getRandom().nextBetween(1, 3) * this.getRandom().nextFloat(), 1, 3));

        this.color = new Color(
                255 - this.getRandom().nextBetween(0, 70),
                255 - this.getRandom().nextBetween(0, 30),
                255 - this.getRandom().nextBetween(0, 70),
                255
        );
    }

    public void setScale(float scale) {
        this.scale = scale;
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_SCALE)).setBaseValue(scale);
        setAttributesBasedOnScale();
    }

    public void setAttributesBasedOnScale() {
        this.scale = (float) this.getAttributeValue(EntityAttributes.GENERIC_SCALE);
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(scale < 2 ? 5 * scale : 10 * scale);
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(3 + scale);
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).setBaseValue(0.35 * scale);
        this.getAttributeInstance(EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO).setBaseValue(1 / scale);
    }

    @Override
    public void tick() {
        this.scale = (float) this.getAttributeValue(EntityAttributes.GENERIC_SCALE);
        if(this.getWorld().isClient() && scale > 2f) {
            if(this.age % 5 == 0) {
                this.getWorld().addParticle(ParticleTypes.TRIAL_SPAWNER_DETECTION,
                        this.getX() + this.random.nextFloat() * this.random.nextBetween(-1, 1),
                        this.getY(),
                        this.getZ() + this.random.nextFloat() * this.random.nextBetween(-1, 1),
                        0, 0.2f, 0);

                this.getWorld().addParticle(ParticleTypes.TOTEM_OF_UNDYING,
                        this.getX() + this.random.nextFloat() * this.random.nextBetween(-1, 1),
                        this.getY() + scale,
                        this.getZ() + this.random.nextFloat() * this.random.nextBetween(-1, 1),
                        0, 0.2f, 0);
            }
        }
        super.tick();
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if(dropBananaBlaster()) {
            this.dropItem(BonziItems.BANANA_BLASTER);
            this.dropItem(BonziItems.BANANA_BLASTER);
            this.dropItem(BonziItems.BANANA_BLASTER);
        }
    }

    public boolean dropBananaBlaster() {
        return this.scale >= 2 && inMinigame();
    }

    @Override
    protected Box getAttackBox() {
        Box box = super.getAttackBox();
        return box.contract(0.75, 0, 0.75);
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16)
                .add(EntityAttributes.GENERIC_SCALE, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1)
                .add(EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO, 0);
    }
}
