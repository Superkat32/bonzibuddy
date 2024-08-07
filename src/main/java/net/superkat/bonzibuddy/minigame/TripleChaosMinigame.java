package net.superkat.bonzibuddy.minigame;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.BonziBuddyEntities;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.AbstractBonziCloneEntity;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziBossEntity;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziCloneEntity;
import net.superkat.bonzibuddy.item.BonziItems;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.minigame.BonziBossBarUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.MinigameHudUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.WaitingForPlayersS2C;

import java.util.Set;
import java.util.UUID;

public class TripleChaosMinigame extends BonziMinigame {
    public int ticksLeft;
    public int secondsLeft;
    public BlockPos currentEnemySpawnPos = BlockPos.ORIGIN;
    public int ticksUntilEnemy;
    private int enemiesToSpawn = 3;
    private int maxEnemiesToSpawn;
    public int minTicksUntilEnemy; //should be adjusted based on difficulty but may not have time for balancing that
    public int maxTicksUntilEnemy;
    public int maxWaitTicksAfterNewEnemyPos = 300;
    public int ticksUntilNewEnemyPos;

    public int difficultyLevel = 1;

    private UUID redBonziUUID;
    private UUID greenBonziUUID;
    private UUID blueBonziUUID;
    public BonziBossEntity redBonzi = null;
    public BonziBossEntity greenBonzi = null;
    public BonziBossEntity blueBonzi = null;
    //percents are from 0f to 1f, with 0.5f = 50%
    public float redBonziPercent = 1f;
    public float greenBonziPercent = 1f;
    public float blueBonziPercent = 1f;
    public float redBonziInitHealth = 400f;
    public float greenBonziInitHealth = 550f;
    public float blueBonziInitHealth = 650f;

    public Set<Item> hatsToReward = Sets.newHashSet();

    public TripleChaosMinigame(int id, ServerWorld world, BlockPos startPos) {
        super(id, world, startPos);
        maxTicksUntilEnemy = 30;
        minTicksUntilEnemy = 10;
        maxEnemiesToSpawn = 3;
    }
    public TripleChaosMinigame(ServerWorld world, NbtCompound nbt) {
        super(world, nbt);
        maxTicksUntilEnemy = 30;
        minTicksUntilEnemy = 10;
        maxEnemiesToSpawn = 3;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.ticksLeft = nbt.getInt("ticksLeft");
        this.secondsLeft = nbt.getInt("secondsLeft");
        NbtHelper.toBlockPos(nbt, "currentEnemySpawnPos").ifPresent(pos -> this.currentEnemySpawnPos = pos);
        this.ticksUntilEnemy = nbt.getInt("ticksUntilEnemy");
        this.enemiesToSpawn = nbt.getInt("enemiesToSpawn");
        this.maxWaitTicksAfterNewEnemyPos = nbt.getInt("maxWaitTicksAfterNewEnemyPos");
        this.ticksUntilNewEnemyPos = nbt.getInt("ticksUntilNewEnemyPos");
        this.difficultyLevel = nbt.getInt("difficultyLevel");

        this.redBonziPercent = nbt.getFloat("redBonziPercent");
        this.greenBonziPercent = nbt.getFloat("greenBonziPercent");
        this.blueBonziPercent = nbt.getFloat("blueBonziPercent");
        this.hudData.redBonziPercent = redBonziPercent;
        this.hudData.greenBonziPercent = greenBonziPercent;
        this.hudData.blueBonziPercent = blueBonziPercent;

        if(redBonziPercent > 0f) {
            NbtElement redBonziElement = nbt.get("redBonzi");
            if(redBonziElement != null) {
                this.redBonziUUID = NbtHelper.toUuid(redBonziElement);
            }
        }


        if (greenBonziPercent > 0f) {
            NbtElement greenBonziElement = nbt.get("greenBonzi");
            if(greenBonziElement != null) {
                this.greenBonziUUID = NbtHelper.toUuid(greenBonziElement);
            }
        }

        if (blueBonziPercent > 0f) {
            NbtElement blueBonziElement = nbt.get("blueBonzi");
            if(blueBonziElement != null) {
                this.blueBonziUUID = NbtHelper.toUuid(blueBonziElement);
            }
        }


        super.readNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("ticksLeft", this.ticksLeft);
        nbt.putInt("secondsLeft", this.secondsLeft);
        nbt.put("currentEnemySpawnPos", NbtHelper.fromBlockPos(this.currentEnemySpawnPos));
        nbt.putInt("ticksUntilEnemy", this.ticksUntilEnemy);
        nbt.putInt("enemiesToSpawn", this.enemiesToSpawn);
        nbt.putInt("maxWaitTicksAfterNewEnemyPos", this.maxWaitTicksAfterNewEnemyPos);
        nbt.putInt("ticksUntilNewEnemyPos", this.ticksUntilNewEnemyPos);
        nbt.putInt("difficultyLevel", this.difficultyLevel);

        nbt.putFloat("redBonziPercent", this.redBonziPercent);
        nbt.putFloat("greenBonziPercent", this.greenBonziPercent);
        nbt.putFloat("blueBonziPercent", this.blueBonziPercent);
        if(redBonzi != null && redBonzi.isAlive()) {
            nbt.put("redBonzi", NbtHelper.fromUuid(this.redBonzi.getUuid()));
        }
        if(greenBonzi != null && greenBonzi.isAlive()) {
            nbt.put("greenBonzi", NbtHelper.fromUuid(this.greenBonzi.getUuid()));
        } if(blueBonzi != null && blueBonzi.isAlive()) {
            nbt.put("blueBonzi", NbtHelper.fromUuid(this.blueBonzi.getUuid()));
        }
        return super.writeNbt(nbt);
    }

