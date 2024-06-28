package net.superkat.bonzibuddy.rendering.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.text.Text;

public class ConfirmLeaveMinigameWorldScreen extends ConfirmScreen {
    public ConfirmLeaveMinigameWorldScreen(BooleanConsumer callback, Text title, Text message) {
        super(callback, title, message);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
