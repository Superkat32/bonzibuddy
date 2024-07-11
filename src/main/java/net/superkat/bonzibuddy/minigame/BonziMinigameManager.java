package net.superkat.bonzibuddy.minigame;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.PersistentState;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class to manage all the ongoing minigames, as well as some common methods between all minigames. Should be called by BonziMinigameApi.
 * @see net.superkat.bonzibuddy.minigame.api.BonziMinigameApi
 * @see BonziMinigame
 */
public class BonziMinigameManager extends PersistentState {
    private final Map<Integer, BonziMinigame> minigames = Maps.newHashMap();
    private final Map<Integer, BlockPos> requestedMinigames = Maps.newHashMap();
    private final Map<Integer, List<ServerPlayerEntity>> playersWaiting = Maps.newHashMap();
    private final ServerWorld world;
    private int nextAvailableId;

    public static PersistentState.Type<BonziMinigameManager> getPersistentStateType(ServerWorld world) {
        return new PersistentState.Type<>(() -> new BonziMinigameManager(world), (nbt, registryLookup) -> fromNbt(world, nbt), null);
    }

    public BonziMinigameManager(ServerWorld world) {
        this.world = world;
        this.nextAvailableId = 0;
        this.markDirty(); //save nbt
    }

    /**
     * Create a new Bonzi Minigame.
     *
     * @param minigame The minigame type to be created.
     * @param startingPos The minigame's starting BlockPos.
     * @return The newly created minigame.
     */
    public BonziMinigame createNewBonziMinigame(BonziMinigameType minigame, BlockPos startingPos) {
        BonziBUDDY.LOGGER.info("Creating new Bonzi Minigame...");
        BonziMinigame bonziMinigame = BonziMinigameApi.createMinigameFromType(minigame, nextId(), world, startingPos);
        finalizeNewBonziMinigame(bonziMinigame);
        this.markDirty();
        return bonziMinigame;
    }

    /**
     * Add a minigame to the stored minigame list.
     *
     * @param minigame Minigame to be finalized.
     */
    public void finalizeNewBonziMinigame(BonziMinigame minigame) {
        BonziBUDDY.LOGGER.info("Finalizing new Bonzi Minigame...");
        minigames.put(minigame.getId(), minigame);
    }

    public void tick() {
        Iterator<BonziMinigame> allMinigames = this.minigames.values().iterator();

        while(allMinigames.hasNext()) {
            BonziMinigame minigame = allMinigames.next();
            if(minigame.stopped()) {
                allMinigames.remove();
                this.markDirty();
            } else {
                minigame.tick();
            }
        }

        //the most convoluted way of working around the chunks not being loaded
        //I can't freaking believe this worked xD - haha doesn't work on server ;-;
        Iterator<Map.Entry<Integer, BlockPos>> requestedMinigamesId = this.requestedMinigames.entrySet().iterator();
        Iterator<List<ServerPlayerEntity>> waitingPlayers = this.playersWaiting.values().iterator();
        while (requestedMinigamesId.hasNext() && waitingPlayers.hasNext()) {
            Map.Entry<Integer, BlockPos> entry = requestedMinigamesId.next();
            BlockPos minigameStartPos = entry.getValue();
            int id = entry.getKey();
            List<ServerPlayerEntity> players = waitingPlayers.next();
            if (!minigameStartPos.equals(BlockPos.ORIGIN)) {
                BonziMinigame minigame = BonziMinigameApi.startBonziMinigame(BonziMinigameType.TRIPLE_CHAOS, this.world, minigameStartPos);

                Difficulty worldDifficulty = this.world.getDifficulty();
                int difficulty = worldDifficulty.ordinal() + players.size() / 2;
                if(minigame instanceof TripleChaosMinigame chaosMinigame) {
                    chaosMinigame.setDifficultyLevel(difficulty);
                }
                BonziMinigameApi.teleportPlayersToMinigame(minigame, players);

                this.world.getChunkManager().removeTicket(ChunkTicketType.DRAGON, new ChunkPos(minigameStartPos.getX(), minigameStartPos.getZ()), 4, Unit.INSTANCE);
                requestedMinigamesId.remove();
                waitingPlayers.remove();
            } else { //starting pos not found yet
                ChunkPos location = new ChunkPos(this.minigames.size() == 0 ? 0 : this.minigames.size() * BonziMinigameApi.STRUCTURE_SPACING, 0);
                this.world.getChunkManager().addTicket(ChunkTicketType.DRAGON, location, 4, Unit.INSTANCE);
                boolean locationLoaded = this.world.isChunkLoaded(location.x, location.z);
                if(locationLoaded) {
                    BlockPos spawnPos = BonziMinigameApi.getAvailableMinigameBlockpos(this.world);
                    if(spawnPos != BlockPos.ORIGIN) {
                        this.requestedMinigames.put(id, spawnPos);
                    }
                }
            }
        }
    }

    public void requestMinigame(List<ServerPlayerEntity> players) {
        int id = this.nextAvailableId++;
        this.requestedMinigames.put(id, BlockPos.ORIGIN);
        this.playersWaiting.put(id, players);
    }

    public BonziMinigame getMinigameById(int id) {
        return this.minigames.get(id);
    }

    public List<BonziMinigame> getAllMinigames() {
        return minigames.values().stream().toList();
    }

    private int nextId() {
        return ++nextAvailableId;
    }

    public static BonziMinigameManager fromNbt(ServerWorld world, NbtCompound nbt) {
        BonziMinigameManager minigameManager = new BonziMinigameManager(world);
        minigameManager.nextAvailableId = nbt.getInt("NextAvailableID");

        NbtList nbtList = nbt.getList("Minigames", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            BonziMinigame minigame = BonziMinigameApi.createMinigameFromType(BonziMinigameType.fromName(nbtCompound.getString("Type")), world, nbtCompound);
            minigameManager.minigames.put(minigame.getId(), minigame);
        }

        return minigameManager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("NextAvailableID", this.nextAvailableId);

        NbtList nbtList = new NbtList();

        for(BonziMinigame minigame : this.minigames.values()) {
            NbtCompound nbtCompound = new NbtCompound();
            minigame.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }

        nbt.put("Minigames", nbtList);
        return nbt;
    }
}