    /**
     * Set the difficulty of the Minigame, not necessary related to the world's Difficulty. This should be adjusted based on the world's Difficulty AND the amount of joining players, OR be allowed to be overridden by the starting player.
     *
     * @param difficulty The difficulty level. For example, 1 should be easy mode, 3 should be hard mode, and higher numbers can be given if there are more players.
     */
    public void setDifficultyLevel(int difficulty) {
        this.difficultyLevel = difficulty;
    }

    public void scaleFromDifficulty() {
        //yeah good luck lol
        this.redBonziInitHealth = (float) (280 + 0.005 * (difficultyLevel * 100) * 400);
        this.greenBonziInitHealth = (float) (385 + 0.005 * (difficultyLevel * 100) * 550);
        this.blueBonziInitHealth = (float) (455 + 0.005 * (difficultyLevel * 100) * 650);

        if(difficultyLevel < 3) {
            maxTicksUntilEnemy = 37 - difficultyLevel * 2;
            minTicksUntilEnemy = 10 + difficultyLevel * 2;
        } else {
            maxTicksUntilEnemy = 31 - difficultyLevel / 3;
            minTicksUntilEnemy = MathHelper.clamp(10 - difficultyLevel, 7, 10);
        }
        maxEnemiesToSpawn = (int) Math.ceil(difficultyLevel * 1.5);

        maxWaitTicksAfterNewEnemyPos = (int) (double) (300 - difficultyLevel * difficultyLevel * 5);
    }

    @Override
    public void addPlayer(ServerPlayerEntity player) {
        super.addPlayer(player);
        BonziBossBarUpdateS2C redPacket = new BonziBossBarUpdateS2C(this.hudData.uuid, this.hudData.redBonziPercent, BonziBossBarUpdateS2C.BonziBoss.RED);
        BonziBossBarUpdateS2C greenPacket = new BonziBossBarUpdateS2C(this.hudData.uuid, this.hudData.greenBonziPercent, BonziBossBarUpdateS2C.BonziBoss.GREEN);
        BonziBossBarUpdateS2C bluePacket = new BonziBossBarUpdateS2C(this.hudData.uuid, this.hudData.blueBonziPercent, BonziBossBarUpdateS2C.BonziBoss.BLUE);
        ServerPlayNetworking.send(player, redPacket);
        ServerPlayNetworking.send(player, greenPacket);
        ServerPlayNetworking.send(player, bluePacket);
    }

    @Override
    public void start() {
        secondsLeft = 100;
        ticksLeft = secondsLeft * 20;

        hudData.time = secondsLeft;
        hudData.redBonziPercent = 1f;
        hudData.greenBonziPercent = 1f;
        hudData.blueBonziPercent = 1f;
        super.start();
    }

