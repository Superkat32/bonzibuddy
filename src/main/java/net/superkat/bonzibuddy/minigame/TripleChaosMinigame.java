package net.superkat.bonzibuddy.minigame;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.superkat.bonzibuddy.entity.BonziBuddyEntities;
import net.superkat.bonzibuddy.entity.bonzi.minigame.mob.BonziBossEntity;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameApi;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.minigame.BonziBossBarUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.MinigameHudUpdateS2C;
import net.superkat.bonzibuddy.network.packets.minigame.WaitingForPlayersS2C;

public class TripleChaosMinigame extends BonziMinigame {
    public int ticksLeft;
    public int secondsLeft;

    public BonziBossEntity redBonzi = null;
    public BonziBossEntity greenBonzi = null;
    public BonziBossEntity blueBonzi = null;
    public float redBonziPercent = 100f;
    public float greenBonziPercent = 100f;
    public float blueBonziPercent = 100f;

    public TripleChaosMinigame(int id, ServerWorld world, BlockPos startPos) {
        super(id, world, startPos);
    }
    public TripleChaosMinigame(ServerWorld world, NbtCompound nbt) {
        super(world, nbt);
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

        for (ServerPlayerEntity player : players()) {
            if(player == null) return;
            if(player.getY() < (double)(this.getWorld().getBottomY())) {
                //if the player falls into the void - doesn't kill them because bugs'o'plenty
                player.teleport(this.world, this.startPos.getX(), this.startPos.getY() + 2, this.startPos.getZ(), 0f, 0f);
            }
        }

        if(onGoing()) {
            if(redBonzi == null || greenBonzi == null || blueBonzi == null) {
                spawnBonziBuddies();
            }
            ticksLeft--;
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
        } else {
            lose();
            ticksUntilInvalidate = 140;
        }
        discardAllEnemies();
    }

    @Override
    public void invalidate() {
        discardAllEnemies();

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
        this.redBonzi.setTripleChaosMinigame(this);
        this.greenBonzi = (BonziBossEntity) spawnEntity(BonziBuddyEntities.BONZI_BOSS);
        this.greenBonzi.setTripleChaosMinigame(this);
        this.blueBonzi = (BonziBossEntity) spawnEntity(BonziBuddyEntities.BONZI_BOSS);
        this.blueBonzi.setTripleChaosMinigame(this);
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
        return (ticksLeft <= 0 || playersAlive() <= 0 || bonziBuddiesDefeated() || !onGoing()) && !gracePeriod();
    }

    public boolean bonziBuddiesDefeated() {
        if(this.world.getDifficulty() == Difficulty.PEACEFUL) return true;
        boolean redDefeated = redBonzi == null || redBonzi.isDead();
        boolean greenDefeated = greenBonzi == null || greenBonzi.isDead();
        boolean blueDefeated = blueBonzi == null || blueBonzi.isDead();
        return redDefeated && greenDefeated && blueDefeated;
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
