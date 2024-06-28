package net.superkat.bonzibuddy.minigame;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.superkat.bonzibuddy.entity.BonziBuddyEntities;
import net.superkat.bonzibuddy.entity.bonzi.minigame.ProtectBonziEntity;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziCloneEntity;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.minigame.MinigameHudUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.WaitingForPlayersS2C;

public class BonziCatastrophicClonesMinigame extends BonziMinigame {
    public int ticksUntilWaveEnd;
    public int secondsUntilWaveEnd;
    public int wave;
    public int maxWaves;
    public int ticksUntilNextWave;
    public ProtectBonziEntity protectBonziEntity = null;
    public int ticksUntilEnemy;
    public int maxTicksUntilEnemy;
    public int minTicksUntilEnemy;
    public BlockPos currentEnemySpawnPos;
    public int ticksUntilNewEnemySpawnPos;

    public BonziCatastrophicClonesMinigame(int id, ServerWorld world, BlockPos startPos) {
        super(id, world, startPos);
        this.maxWaves = 3;
        maxTicksUntilEnemy = 30;
        minTicksUntilEnemy = 10;
    }

    public BonziCatastrophicClonesMinigame(ServerWorld world, NbtCompound nbt) {
        super(world, nbt);
    }

    @Override
    public void tick() {
        super.tick();

        if(gracePeriodSeconds > 13 && wave == 1) {
            if(gracePeriodTicks % 5 == 0) { //clear previous minigame entities in case of error
                BonziMinigameApi.clearAnyEntities(world, startPos, 30);
            }
        }

        if(!isLoaded()) {
            ticksUntilInvalidate--;
            if(ticksUntilInvalidate <= 0) {
                invalidate();
            }
            return;
        }

        if(onGoing()) {
            //check for wave clear sequence
            if(ticksUntilNextWave > 0) {
                ticksUntilNextWave--;
                if(ticksUntilNextWave <= 0) {
                    startNextWave();
                }
            } else {
                ticksUntilWaveEnd--;

                ticksUntilEnemy--;
                if(ticksUntilEnemy <= 0) {
                    spawnClone();
                }

                ticksUntilNewEnemySpawnPos--;
                if(ticksUntilNewEnemySpawnPos <= 0) {
                    newEnemySpawnPos();
                }
            }
        } else if (hasWon() || hasLost()) {
            ticksUntilInvalidate--;
            if(ticksUntilInvalidate <= 0) {
                invalidate();
            }
        }
    }

    @Override
    public void tickSecond() {
        secondsUntilWaveEnd = ticksUntilWaveEnd / 20;
        hudData.setTime(secondsUntilWaveEnd);
        sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.UPDATE_TIME);