    @Override
    public void tick() {
        super.tick();

        if(!isLoaded()) {

            if(ticksUntilInvalidate % 20 == 0) {
                updateInvolvedPlayers();
            }

            ticksUntilInvalidate--;

            if(ticksUntilInvalidate <= 0 && ticksSinceReload >= 100) {
                if(fullLogs()) BonziBUDDY.LOGGER.info("Triple Chaos Minigame {} invalidated because of game end (unloaded)", this.getId());
                invalidate();
            }
            return;
        }

        for (ServerPlayerEntity player : players()) {
            if(player != null) {
                if(player.getY() < (double)(this.getWorld().getBottomY())) {
                    //if the player falls into the void - doesn't kill them because bugs'o'plenty
                    player.teleport(this.world, this.startPos.getX(), this.startPos.getY() + 2, this.startPos.getZ(), 0f, 0f);
                    if(fullLogs()) BonziBUDDY.LOGGER.info("Triple Chaos Minigame {} - teleported player {} because they fell in the void", this.getId(), player.getName());
                }
            }
        }

        if(onGoing()) {
            if((redBonzi == null || greenBonzi == null || blueBonzi == null) && ticksSinceReload >= 40) {
                if(loadedFromNbt) {
                    if(this.redBonziUUID != null) {
                        BonziBossEntity bonziBoss = (BonziBossEntity) this.getWorld().getEntity(redBonziUUID);
                        if(bonziBoss != null) {
                            this.redBonzi = bonziBoss;
                        }
                    }

                    if(this.greenBonziUUID != null) {
                        BonziBossEntity bonziBoss = (BonziBossEntity) this.getWorld().getEntity(greenBonziUUID);
                        if(bonziBoss != null) {
                            this.greenBonzi = bonziBoss;
                        }
                    }

                    if(this.blueBonziUUID != null) {
                        BonziBossEntity bonziBoss = (BonziBossEntity) this.getWorld().getEntity(blueBonziUUID);
                        if(bonziBoss != null) {
                            this.blueBonzi = bonziBoss;
                        }
                    }
                } else {
                    scaleFromDifficulty();
                    spawnBonziBuddies();
                }
            }
            ticksLeft--;

            ticksUntilEnemy--;
            if(ticksUntilEnemy <= 0) {
                spawnClone();
            }

            ticksUntilNewEnemyPos--;
            if(ticksUntilNewEnemyPos <= 0) {
                newEnemySpawnPos();
            }
        }
    }

    @Override
    public void tickSecond() {
        secondsLeft = ticksLeft / 20;
        hudData.setTime(secondsLeft);
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
    public void end() {
        if(bonziBuddiesDefeated()) {
            win();
            ticksUntilInvalidate = 300;
            if(extraLogs()) BonziBUDDY.LOGGER.info("Ended Triple Chaos Minigame {} (Bonzi Buddies defeated)", this.getId());
        } else {
            lose();
            ticksUntilInvalidate = 140;
            if(extraLogs()) BonziBUDDY.LOGGER.info("Ended Triple Chaos Minigame {} (Defeat likely)", this.getId());
        }
        cachePlayerRewards();
        discardAllEnemies();
    }

    @Override
    public void invalidate() {
        discardAllEnemies();
        rewardPlayers();

        if(this.world.random.nextBetween(1, 4) == 1) {
            players().forEach(player -> {
                if(player == null) return;
                player.addStatusEffect(new StatusEffectInstance(BonziBuddyEntities.BONZID_EFFECT, 1800, 1, false, false, true));
            });
        }

        super.invalidate();
    }

    @Override
    public void startGracePeriod() {
        gracePeriodSeconds = 17;

        hudData.gracePeriod = gracePeriodSeconds;
        super.startGracePeriod();
    }

    @Override
    public void gracePeriodTickSecond() {
        super.gracePeriodTickSecond();
        hudData.gracePeriod = gracePeriodSeconds;
        if(gracePeriodSeconds <= 10) {
            sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.UPDATE_GRACE_PERIOD);
        } else {
            sendPacketToInvolvedPlayers(new WaitingForPlayersS2C());
        }
    }

    public void spawnBonziBuddies() {
        this.redBonzi = (BonziBossEntity) spawnEntity(BonziBuddyEntities.BONZI_BOSS);
        this.redBonzi.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(redBonziInitHealth);
        this.redBonzi.setIsRed(true);
        this.redBonzi.setTripleChaosMinigame(this);

        this.greenBonzi = (BonziBossEntity) spawnEntity(BonziBuddyEntities.BONZI_BOSS);
        this.greenBonzi.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(greenBonziInitHealth);
        this.greenBonzi.setIsGreen(true);
        this.greenBonzi.setTripleChaosMinigame(this);

        this.blueBonzi = (BonziBossEntity) spawnEntity(BonziBuddyEntities.BONZI_BOSS);
        this.blueBonzi.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(blueBonziInitHealth);
        this.blueBonzi.setIsBlue(true);
        this.blueBonzi.setTripleChaosMinigame(this);

        if(fullLogs()) BonziBUDDY.LOGGER.info("Spawned Bonzi Buddies!");
    }

