package net.superkat.bonzibuddy.minigame;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.minigame.MinigameHudUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.PlayerInMinigameUpdateS2C;
import org.jetbrains.annotations.Nullable;

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
    protected final Set<UUID> playersUuid = Sets.newHashSet();
    protected final int id;
    protected final ServerWorld world;
    protected final BlockPos startPos;
    //the ticks since the world/nbt has been reloaded
    protected int ticksSinceReload = 0;
    protected boolean loadedFromNbt = false;

    public MinigameHudData hudData = createHudData();
    protected BonziMinigame.Status status;
    public boolean loaded;
    public int ticksSinceStart;
    public int gracePeriodTicks;
    public int gracePeriodSeconds;
    public Set<UUID> enemies = Sets.newHashSet();
    public int maxEnemies;
    public int ticksUntilInvalidate;
    public BonziMinigame(int id, ServerWorld world, BlockPos startPos) {
        this.id = id;
        this.world = world;
        this.startPos = startPos;

        this.status = Status.STARTING;
        this.maxEnemies = 48;
        this.ticksUntilInvalidate = world.getGameRules().getInt(BonziBUDDY.UNLOADED_INVALIDATION_SECONDS) * 20;
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
        for (ServerPlayerEntity player : players()) {
            if(player != null) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }

    /**
     * Send updates, such as packets, to all involved players.
     */
    public void updateInvolvedPlayers() {
        List<ServerPlayerEntity> allPlayers = this.world.getPlayers();
        List<ServerPlayerEntity> inRangePlayers = getNearbyPlayers();

        if(allPlayers.isEmpty() && !playersUuid.isEmpty()) {
            for (UUID uuid : playersUuid) {
                ServerPlayerEntity player = this.world.getServer().getPlayerManager().getPlayer(uuid);
                if(player != null) {
                    //i don't understand why this is here but it causes a server crash
//                    removePlayer(player);
                }
            }
        }

        for (ServerPlayerEntity player : allPlayers) {
            if(!inRangePlayers.contains(player) && playersUuid.contains(player.getUuid())) {
                removePlayer(player);
            }
        }

        for (ServerPlayerEntity player : inRangePlayers) {
            if(!playersUuid.contains(player.getUuid())) {
                addPlayer(player);
            }
        }
    }

    public List<ServerPlayerEntity> players() {
        return playersUuid.stream().map(uuid -> (ServerPlayerEntity) this.world.getPlayerByUuid(uuid)).toList();
    }

    @Nullable
    public ServerPlayerEntity randomPlayer() {
        int players = players().size();
        if(players > 0) { //haha learned my lesson last modfest
            int player = this.world.random.nextInt(players);
            return players().get(player);
        } else {
            return null;
        }
    }

    /**
     * Called every Minecraft tick. Should be the main method for handling everything.
     */
    public void tick() {
        ticksSinceReload++;
        if(checkForGameEnd() && ticksSinceReload >= 100) {
            if(this.onGoing()) {
                this.end();
                if(fullLogs()) BonziBUDDY.LOGGER.info("Minigame {} invalidated because of game end", this.getId());
            } else if (hasWon() || hasLost()) {
                ticksUntilInvalidate--;
                if (ticksUntilInvalidate <= 0) {
                    if(fullLogs()) BonziBUDDY.LOGGER.info("Minigame {} invalidated because of game end (won or lost)", this.getId());
                    invalidate();
                }
            }
            return;
        }

        if(!isLoaded()) {
            return;
        }

        if(gracePeriod()) {
            if(gracePeriodTicks <= 0) {
                this.status = Status.ONGOING;
                return;
            }

            gracePeriodTicks--;
            if(gracePeriodTicks % 20 == 0) {
                gracePeriodSeconds--;
                gracePeriodTickSecond();
            }

        } else if(onGoing()) {
            ticksSinceStart++;

            if(ticksSinceStart % 20 == 0) {
                tickSecond();
            }
        }


    }

    public boolean isLoaded() {
        boolean wasLoaded = loaded;
        this.loaded = world.isChunkLoaded(startPos);
        if(!loaded) {
            if(wasLoaded) {
                sendRemoveMinigameHudPacket();
                ticksUntilInvalidate = this.world.getGameRules().getInt(BonziBUDDY.UNLOADED_INVALIDATION_SECONDS) * 20;
                if(extraLogs()) BonziBUDDY.LOGGER.info("Minigame {} unloaded!", this.getId());
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
        if(fullLogs()) BonziBUDDY.LOGGER.info("tickSecond");
    }

    public void gracePeriodTickSecond() {
        updateInvolvedPlayers();
        if(fullLogs()) BonziBUDDY.LOGGER.info("tickGraceSecond");
    }

    public void startGracePeriod() {
        gracePeriodTicks = gracePeriodSeconds * 20;
        this.status = Status.GRACE_PERIOD;
        updateInvolvedPlayers();
        if(fullLogs()) BonziBUDDY.LOGGER.info("Grace period started for minigame {}", this.getId());
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
        startGracePeriod();
        if(this.getMinigameType() == BonziMinigameType.ABSTRACT) {
            sendCreateMinigameHudPacket();
        }
    }

    public void addEnemy(MobEntity enemy) {
        enemies.add(enemy.getUuid());
    }

    public void addPlayer(ServerPlayerEntity player) {
//        players.add(player);
        playersUuid.add(player.getUuid());
        MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.ADD);
        ServerPlayNetworking.send(player, packet);

        PlayerInMinigameUpdateS2C minigameUpdate = new PlayerInMinigameUpdateS2C(true);
        ServerPlayNetworking.send(player, minigameUpdate);

        sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.UPDATE_ONE_PLAYER_LEFT);

        if(hasLost()) {
            ServerPlayNetworking.send(player, new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.DEFEAT));
        } else if(hasWon()) {
            ServerPlayNetworking.send(player, new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.VICTORY));
        }

        if(extraLogs()) {
            BonziBUDDY.LOGGER.info("Added player {} to minigame!", player.getName());
        }
    }

    public void removePlayer(ServerPlayerEntity player) {
//        players.remove(player);
        playersUuid.remove(player.getUuid());
        MinigameHudUpdateS2C packet = new MinigameHudUpdateS2C(hudData, MinigameHudUpdateS2C.Action.REMOVE);
        ServerPlayNetworking.send(player, packet);

        PlayerInMinigameUpdateS2C minigameUpdate = new PlayerInMinigameUpdateS2C(false);
        ServerPlayNetworking.send(player, minigameUpdate);

        if(extraLogs()) {
            BonziBUDDY.LOGGER.info("Removed player {} from minigame!", player.getName());
        }
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
        return this.players().size() >= 2;
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

    public void win() {
        this.status = Status.VICTORY;
        sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.VICTORY);
        if(extraLogs()) BonziBUDDY.LOGGER.info("Won minigame {}", this.getId());
    }

    public void lose() {
        this.status = Status.LOSS;
        sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.DEFEAT);
        if(extraLogs()) BonziBUDDY.LOGGER.info("Lost minigame {}", this.getId());
    }

    /**
     * Called when the minigame is ready to be won or lost.
     */
    public void end() {
        invalidate();
        if(extraLogs()) BonziBUDDY.LOGGER.info("Ended minigame {}", this.getId());
    }

    /**
     * Called when the minigame is ready to be removed.
     */
    public void invalidate() {
        this.status = Status.STOPPED;
        sendRemoveMinigameHudPacket();
        if(BonziMinigameApi.isBonziBuddyWorld(this.world)) {
            BonziMinigameApi.teleportPlayersToRespawn(this.players());
        }
        BonziBUDDY.LOGGER.info("Bonzi Minigame " + getId() + " (" + getMinigameType().getName() + ") has finished!");
    }

    public boolean gracePeriod() {
        return this.status == Status.GRACE_PERIOD;
    }

    public boolean onGoing() {
        return this.status == Status.ONGOING;
    }
    /**
     * @return If the Minigame has stopped and is ready to be removed.
     */
    public boolean stopped() {
        return this.status == Status.STOPPED;
    }

    public boolean hasWon() {
        return this.status == Status.VICTORY;
    }

    public boolean hasLost() {
        return this.status == Status.LOSS;
    }

    public ServerWorld getWorld() {
        return world;
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

        this.ticksSinceStart = nbt.getInt("ticksSinceStart");
        this.gracePeriodTicks = nbt.getInt("gracePeriodTicks");
        this.gracePeriodSeconds = nbt.getInt("gracePeriodSeconds");

        if(nbt.contains("Enemies", NbtElement.LIST_TYPE)) {
            for (NbtElement nbtElement : nbt.getList("Enemies", NbtElement.INT_ARRAY_TYPE)) {
                UUID entityUuid = NbtHelper.toUuid(nbtElement);
                MobEntity entity = (MobEntity) this.getWorld().getEntity(entityUuid);
                if(entity != null) {
                    this.enemies.add(entity.getUuid());
                }
            }
        }

        this.maxEnemies = nbt.getInt("maxEnemies");
        this.ticksUntilInvalidate = nbt.getInt("ticksUntilInvalidate");


        if(nbt.contains("Enemies", NbtElement.LIST_TYPE)) {
            for (NbtElement nbtElement : nbt.getList("Enemies", NbtElement.INT_ARRAY_TYPE)) {
                this.enemies.add(NbtHelper.toUuid(nbtElement));
            }
        }

        this.ticksSinceReload = 0;
        this.loadedFromNbt = true;
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

        nbt.putInt("ticksSinceStart", this.ticksSinceStart);
        nbt.putInt("gracePeriodTicks", this.gracePeriodTicks);
        nbt.putInt("gracePeriodSeconds", this.gracePeriodSeconds);

        NbtList enemiesNbtList = new NbtList();
        for (UUID enemy : this.enemies) {
            enemiesNbtList.add(NbtHelper.fromUuid(enemy));
        }
        nbt.put("Enemies", enemiesNbtList);
        nbt.putInt("maxEnemies", this.maxEnemies);
        nbt.putInt("ticksUntilInvalidate", this.ticksUntilInvalidate);

        return nbt;
    }

    /**
     * Used for some extra logs that would be helpful in knowing what important code is being activated.
     */
    protected boolean extraLogs() {
        return DisasterLoggerLevel.extras(this.world.getGameRules().get(BonziBUDDY.MINIGAME_DISASTER_LOGGER_LEVEL).get());
    }

    /**
     * Used to log literally as many things as possible.
     */
    protected boolean fullLogs() {
        return DisasterLoggerLevel.full(this.world.getGameRules().get(BonziBUDDY.MINIGAME_DISASTER_LOGGER_LEVEL).get());
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
        GRACE_PERIOD,
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
