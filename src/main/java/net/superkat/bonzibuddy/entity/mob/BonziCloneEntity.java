package net.superkat.bonzibuddy.entity.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Objects;

public class BonziCloneEntity extends AbstractBonziCloneEntity {
    public double scale;
    public Color color;
    public BonziCloneEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.scale = MathHelper.clamp(this.getRandom().nextBetween(1, 3) * this.getRandom().nextFloat(), 1, 3);
        this.color = new Color(
                this.getRandom().nextBetween(0, 100),
                this.getRandom().nextBetween(0, 70),
                this.getRandom().nextBetween(0, 100),
                255
        );
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_SCALE)).setBaseValue(scale);
        setAttributesBasedOnScale();
    }

    public void setAttributesBasedOnScale() {
        
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16)
                .add(EntityAttributes.GENERIC_SCALE, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1);
    }
}