    public void spawnClone() {
        ticksUntilEnemy = world.random.nextBetween(minTicksUntilEnemy, maxTicksUntilEnemy);
        enemiesToSpawn--;

        if(currentEnemySpawnPos != null && enemies.size() < maxEnemies) {
            BonziCloneEntity clone = BonziBuddyEntities.BONZI_CLONE.create(world);
            clone.setPos(currentEnemySpawnPos.getX(), currentEnemySpawnPos.getY(), currentEnemySpawnPos.getZ());
            clone.initialize(world, world.getLocalDifficulty(currentEnemySpawnPos), SpawnReason.EVENT, null);
            world.spawnEntity(clone);

            //more banana blasters on higher difficulty
            //also guarantees at least one clone spawns that can drop the banana blaster
            if(enemiesToSpawn <= 0 || (this.difficultyLevel >= 3 && this.world.random.nextBetween(1, Math.max(25 / difficultyLevel, 3)) == 1)) {
                if(clone.scale < 2f) {
                    clone.setScale(MathHelper.nextFloat(this.world.random, 2f, 3f));
                }
            }

            addEnemy(clone);
        }

        if(enemiesToSpawn <= 0) {
            enemiesToSpawn = this.world.random.nextBetween(1, maxEnemiesToSpawn);
            //at least 5 seconds, at most 12 seconds
            ticksUntilEnemy = MathHelper.clamp(ticksUntilEnemy * 4, 100, maxWaitTicksAfterNewEnemyPos);
        }
    }

    public void addEnemy(MobEntity enemy) {
        super.addEnemy(enemy);
        if(enemy instanceof AbstractBonziCloneEntity clone) {
            clone.setTripleChaosMinigame(this);
        }
        ServerPlayerEntity target = randomPlayer();
        if(target != null) {
            enemy.setTarget(target);
        }
    }

    public void newEnemySpawnPos() {
        BlockPos newPos = BonziMinigameApi.getEnemySpawnPos(world, startPos, 1, 20);
        if(newPos != null) {
            this.currentEnemySpawnPos = newPos;
        }
        ticksUntilNewEnemyPos = world.random.nextBetween(140, 300);
    }

    public void discardAllEnemies() {
        if(this.redBonzi != null) {
            this.redBonzi.discard();
        }

        if(this.greenBonzi != null) {
            this.greenBonzi.discard();
        }

        if(this.blueBonzi != null) {
            this.blueBonzi.discard();
        }

        this.enemies.forEach(enemyUuid -> {
            Entity enemy = this.getWorld().getEntity(enemyUuid);
            if(enemy != null) {
                enemy.discard();
            }
        });

        if(fullLogs()) BonziBUDDY.LOGGER.info("Discarded all enemies!");
    }
    
    public void updateBossHealth(BonziBossEntity boss) {
        BonziBossBarUpdateS2C.BonziBoss type = BonziBossBarUpdateS2C.BonziBoss.RED;
        boolean sendPacket = false;
        float percent = boss.getHealth() / boss.getMaxHealth();
        if(this.redBonzi == boss) {
            this.redBonziPercent = percent;
            sendPacket = true;
        } else if (this.greenBonzi == boss) {
            this.greenBonziPercent = percent;
            type = BonziBossBarUpdateS2C.BonziBoss.GREEN;
            sendPacket = true;
        } else if (this.blueBonzi == boss) {
            this.blueBonziPercent = percent;
            type = BonziBossBarUpdateS2C.BonziBoss.BLUE;
            sendPacket = true;
        }
        if(sendPacket) {
            this.sendPacketToInvolvedPlayers(new BonziBossBarUpdateS2C(this.hudData.uuid, percent, type));
        }
    }

