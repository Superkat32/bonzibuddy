package net.superkat.bonzibuddy;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.superkat.bonzibuddy.entity.BonziBuddyEntities;
import net.superkat.bonzibuddy.entity.client.BonziBuddyModelLayers;
import net.superkat.bonzibuddy.entity.client.renderer.BonziBuddyEntityRenderer;
import net.superkat.bonzibuddy.entity.client.renderer.mob.BonziBossEntityRenderer;
import net.superkat.bonzibuddy.entity.client.renderer.mob.BonziCloneEntityRenderer;
import net.superkat.bonzibuddy.network.BonziBuddyClientNetworkHandler;
import net.superkat.bonzibuddy.rendering.hud.MinigameHudRenderer;

public class BonziBUDDYClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //Registers Bonzi Buddy Entity model and renderer
        BonziBuddyModelLayers.registerModelLayers();
        EntityRendererRegistry.register(BonziBuddyEntities.BONZI_BUDDY, BonziBuddyEntityRenderer::new);
//        EntityRendererRegistry.register(BonziBuddyEntities.BONZI_BUDDY, BonziBuddyEntityRenderer::new);

        EntityRendererRegistry.register(BonziBuddyEntities.PROTECTABLE_BONZI_BUDDY, BonziBuddyEntityRenderer::new);
        EntityRendererRegistry.register(BonziBuddyEntities.BONZI_CLONE, BonziCloneEntityRenderer::new);
        EntityRendererRegistry.register(BonziBuddyEntities.BONZI_BOSS, BonziBossEntityRenderer::new);


        //Packets
        BonziBuddyClientNetworkHandler.registerClientPackets();

        MinigameHudRenderer.registerHudRenderEvents();
    }
}
