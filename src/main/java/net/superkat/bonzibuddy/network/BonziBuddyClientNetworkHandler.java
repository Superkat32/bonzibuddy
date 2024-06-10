package net.superkat.bonzibuddy.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.superkat.bonzibuddy.entity.BonziBuddyEntity;
import net.superkat.bonzibuddy.network.packets.BonziBuddySyncAnimationS2C;
import net.superkat.bonzibuddy.network.packets.OpenBonziBuddyScreenS2C;
import net.superkat.bonzibuddy.rendering.gui.BonziBuddyScreen;

public class BonziBuddyClientNetworkHandler {
    @Environment(EnvType.CLIENT)
    public static void registerClientPackets() {
        //Client bound packets
        ClientPlayNetworking.registerGlobalReceiver(OpenBonziBuddyScreenS2C.ID, BonziBuddyClientNetworkHandler::onBonziBuddyScreen);
        ClientPlayNetworking.registerGlobalReceiver(BonziBuddySyncAnimationS2C.ID, BonziBuddyClientNetworkHandler::onBonziBuddySyncAnimation);
    }

    public static void onBonziBuddyScreen(OpenBonziBuddyScreenS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        client.setScreen(new BonziBuddyScreen(context.player().getWorld(), payload.bonziBuddyId()));
    }

    public static void onBonziBuddySyncAnimation(BonziBuddySyncAnimationS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        BonziBuddyEntity bonziBuddy = (BonziBuddyEntity) client.world.getEntityById(payload.bonziBuddyId());
        int animationIndex = payload.bonziAnimationNumber();
        if(bonziBuddy != null && bonziBuddy.isAlive()) {
            BonziBuddyEntity.BonziAnimation animation = BonziBuddyEntity.BonziAnimation.getFromIndex(animationIndex);
            bonziBuddy.playAnimation(animation);
        }
    }
}