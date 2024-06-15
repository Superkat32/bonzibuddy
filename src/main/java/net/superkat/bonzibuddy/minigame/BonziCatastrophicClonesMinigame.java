package net.superkat.bonzibuddy.minigame;

import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.superkat.bonzibuddy.entity.BonziBuddyEntities;
import net.superkat.bonzibuddy.entity.minigame.ProtectBonziEntity;
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
    public int ticksUntilInvalidate;
    public BonziCatastrophicClonesMinigame(int id, ServerWorld world, BlockPos startPos) {
        super(id, world, startPos);
        this.maxWaves = 3;
    }

    public BonziCatastrophicClonesMinigame(ServerWorld world, NbtCompound nbt) {
        super(world, nbt);
    }

    @Override
    public void tick() {
        super.tick();

        if(!isLoaded()) {
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
        return (ticksUntilWaveEnd <= 0 || bonziDefeated()) && !hasLost() && !hasWon() && !gracePeriod() && ticksUntilNextWave <= 0;
    }

    public boolean bonziDefeated() {
        boolean bonziDefeated = protectBonziEntity == null;
        if(!bonziDefeated) {
            bonziDefeated = !protectBonziEntity.isAlive();
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
    public void invalidate() {
        if(this.protectBonziEntity != null) {
            this.protectBonziEntity.kill();
        }
        super.invalidate();
    }

    public void startNextWave() {
        wave++;
        ticksUntilWaveEnd = 2000;
        secondsUntilWaveEnd = ticksUntilWaveEnd / 20;
        hudData.setTime(secondsUntilWaveEnd);
        hudData.setWave(this.wave);

        players.forEach(player -> {
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

    private void spawnProtectableBonziBuddy() {
        protectBonziEntity = BonziBuddyEntities.PROTECTABLE_BONZI_BUDDY.create(world);
        protectBonziEntity.setPos(this.startPos.getX(), this.startPos.getY() + 7, this.startPos.getZ());
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