    public void bossDefeated(BonziBossEntity boss) {
        boolean sendPacket = false;
        if(this.redBonzi == boss) {
            this.redBonziPercent = boss.getHealth() / boss.getMaxHealth();
            hudData.setDefeatedBoss(Text.translatable("bonzibuddy.minigame.redbonzi").getString());
            sendPacket = true;
        } else if (this.greenBonzi == boss) {
            this.greenBonziPercent = boss.getHealth() / boss.getMaxHealth();
            hudData.setDefeatedBoss(Text.translatable("bonzibuddy.minigame.greenbonzi").getString());
            sendPacket = true;
        } else if (this.blueBonzi == boss) {
            this.blueBonziPercent = boss.getHealth() / boss.getMaxHealth();
            hudData.setDefeatedBoss(Text.translatable("bonzibuddy.minigame.bluebonzi").getString());
            sendPacket = true;
        }
        if(sendPacket) {
            this.sendUpdateMinigameHudPacket(MinigameHudUpdateS2C.Action.BOSS_DEFEATED);
        }

        //little bonus :)
        players().forEach(player -> {
            if(player == null) return;
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 60, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 60, 1));
        });
    }

    private LivingEntity spawnEntity(EntityType<? extends MobEntity> type) {
        MobEntity entity = type.create(this.world);
        BlockPos spawnPos = BonziMinigameApi.getEnemySpawnPos(this.world, this.startPos, 1, 20);
        if(spawnPos == null) {
            spawnPos = startPos.add(0, 2, 0); //fallback
        }
        entity.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        entity.initialize(this.world, this.world.getLocalDifficulty(spawnPos), SpawnReason.EVENT, null);
        this.world.spawnEntity(entity);
        return entity;
    }

    @Override
    public boolean checkForGameEnd() {
        return (ticksLeft <= -19 || playersAlive() <= 0 || bonziBuddiesDefeated() || !onGoing()) && !gracePeriod();
    }

    public boolean bonziBuddiesDefeated() {
        if(this.world.getDifficulty() == Difficulty.PEACEFUL) return true;
        boolean redDefeated = redBonzi == null || redBonzi.isDead() || redBonzi.getRemovalReason() == Entity.RemovalReason.DISCARDED;
        boolean greenDefeated = greenBonzi == null || greenBonzi.isDead() || greenBonzi.getRemovalReason() == Entity.RemovalReason.DISCARDED;
        boolean blueDefeated = blueBonzi == null || blueBonzi.isDead() || blueBonzi.getRemovalReason() == Entity.RemovalReason.DISCARDED;
        return redDefeated && greenDefeated && blueDefeated;
    }

    /**
     * The player's rewards are determined and saved right as the game ends, then are given to them as they are returned back to their spawns/to the overworld.
     *
     * Each player should be given the same amount of hats, and the same hats because I think that's more fun.
     */
    public void cachePlayerRewards() {
        boolean redDefeated = redBonzi == null || redBonzi.isDead() || redBonzi.getRemovalReason() == Entity.RemovalReason.DISCARDED;
        boolean greenDefeated = greenBonzi == null || greenBonzi.isDead() || greenBonzi.getRemovalReason() == Entity.RemovalReason.DISCARDED;
        boolean blueDefeated = blueBonzi == null || blueBonzi.isDead() || blueBonzi.getRemovalReason() == Entity.RemovalReason.DISCARDED;
        boolean shouldGetGoldenHat = redDefeated && greenDefeated && blueDefeated;

        int hats = 0;
        hats += redDefeated ? 1 : determineHatsFromPercent(redBonziPercent) ? 1 : 0;
        hats += greenDefeated ? 1 : determineHatsFromPercent(greenBonziPercent) ? 1 : 0;
        hats += blueDefeated ? 1 : determineHatsFromPercent(blueBonziPercent) ? 1 : 0;

        if(hats >= 1) {
            if(shouldGetGoldenHat) {
                hatsToReward.add(BonziItems.GOLDEN_BONZI_HAT);
            }

            for (int i = 0; i < hats; i++) {
                int hatToGet = this.world.random.nextInt(BonziItems.hats.size());
                Item hat = BonziItems.hats.get(hatToGet);
                this.hatsToReward.add(hat);
            }
        }

        if(fullLogs()) BonziBUDDY.LOGGER.info("Caching player rewards...");
    }

    /**
     * Determine the amount of extra hats to give if a boss wasn't defeated, based on their remaining health.
     *
     * @param percent A boss' health in percent, 0f to 1f
     */
    private boolean determineHatsFromPercent(float percent) {
        float percentForNoHat = MathHelper.clamp(0.5f + percent, 0.5f, 1f);
        if(percentForNoHat >= 1f) {
            return false;
        } else {
            float percentForHat = 1f - percentForNoHat;
            float randomFloat = MathHelper.nextFloat(this.getWorld().random, 0f, 1f);
            return randomFloat <= percentForHat;
        }
    }

    public void rewardPlayers() {
        if(this.hatsToReward.isEmpty()) return;

        this.players().forEach(player -> {
            if(player == null) return;
            this.hatsToReward.forEach(item -> {
               player.giveItemStack(item.getDefaultStack());
            });
        });
    }

    @Override
    public MinigameHudData createHudData() {
        return new MinigameHudData(this.getMinigameType(), "Triple Bonzi Chaos");
    }

    @Override
    public BonziMinigameType getMinigameType() {
        return BonziMinigameType.TRIPLE_CHAOS;
    }
}
