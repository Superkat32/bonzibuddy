package net.superkat.bonzibuddy.rendering.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.entity.bonzi.BonziBuddyEntity;
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
        //FIXME - translatable string
        ButtonWidget button = ButtonWidget.builder(Text.translatable("bonzibuddy.doatrick"), (btn) -> {
            doATrick();
        }).dimensions(260, 40, 120, 20).build();
        this.addDrawableChild(button);

        ButtonWidget playTripleChaos = ButtonWidget.builder(Text.translatable("bonzibuddy.playtriplechaos"), (btn) -> {
            openPrepMinigameScreen();
        }).dimensions(260, 100, 120, 20).build();
        this.addDrawableChild(playTripleChaos);

        //TODO - email animation
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
//        InventoryScreen.drawEntity();
//        context.fill(70, this.height / 2 - 20, this.width / 2, this.height / 2 + 90, Color.WHITE.getRGB());
    }

    public void doATrick() {
        if(bonziBuddyEntity != null) {
            ClientPlayNetworking.send(new BonziBuddyDoATrickC2S(bonziBuddyEntity.getId()));
        }
    }

    public void openPrepMinigameScreen() {
        BlockPos bonziPos = bonziBuddyEntity == null ? this.client.player.getBlockPos() : bonziBuddyEntity.getBlockPos();
        this.client.setScreen(new PrepBonziMinigameScreen(bonziPos));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
