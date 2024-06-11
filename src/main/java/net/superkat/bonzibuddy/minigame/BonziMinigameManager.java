package net.superkat.bonzibuddy.minigame;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;

import java.util.Iterator;
import java.util.Map;

/**
 * Class to manage all the ongoing minigames, as well as some common methods between all minigames. Should be called by BonziMinigameApi.
 * @see net.superkat.bonzibuddy.minigame.api.BonziMinigameApi
 * @see BonziMinigame
 */
public class BonziMinigameManager extends PersistentState {
    private final Map<Integer, BonziMinigame> minigames = Maps.newHashMap();
    private final ServerWorld world;
    private int nextAvailableId;

    public static PersistentState.Type<BonziMinigameManager> getPersistentStateType(ServerWorld world) {
        return new PersistentState.Type<>(() -> new BonziMinigameManager(world), (nbt, registryLookup) -> fromNbt(world, nbt), null);
    }

    public BonziMinigameManager(ServerWorld world) {
        this.world = world;
        this.nextAvailableId = 0;
        this.markDirty();
    }

    public BonziMinigame createNewBonziMinigame(BonziMinigameType minigame, BlockPos startingPos) {
        BonziBUDDY.LOGGER.info("Creating new Bonzi Minigame...");
        BonziMinigame bonziMinigame = BonziMinigameApi.createMinigameFromType(minigame, nextId(), world, startingPos);
        finalizeNewBonziMinigame(bonziMinigame);
        this.markDirty();
        return bonziMinigame;
    }

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
//            BonziMinigame minigame = new BonziMinigame(world, nbtCompound);
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
