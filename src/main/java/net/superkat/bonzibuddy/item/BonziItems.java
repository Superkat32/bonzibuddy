package net.superkat.bonzibuddy.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;

public class BonziItems {

    public static final Item BANANA_BLASTER = register(
            new BananaBlasterItem(new Item.Settings()),
            "banana_blaster"
    );

    public static <T extends Item> T register(T item, String ID) {
        Identifier itemId = Identifier.of(BonziBUDDY.MOD_ID, ID);

        return Registry.register(Registries.ITEM, itemId, item);
    }        

    public static void registerItems() {
        ItemGroupEvents.modifyEntriesEvent(
                ItemGroups.TOOLS).register((itemGroup) -> {
            itemGroup.add(BANANA_BLASTER);
        });
    }

}
