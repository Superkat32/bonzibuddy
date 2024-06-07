package net.superkat.bonzibuddy;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.superkat.bonzibuddy.entity.client.BonziBuddyModelLayers;
import net.superkat.bonzibuddy.entity.client.renderer.BonziBuddyEntityRenderer;

public class BonziBUDDYClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        //Registers Bonzi Buddy Entity model and renderer
        EntityRendererRegistry.register(BonziBUDDY.BONZI_BUDDY, BonziBuddyEntityRenderer::new);
        BonziBuddyModelLayers.registerModelLayers();
    }
}
