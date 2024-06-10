package net.superkat.bonzibuddy.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.superkat.bonzibuddy.entity.BonziBuddyEntity;
import net.superkat.bonzibuddy.network.packets.BonziBuddyDoATrickC2S;

public class BonziBuddyServerNetworkHandler {
    public static void registerServerPackets() {
        BonziBuddyPackets.registerPackets();

        //Server bound packets
        ServerPlayNetworking.registerGlobalReceiver(BonziBuddyDoATrickC2S.ID, BonziBuddyServerNetworkHandler::onBonziBuddyDoATrick);
    }

    public static void onBonziBuddyDoATrick(BonziBuddyDoATrickC2S payload, ServerPlayNetworking.Context context) {
        BonziBuddyEntity bonziBuddyEntity = (BonziBuddyEntity) context.player().getWorld().getEntityById(payload.bonziBuddyId());
        if(bonziBuddyEntity != null && bonziBuddyEntity.isAlive()) {
            bonziBuddyEntity.doATrick();
        }
    }
}
