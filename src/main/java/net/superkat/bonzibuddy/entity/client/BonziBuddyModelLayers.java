package net.superkat.bonzibuddy.entity.client;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.client.model.BonziBuddyEntityModel;

public class BonziBuddyModelLayers {
    public static final EntityModelLayer BONZI_BUDDY_LAYER =
            new EntityModelLayer(Identifier.of(BonziBUDDY.MOD_ID, "bonzibuddy"), "cube");

    public static void registerModelLayers() {
        EntityModelLayerRegistry.registerModelLayer(BONZI_BUDDY_LAYER, BonziBuddyEntityModel::getTexturedModelData);
    }
}
