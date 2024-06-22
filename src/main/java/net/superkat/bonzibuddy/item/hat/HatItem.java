package net.superkat.bonzibuddy.item.hat;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;

public class HatItem extends Item implements Equipment {

    public HatItem() {
        super(new Item.Settings());
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }
}
