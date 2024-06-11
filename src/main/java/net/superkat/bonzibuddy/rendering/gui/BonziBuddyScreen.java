package net.superkat.bonzibuddy.rendering.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.entity.BonziBuddyEntity;
import net.superkat.bonzibuddy.network.packets.BonziBuddyDoATrickC2S;
import org.jetbrains.annotations.Nullable;

public class BonziBuddyScreen extends Screen {
    @Nullable //nullable for possible commands allowing to open this screen?
    public BonziBuddyEntity bonziBuddyEntity;
    public BonziBuddyScreen() {
        super(Text.of("Bonzi buddy screen"));
    }

    public BonziBuddyScreen(World world, int bonziBuddyId) {
        this();
        this.bonziBuddyEntity = (BonziBuddyEntity) world.getEntityById(bonziBuddyId);
    }

    @Override
    protected void init() {
        ButtonWidget button = ButtonWidget.builder(Text.of("Do A Trick!"), (btn) -> {
            doATrick();
        }).dimensions(40, 40, 120, 20).build();

        this.addDrawableChild(button);
    }

    public void doATrick() {
        if(bonziBuddyEntity != null) {
            ClientPlayNetworking.send(new BonziBuddyDoATrickC2S(bonziBuddyEntity.getId()));
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
