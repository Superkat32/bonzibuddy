package net.superkat.bonzibuddy.rendering.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.superkat.bonzibuddy.entity.bonzi.BonziBuddyEntity;
import net.superkat.bonzibuddy.network.packets.BonziAirplaneC2S;
import net.superkat.bonzibuddy.network.packets.BonziBuddyDoATrickC2S;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class BonziBuddyScreen extends Screen {
    @Nullable //nullable for possible commands allowing to open this screen?
    public BonziBuddyEntity bonziBuddyEntity;
    public boolean tripleChaosEnabled = true;

    public BonziBuddyScreen() {
        super(Text.of("Bonzi buddy screen"));
    }

    public BonziBuddyScreen(World world, int bonziBuddyId, boolean tripleChaosEnabled) {
        this();
        this.bonziBuddyEntity = (BonziBuddyEntity) world.getEntityById(bonziBuddyId);
        this.tripleChaosEnabled = tripleChaosEnabled;
    }

    @Override
    protected void init() {

        VeryFancyButtonWidget doATrick = new VeryFancyButtonWidget(
                this.width / 2 + 20,
                this.height / 2 - 85,
                160, 28,
                Text.translatable("bonzibuddy.doatrick"),
                (btn) -> doATrick()
        );
        this.addDrawableChild(doATrick);

        VeryFancyButtonWidget sendEmail = new VeryFancyButtonWidget(
                this.width / 2 + 20,
                this.height / 2 - 25,
                160, 28,
                Text.translatable("bonzibuddy.email"),
                (btn) -> sendEmail()
        );
        this.addDrawableChild(sendEmail);

        VeryFancyButtonWidget playTripleChaos = new VeryFancyButtonWidget(
                this.width / 2 + 20,
                this.height / 2 + 40,
                160, 28,
                Text.translatable("bonzibuddy.playtriplechaos"),
                (btn) -> openPrepMinigameScreen()
        ).showOutOfOrder(!tripleChaosEnabled);
        this.addDrawableChild(playTripleChaos);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if(bonziBuddyEntity != null) {
            InventoryScreen.drawEntity(context, 50, this.height / 2 - 110, this.width / 2 - 20, this.height / 2 + 90, 75, 0.0625F, mouseX, mouseY, this.bonziBuddyEntity);
        }
        context.fill(50, this.height / 2 - 110, this.width / 2 - 20, this.height / 2 + 90, Color.GRAY.getRGB());
        context.fill(50, this.height / 2 - 10, this.width / 2 - 20 + 2, this.height / 2 + 90 + 2, new Color(0, 0, 0, 128).getRGB());
    }

    public void doATrick() {
        if(bonziBuddyEntity != null) {
            ClientPlayNetworking.send(new BonziBuddyDoATrickC2S(bonziBuddyEntity.getId()));
        }
    }

    public void sendEmail() {
        if(bonziBuddyEntity != null) {
            ClientPlayNetworking.send(new BonziAirplaneC2S(bonziBuddyEntity.getId()));
        }
    }

    public void openPrepMinigameScreen() {
        BlockPos bonziPos = bonziBuddyEntity == null ? this.client.player.getBlockPos() : bonziBuddyEntity.getBlockPos();
        this.client.setScreen(new BrowseFriendRoomsScreen());
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
