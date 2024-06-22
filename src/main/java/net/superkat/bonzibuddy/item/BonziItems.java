package net.superkat.bonzibuddy.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.superkat.bonzibuddy.BonziBUDDY;
import net.superkat.bonzibuddy.item.hat.HatItem;

public class BonziItems {

    public static final Item BANANA_BLASTER = register(
            new BananaBlasterItem(new Item.Settings()),
            "banana_blaster"
    );

    public static final Item TATER_HAT = registerHat("tater");
    public static final Item BONZI_HAT = registerHat("bonzihat");
    public static final Item BONZI_SUNGLASSES = registerHat("bonzisunglasses");
    public static final Item BONZI_BEANIE = registerHat("bonzibeanie");
    public static final Item BONZI_BUCKET_HAT = registerHat("bonzibucket");
    public static final Item BONZI_GOGGLES = registerHat("bonzigoggles");

    public static <T extends Item> T register(T item, String ID) {
        Identifier itemId = Identifier.of(BonziBUDDY.MOD_ID, ID);

        return Registry.register(Registries.ITEM, itemId, item);
    }

    public static HatItem registerHat(String id) {
        Identifier itemId = Identifier.of(BonziBUDDY.MOD_ID, id);

        return Registry.register(Registries.ITEM, itemId, new HatItem());
    }

    public static void registerItems() {
        ItemGroupEvents.modifyEntriesEvent(
                ItemGroups.TOOLS).register((itemGroup) -> {
            itemGroup.add(BANANA_BLASTER);
        });

        ItemGroupEvents.modifyEntriesEvent(
                ItemGroups.COMBAT).register((itemGroup) -> {
                    itemGroup.add(TATER_HAT);
                    itemGroup.add(BONZI_HAT);
                    itemGroup.add(BONZI_SUNGLASSES);
                    itemGroup.add(BONZI_BEANIE);
                    itemGroup.add(BONZI_BUCKET_HAT);
                    itemGroup.add(BONZI_GOGGLES);
        });
    }

}