        if(onePlayerLeft()) {
            hudData.onePlayerLeft = true;
            sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.UPDATE_ONE_PLAYER_LEFT);
        } else if (hudData.onePlayerLeft) {
            hudData.onePlayerLeft = false;
            sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.UPDATE_ONE_PLAYER_LEFT);
        }
        super.tickSecond();
    }

    @Override
    public void gracePeriodTickSecond() {
        super.gracePeriodTickSecond();
        hudData.gracePeriod = gracePeriodSeconds;
        if(gracePeriodSeconds <= 10) {
            sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.UPDATE_GRACE_PERIOD);
            if(protectBonziEntity == null) {
                spawnProtectableBonziBuddy();
            }
        } else {
            sendPacketToInvolvedPlayers(new WaitingForPlayersS2C());
        }
    }

    @Override
    public boolean checkForGameEnd() {
        boolean playersDefeated;
        if(multiplayer()) {
            playersDefeated = playersAlive() <= 0;
        } else {
            int playerLives = 3;
            playersDefeated = playerLives <= 0;
        }
        return (ticksUntilWaveEnd <= 0 || bonziDefeated() || playersDefeated) && !hasLost() && !hasWon() && !gracePeriod() && ticksUntilNextWave <= 0;
    }

    public boolean bonziDefeated() {
        boolean bonziDefeated = protectBonziEntity == null;
        if(!bonziDefeated) {
            if(protectBonziEntity.getRemovalReason() != Entity.RemovalReason.UNLOADED_TO_CHUNK) {
                bonziDefeated = !protectBonziEntity.isAlive();
            }
        }
        return bonziDefeated;
    }

    @Override
    public void startGracePeriod() {
        gracePeriodSeconds = 10;
        if(this.wave == 1) {
            gracePeriodSeconds += 7; //secret 7 seconds to give everyone time to load in
        }
        hudData.gracePeriod = gracePeriodSeconds;
        super.startGracePeriod();
    }

    @Override
    public void start() {
        startNextWave();
        hudData.setTime(secondsUntilWaveEnd);
        hudData.setWave(this.wave);

        newEnemySpawnPos();
        ticksUntilNewEnemySpawnPos = 100;

        super.start();
    }

    /**
     * Called when the minigame, OR wave, is ready to be won or lost.
     */
    @Override
    public void end() {
        if(bonziDefeated()) {
            lose();
            ticksUntilInvalidate = 140;
        } else {
            if(wave < maxWaves) {
                waveClear();
            } else {
                win();
                ticksUntilInvalidate = 300;
            }
        }
    }

    @Override
    public void lose() {
        super.lose();
        //1 in 100 chance of all of the alive enemies wearing sunglasses upon player defeat :-]
//        int wearSunglasses = this.world.random.nextInt(100);
//        if(wearSunglasses == 0) {
//            this.enemies.forEach(enemy -> {
//                if(enemy instanceof BonziLikeEntity bonziLikeEntity) {
//                    bonziLikeEntity.playAnimation(enemy, BonziLikeEntity.BonziAnimation.VICTORY_GLASSES);
//                }
//            });
//        }
    }

    @Override
    public void invalidate() {
        if(isLoaded()) {
            if(this.protectBonziEntity != null) {
                this.protectBonziEntity.kill();
            }
            this.enemies.forEach(uuid -> {
                Entity enemy = this.getWorld().getEntity(uuid);
                if(enemy != null) {
                    enemy.kill();
                }
            });
        } else {
            //this really doesn't do anything I don't think
            if(this.protectBonziEntity != null) {
                this.protectBonziEntity.remove(Entity.RemovalReason.DISCARDED);
            }
            this.enemies.forEach(uuid -> {
                Entity enemy = this.getWorld().getEntity(uuid);
                if(enemy != null) {
                    enemy.discard();
                }
            });
        }
        super.invalidate();
    }

    public void startNextWave() {
        wave++;
        ticksUntilWaveEnd = 2000;
        secondsUntilWaveEnd = ticksUntilWaveEnd / 20;
        hudData.setTime(secondsUntilWaveEnd);
        hudData.setWave(this.wave);

        ticksUntilEnemy = 20;
        newEnemySpawnPos();
        ticksUntilNewEnemySpawnPos = 100;

        players().forEach(player -> {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 3));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 200, 2));
        });

        sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.UPDATE_WAVE);
        sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.UPDATE_TIME);
        startGracePeriod();
        sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.UPDATE_GRACE_PERIOD);
    }

    public void waveClear() {
        ticksUntilNextWave = 100; //5 seconds before grace period
        sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.WAVE_CLEAR);
    }

    public void spawnClone() {
        if(currentEnemySpawnPos != null && enemies.size() < maxEnemies) {
            BonziCloneEntity clone = BonziBuddyEntities.BONZI_CLONE.create(world);
            clone.setPos(currentEnemySpawnPos.getX(), currentEnemySpawnPos.getY(), currentEnemySpawnPos.getZ());
            clone.initialize(world, world.getLocalDifficulty(currentEnemySpawnPos), SpawnReason.EVENT, null);
            world.spawnEntity(clone);
            addEnemy(clone);
        }

        ticksUntilEnemy = world.random.nextBetween(minTicksUntilEnemy, maxTicksUntilEnemy);
    }

    public void addEnemy(MobEntity enemy) {
        super.addEnemy(enemy);
        enemy.setTarget(protectBonziEntity);
    }

    public void newEnemySpawnPos() {
        BlockPos newPos = BonziMinigameApi.getEnemySpawnPos(world, startPos, 1, 20);
        if(newPos != null) {
            this.currentEnemySpawnPos = newPos;
        }
        ticksUntilNewEnemySpawnPos = world.random.nextBetween(140, 300);
    }

    private void spawnProtectableBonziBuddy() {
        protectBonziEntity = BonziBuddyEntities.PROTECTABLE_BONZI_BUDDY.create(world);
        protectBonziEntity.setPos(this.startPos.getX(), this.startPos.getY() + 3, this.startPos.getZ());
        protectBonziEntity.initialize(world, world.getLocalDifficulty(this.startPos), SpawnReason.EVENT, null);
        world.spawnEntity(protectBonziEntity);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.ticksUntilWaveEnd = nbt.getInt("TicksUntilWaveEnd");
        this.secondsUntilWaveEnd = ticksUntilWaveEnd / 20;
        super.readNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("TicksUntilWaveEnd", ticksUntilWaveEnd);
        return super.writeNbt(nbt);
    }

    @Override
    public MinigameHudData createHudData() {
        return new MinigameHudData(this.getMinigameType(), "Catastrophic Clones");
    }

    @Override
    public BonziMinigameType getMinigameType() {
        return BonziMinigameType.CATASTROPHIC_CLONES;
    }
}
