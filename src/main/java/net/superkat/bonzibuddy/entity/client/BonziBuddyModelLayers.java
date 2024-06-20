package net.superkat.bonzibuddy.entity.client;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.entity.client.model.BananaBlasterEntityModel;

public class BonziBuddyModelLayers {
    //I don't really know what the "name" param in the EntityModelLayer does
//    public static final EntityModelLayer BONZI_BUDDY_LAYER =
//            new EntityModelLayer(Identifier.of(BonziBUDDY.MOD_ID, "bonzibuddy"), "bonzibuddy");

    public static final EntityModelLayer BANANA_BLASTER_LAYER =
            new EntityModelLayer(Identifier.of(BonziBUDDY.MOD_ID, "bananablaster"), "bananablaster");

    /**
     * Register all the EntityModelLayers used.
     */
    public static void registerModelLayers() {
//        EntityModelLayerRegistry.registerModelLayer(BONZI_BUDDY_LAYER, BonziLikeModel::getBonziModelData);
        EntityModelLayerRegistry.registerModelLayer(BANANA_BLASTER_LAYER, BananaBlasterEntityModel::getTexturedModelData);
    }
}
