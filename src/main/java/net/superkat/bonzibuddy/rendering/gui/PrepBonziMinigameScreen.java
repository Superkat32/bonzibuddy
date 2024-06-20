package net.superkat.bonzibuddy.rendering.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.superkat.bonzibuddy.minigame.api.BonziMinigameType;
import net.superkat.bonzibuddy.network.packets.minigame.RequestPlayMinigameC2S;

public class PrepBonziMinigameScreen extends Screen {

    //This could be a widget instead depending on how we play our cards

    public PrepBonziMinigameScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        //FIXME - translatable string
        ButtonWidget playCatastrophicClones = ButtonWidget.builder(Text.of("Let's Go!"), (btn) -> {
            requestPlayTripleChaos();
        }).dimensions(40, 40, 120, 20).build();
        this.addDrawableChild(playCatastrophicClones);
    }

    public void requestPlayTripleChaos() {
        //Assumes all nearby players are entering
        ClientPlayNetworking.send(new RequestPlayMinigameC2S(BonziMinigameType.TRIPLE_CHAOS));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
