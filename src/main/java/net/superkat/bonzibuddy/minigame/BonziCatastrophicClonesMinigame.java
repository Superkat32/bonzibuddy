package net.superkat.bonzibuddy.minigame;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;

public class BonziCatastrophicClonesMinigame extends BonziMinigame {
    public int ticksUntilWaveEnd;
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
        if(ticksSinceStart % 20 == 0) {
            BonziBUDDY.LOGGER.info("yay");
        }
    }

    @Override
    public boolean checkForGameEnd() {
        return ticksUntilWaveEnd <= 0;
    }

    @Override
    public void start() {
        super.start();
        ticksUntilWaveEnd = 2000;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.ticksUntilWaveEnd = nbt.getInt("TicksUntilWaveEnd");
        super.readNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("TicksUntilWaveEnd", ticksUntilWaveEnd);
        return super.writeNbt(nbt);
    }

    @Override
    public BonziMinigameType getMinigameType() {
        return BonziMinigameType.CATASTROPHIC_CLONES;
    }
}
