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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;

public class BonziBossEntity extends AbstractBonziCloneEntity {

    private static final TrackedData<BlockPos> LEAP_POS = DataTracker.registerData(BonziBossEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Boolean> LEAPING = DataTracker.registerData(BonziBossEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> TICKS_SINCE_LEAP = DataTracker.registerData(BonziBossEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public int secondsBetweenLeaps = 12;
    public int ticksUntilLeap = 100;

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
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 16)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.25)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0)
                //if you accidentally spawn this in... oh well
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1024);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.setIsRed(nbt.getBoolean("red"));
        this.setIsGreen(nbt.getBoolean("green"));
        this.setIsBlue(nbt.getBoolean("blue"));
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("red", this.isRed());
        nbt.putBoolean("green", this.isGreen());
        nbt.putBoolean("blue", this.isBlue());
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_RED, false);
        builder.add(IS_GREEN, false);
        builder.add(IS_BLUE, false);
        builder.add(LEAP_POS, BlockPos.ORIGIN);
        builder.add(LEAPING, false);
        builder.add(TICKS_SINCE_LEAP, 0);
    }

    public boolean isRed() {
        return this.getDataTracker().get(IS_RED);
    }

    public void setIsRed(boolean isRed) {
        this.getDataTracker().set(IS_RED, isRed);
        if(isRed) {
            this.secondsBetweenLeaps = 11;
            this.ticksUntilLeap = secondsBetweenLeaps * 20;
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).setBaseValue(2);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(20);
        }
    }

    public boolean isGreen() {
        return this.getDataTracker().get(IS_GREEN);
    }

    public void setIsGreen(boolean isGreen) {
        this.getDataTracker().set(IS_GREEN, isGreen);
        if(isGreen) {
            this.secondsBetweenLeaps = 15;
            this.ticksUntilLeap = secondsBetweenLeaps * 20;
            this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.5);
        }
    }

    public boolean isBlue() {
        return this.getDataTracker().get(IS_BLUE);
    }

    public void setIsBlue(boolean isBlue) {
        this.getDataTracker().set(IS_BLUE, isBlue);
        if(isBlue) {
            this.secondsBetweenLeaps = 13;
            this.ticksUntilLeap = secondsBetweenLeaps * 20;
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
        }
    }

    public boolean isLeaping() {
        return this.getDataTracker().get(LEAPING);
    }

    public void setIsLeaping(boolean isLeaping) {
        this.getDataTracker().set(LEAPING, isLeaping);
    }

    public BlockPos leapPos() {
        return this.getDataTracker().get(LEAP_POS);
    }

    public void setLeapPos(BlockPos leapPos) {
        this.getDataTracker().set(LEAP_POS, leapPos);
    }

    public int getTicksSinceLeap() {
        return this.getDataTracker().get(TICKS_SINCE_LEAP);
    }

    public void setTicksSinceLeap(int ticks) {
        this.getDataTracker().set(TICKS_SINCE_LEAP, ticks);
    }

    @Override
    public void tick() {
        super.tick();

        if(isLeaping()) {
            if(this.getWorld().isClient) {
                BlockPos leapPos = leapPos();
                this.getWorld().addParticle(ParticleTypes.FALLING_LAVA, leapPos.getX(), leapPos.getY() + 3, leapPos.getZ(), 0, -1, 0);

                if(this.age % 20 == 0) {
                    this.getWorld().playSound(leapPos.getX(), leapPos.getY(), leapPos.getZ(), SoundEvents.BLOCK_BELL_USE, SoundCategory.HOSTILE, 0.8f, 1f, true);
                } else if (this.age % 2 == 0) {
                    this.getWorld().playSoundFromEntity(this, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.HOSTILE, 0.5f, 1.3f);
                }
                this.getWorld().addParticle(ParticleTypes.DRIPPING_LAVA, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                this.getWorld().addImportantParticle(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

                float radius = 4.5f;
                //divided by 50 to reduce particle count from roughly 5k to 150 particles
                float rangeTimesALotOfDigits = MathHelper.ceil((float) Math.PI * radius * radius) / 50f;
                for (int i = 0; i < rangeTimesALotOfDigits; i++) {
                    float h = this.random.nextFloat() * (float) (Math.PI * 2);
//                    float k = MathHelper.sqrt(this.random.nextFloat()) * range;
                    double x = leapPos.getX() + (double)(MathHelper.cos(h) * radius);
                    double y = leapPos.getY() + 0.5;
                    double z = leapPos.getZ() + (double)(MathHelper.sin(h) * radius);
                    this.getWorld().addImportantParticle(ParticleTypes.DRIPPING_LAVA, x, y, z, 0.001, 0.001, 0.001);
                }
            } else if(getTicksSinceLeap() > 20 && isOnGround()) {
                setIsLeaping(false);
                ServerWorld serverWorld = (ServerWorld) this.getWorld();
                float velX = (float) (this.random.nextFloat() * 2.0 - 1.0);
                float velY = (float) ((this.random.nextFloat() * 2.0 - 1.0) + 0.2);
                float velZ = (float) (this.random.nextFloat() * 2.0 - 1.0);
                serverWorld.spawnParticles(ParticleTypes.DRAGON_BREATH, this.getX(), this.getY(), this.getZ(), 20, velX, velY, velZ, 1);
                serverWorld.spawnParticles(ParticleTypes.DUST_PLUME, this.getX(), this.getY(), this.getZ(), 20, velX, velY, velZ, 1);
                this.getWorld().playSoundFromEntity(this, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.HOSTILE, 1f, 0.7f);

                for(AbstractBonziCloneEntity bonzi : this.getWorld().getNonSpectatingEntities(AbstractBonziCloneEntity.class, this.getBoundingBox().expand(3))) {
                    if(bonzi != this) {
                        //friendly fire :)
                        bonzi.damage(this.getDamageSources().mobAttack(this), 115);
                    }
                }
            }

        }

        if(this.age % 10 == 0) {
            setTicksSinceLeap(getTicksSinceLeap() + 10);
        }
        ticksUntilLeap--;

        if(ticksUntilLeap <= 0) {
            if(this.getTarget() != null) {
                leapTowardsPos(this.getTarget().getBlockPos());
            }
        }

    }

    public void leapTowardsPos(BlockPos pos) {
        ticksUntilLeap = secondsBetweenLeaps * 20;
        setTicksSinceLeap(0);
        setLeapPos(pos);
        setIsLeaping(true);

        double dx = pos.getX() - this.getX();
        double dy = 2;
        double dz = pos.getZ() - this.getZ();

        double distance = Math.sqrt(dx * dx + dz * dz);
        this.setVelocity(dx / distance * 2, dy, dz / distance * 2);
    }


    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if(inMinigame()) {
            tripleChaosMinigame.updateBossHealth(this);
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if(inMinigame()) {
            tripleChaosMinigame.bossDefeated(this);
        }
    }

    @Override
    public void attemptTickInVoid() {
        if(!this.getWorld().isClient) {
            if(inMinigame() && BonziMinigameApi.isBonziBuddyWorld((ServerWorld) this.getWorld())) {
                if (this.getY() < (double)(this.getWorld().getBottomY() - 64)) {
                    this.tickInVoid();
                }
            }
        } else {
            super.attemptTickInVoid();
        }
    }

    @Override
    protected void tickInVoid() {
        if(inMinigame()) {
            BlockPos startPos = tripleChaosMinigame.getStartPos();
            this.teleport(startPos.getX(), startPos.getY() + 10, startPos.getZ(), true);
            leapTowardsPos(startPos.add(0, 1, 0));
            this.setVelocity(this.getVelocity().getX(), 2, this.getVelocity().getZ());
        } else {
            this.kill();
        }
    }
}
