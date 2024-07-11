package net.superkat.bonzibuddy;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.superkat.bonzibuddy.entity.AirplaneParticle;
import net.superkat.bonzibuddy.entity.BonziBuddyEntities;
import net.superkat.bonzibuddy.entity.client.BonziBuddyModelLayers;
import net.superkat.bonzibuddy.entity.client.renderer.BananaBlasterEntityRenderer;
import net.superkat.bonzibuddy.entity.client.renderer.BonziBuddyEntityRenderer;
import net.superkat.bonzibuddy.entity.client.renderer.mob.BonziBossEntityRenderer;
import net.superkat.bonzibuddy.entity.client.renderer.mob.BonziCloneEntityRenderer;
import net.superkat.bonzibuddy.minigame.room.FriendRoomManager;
import net.superkat.bonzibuddy.network.BonziBuddyClientNetworkHandler;
import net.superkat.bonzibuddy.rendering.hud.MinigameHudRenderer;

public class BonziBUDDYClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ParticleFactoryRegistry.getInstance().register(BonziBUDDY.PAPER_AIRPLANE, AirplaneParticle.Factory::new);

        //Registers Bonzi Buddy Entity model and renderer
        BonziBuddyModelLayers.registerModelLayers();
        EntityRendererRegistry.register(BonziBuddyEntities.BONZI_BUDDY, BonziBuddyEntityRenderer::new);

        EntityRendererRegistry.register(BonziBuddyEntities.PROTECTABLE_BONZI_BUDDY, BonziBuddyEntityRenderer::new);
        EntityRendererRegistry.register(BonziBuddyEntities.BONZI_CLONE, BonziCloneEntityRenderer::new);
        EntityRendererRegistry.register(BonziBuddyEntities.BONZI_BOSS, BonziBossEntityRenderer::new);
        EntityRendererRegistry.register(BonziBuddyEntities.BANANA_BLASTER_PROJECTILE, BananaBlasterEntityRenderer::new);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            MinigameHudRenderer.minigameHuds.clear();
            FriendRoomManager.rooms.clear();
            FriendRoomManager.currentRoom = null;
        });
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if(!MinigameHudRenderer.minigameHuds.values().isEmpty()) {
                MinigameHudRenderer.ticksSinceUpdate++;
                //no update should take more than a second, let alone 15 seconds
                if(MinigameHudRenderer.ticksSinceUpdate >= 300) {
                    MinigameHudRenderer.minigameHuds.clear();
                }
            }
        });

        //Packets
        BonziBuddyClientNetworkHandler.registerClientPackets();

        MinigameHudRenderer.registerHudRenderEvents();
    }
}
