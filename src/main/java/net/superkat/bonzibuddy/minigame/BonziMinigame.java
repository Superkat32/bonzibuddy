package net.superkat.bonzibuddy.minigame;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.minigame.MinigameHudUpdateS2C;

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
    private final Set<ServerPlayerEntity> players = Sets.newHashSet();
    private final Set<UUID> playersUuid = Sets.newHashSet();
    private final int id;
    private final ServerWorld world;
    private final BlockPos startPos;

    public MinigameHudData hudData = createHudData();
    private BonziMinigame.Status status;
    public boolean loaded;
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

    public void sendCreateMinigameHudPacket() {
        sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.ADD);
    }

    public void sendRemoveMinigameHudPacket() {
        sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.REMOVE);
    }

    public void sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action updateAction) {
        MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, updateAction);
        sendPacketToInvolvedPlayers(packet);
    }

    public void sendPacketToInvolvedPlayers(CustomPayload payload) {
        for (ServerPlayerEntity player : players) {
            if(player != null) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }

    /**
     * Send updates, such as packets, to all involved players.
     */
    public void updateInvolvedPlayers() {
        List<ServerPlayerEntity> allPlayers = world.getPlayers();
        List<ServerPlayerEntity> inRangePlayers = getNearbyPlayers();

        for (ServerPlayerEntity player : allPlayers) {
            if(!inRangePlayers.contains(player) && players.contains(player.getUuid())) {
                removePlayer(player);
            }
        }

        for (ServerPlayerEntity player : inRangePlayers) {
            if(!players.contains(player.getUuid())) {
                addPlayer(player);
            }
        }
    }

    /**
     * Called every Minecraft tick. Should be the main method for handling everything.
     */
    public void tick() {
        if(checkForGameEnd()) {
            this.end();
            return;
        }

        if(!isLoaded()) {
            return;
        }

        ticksSinceStart++;

        if(ticksSinceStart % 20 == 0) {
            tickSecond();
        }

    }

    public boolean isLoaded() {
        boolean wasLoaded = loaded;
        this.loaded = world.isChunkLoaded(startPos);
        if(!loaded) {
            if(wasLoaded) {
                sendRemoveMinigameHudPacket();
            }
            return false;
        }
        return true;
    }

    /**
     * Called every second(20 Minecraft ticks). Notably used for updating involved players, as it is a bit more of an expensive method.
     */
    public void tickSecond() {
        updateInvolvedPlayers();
    }

    /**
     * @return If the minigame should be ready to end. May not end the game right away, as a winning or losing event may be played first.
     */
    public boolean checkForGameEnd() {
        return getNearbyPlayers().isEmpty();
    }

    /**
     * Called when the minigame has been created, used to set fields to their starting value.
     */
    public void start() {
        ticksSinceStart = 0;
        sendCreateMinigameHudPacket();
    }

    public void addPlayer(ServerPlayerEntity player) {
        players.add(player);
        playersUuid.add(player.getUuid());
        MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.ADD);
        ServerPlayNetworking.send(player, packet);
    }

    public void removePlayer(ServerPlayerEntity player) {
        players.remove(player);
        playersUuid.remove(player.getUuid());
        MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.REMOVE);
        ServerPlayNetworking.send(player, packet);
    }

    /**
     * @return All players within the minigame's range.
     */
    public List<ServerPlayerEntity> getNearbyPlayers() {
        return this.world.getPlayers(player -> {
            return player.squaredDistanceTo(this.startPos.getX(), this.startPos.getY(), this.startPos.getZ()) <= minigameRange() * minigameRange();
        });
    }

    public int playersAlive() {
        return (int) getNearbyPlayers().stream().filter(LivingEntity::isAlive).count();
    }

    /**
     * @return If the minigame is multiplayer, NOT the world/server!!!
     */
    public boolean multiplayer() {
        return this.players.size() >= 2;
    }

    /**
     * @return If there is one player remaining in a multiplayer minigame.
     */
    public boolean onePlayerLeft() {
        return multiplayer() && playersAlive() == 1;
    }

    public double minigameRange() {
        return 48;
    }

    public void end() {
        invalidate();
    }

    /**
     * Called when the minigame is ready to be removed.
     */
    public void invalidate() {
        this.status = Status.STOPPED;
        sendRemoveMinigameHudPacket();
    }

    /**
     * @return If the Minigame has stopped and is ready to be removed.
     */
    public boolean stopped() {
        return this.status == Status.STOPPED;
    }

    public BlockPos getStartPos() {
        return startPos;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * @return The minigame's unique ID, which may or may not never get used honestly.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Read special NBT specific to the minigame
     *
     * @param nbt - The nbt to read from.
     */
    public void readNbt(NbtCompound nbt) {
        this.status = Status.fromName(nbt.getString("Status"));
    }

    /**
     * Write special NBT specific to the minigame.
     *
     * @param nbt The NBT to write to.
     * @return The NBT param but with the newly written NBT data.
     */
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("Id", this.id);
        nbt.putString("Type", this.getMinigameType().getName());
        nbt.putInt("StartX", this.startPos.getX());
        nbt.putInt("StartY", this.startPos.getY());
        nbt.putInt("StartZ", this.startPos.getZ());
        nbt.putString("Status", this.status.getName());

        return nbt;
    }

    public MinigameHudData createHudData() {
        //FIXME - getName() Text method with translatable string here
        return new MinigameHudData(getMinigameType(), "Abstract Bonzi Minigame");
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
