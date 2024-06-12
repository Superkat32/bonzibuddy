package net.superkat.bonzibuddy.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.superkat.bonzibuddy.network.packets.BonziBuddyDoATrickC2S;
import net.superkat.bonzibuddy.network.packets.BonziBuddySyncAnimationS2C;
import net.superkat.bonzibuddy.network.packets.OpenBonziBuddyScreenS2C;
import net.superkat.bonzibuddy.network.packets.minigame.MinigameHudUpdateS2C;

public class BonziBuddyPackets {
    public static void registerPackets() {
        //Server bound packets
        PayloadTypeRegistry.playC2S().register(BonziBuddyDoATrickC2S.ID, BonziBuddyDoATrickC2S.CODEC);

        //Client bound packets
        PayloadTypeRegistry.playS2C().register(OpenBonziBuddyScreenS2C.ID, OpenBonziBuddyScreenS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(BonziBuddySyncAnimationS2C.ID, BonziBuddySyncAnimationS2C.CODEC);

        PayloadTypeRegistry.playS2C().register(MinigameHudUpdateS2C.ID, MinigameHudUpdateS2C.CODEC);
    }
}
