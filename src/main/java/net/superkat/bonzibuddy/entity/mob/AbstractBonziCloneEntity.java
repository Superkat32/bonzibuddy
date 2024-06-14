package net.superkat.bonzibuddy.entity.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.entity.ProtectBonziEntity;

/**
 * Class to contain methods for interacting with the (Catastrophic Clones?) Bonzi minigame
 */
public abstract class AbstractBonziCloneEntity extends HostileEntity {
    public AbstractBonziCloneEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1, false));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8f, 0.5f));
        this.goalSelector.add(3, new LookAtEntityGoal(this, ProtectBonziEntity.class, 16f, 0.5f));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, ProtectBonziEntity.class, false));
    }
}
