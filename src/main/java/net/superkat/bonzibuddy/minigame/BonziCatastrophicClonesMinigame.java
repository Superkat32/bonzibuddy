package net.superkat.bonzibuddy.minigame;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.minigame.MinigameHudUpdateS2C;

public class BonziCatastrophicClonesMinigame extends BonziMinigame {
    public int ticksUntilWaveEnd;
    public int secondsUntilWaveEnd;
    public int wave;
    public BonziCatastrophicClonesMinigame(int id, ServerWorld world, BlockPos startPos) {
        super(id, world, startPos);
    }

    public BonziCatastrophicClonesMinigame(ServerWorld world, NbtCompound nbt) {
        super(world, nbt);
    }

    @Override
    public void tick() {
        super.tick();
        ticksUntilWaveEnd--;
    }

    @Override
    public void tickSecond() {
        secondsUntilWaveEnd = ticksUntilWaveEnd / 20;
        this.hudData.setTime(secondsUntilWaveEnd);
        sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.UPDATE_TIME);
        super.tickSecond();
    }

    @Override
    public boolean checkForGameEnd() {
        return ticksUntilWaveEnd <= -19; //-19 to allow the seconds to reach 0 for about a second
    }

    @Override
    public void start() {
        ticksUntilWaveEnd = 2000;
        secondsUntilWaveEnd = ticksUntilWaveEnd / 20;
        wave = 1;
        this.hudData.setTime(secondsUntilWaveEnd);
        this.hudData.setWave(this.wave);
        super.start();
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
