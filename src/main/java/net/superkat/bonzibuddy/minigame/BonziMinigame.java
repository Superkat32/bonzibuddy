package net.superkat.bonzibuddy.minigame;

import com.google.common.collect.Sets;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Class to be extended by all specific minigames. Should also include common minigame methods.
 *
 * @see net.superkat.bonzibuddy.minigame.api.BonziMinigameApi
 * @see BonziMinigameManager
 */
public class BonziMinigame {
    private final Set<UUID> players = Sets.newHashSet();
    private final int id;
    private final ServerWorld world;
    private final BlockPos startPos;

    private BonziMinigame.Status status;
    public int ticksSinceStart;

    public BonziMinigame(int id, ServerWorld world, BlockPos startPos) {
        this.id = id;
        this.world = world;
        this.startPos = startPos;

        this.status = Status.STARTING;
    }

    //Minigame from NBT
    public BonziMinigame(ServerWorld world, NbtCompound nbt) {
        this.world = world;
        this.id = nbt.getInt("Id");
        int startX = nbt.getInt("StartX");
        int startY = nbt.getInt("StartY");
        int startZ = nbt.getInt("StartZ");
        this.startPos = new BlockPos(startX, startY, startZ);
        readNbt(nbt);
    }

    public void updateInvolvedPlayers() {

    }

    public boolean objectiveComplete() {
        return ticksSinceStart >= 100;
    }

    public void tick() {
        if(checkForGameEnd()) {
            this.end();
            return;
        }

        ticksSinceStart++;
    }

    public boolean checkForGameEnd() {
        return getNearbyPlayers().isEmpty();
    }

    public void start() {
        ticksSinceStart = 0;
    }

    public List<ServerPlayerEntity> getNearbyPlayers() {
        return this.world.getPlayers(player -> {
            return player.squaredDistanceTo(this.startPos.getX(), this.startPos.getY(), this.startPos.getZ()) <= minigameRange();
        });
    }

    public double minigameRange() {
        return 48;
    }

    public void end() {
        this.status = Status.STOPPED;
    }

    public boolean stopped() {
        return this.status == Status.STOPPED;
    }

    public int getId() {
        return this.id;
    }

    public void readNbt(NbtCompound nbt) {
        this.status = Status.fromName(nbt.getString("Status"));
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("Id", this.id);
        nbt.putString("Type", this.getMinigameType().getName());
        nbt.putInt("StartX", this.startPos.getX());
        nbt.putInt("StartY", this.startPos.getY());
        nbt.putInt("StartZ", this.startPos.getZ());
        nbt.putString("Status", this.status.getName());

        return nbt;
    }

    public BonziMinigameType getMinigameType() {
        return BonziMinigameType.ABSTRACT;
    }

    static enum Status {
        STARTING,
        ONGOING,
        WAVE_CLEAR,
        VICTORY,
        LOSS,
        STOPPED;
        private static final BonziMinigame.Status[] VALUES = values();

        static BonziMinigame.Status fromName(String name) {
            for (BonziMinigame.Status status : VALUES) {
                if(name.equalsIgnoreCase(status.name())) {
                    return status;
                }
            }

            return ONGOING;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

}
