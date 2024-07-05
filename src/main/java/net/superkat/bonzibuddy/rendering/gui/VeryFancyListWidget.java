package net.superkat.bonzibuddy.rendering.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VeryFancyListWidget extends ClickableWidget implements ParentElement {
    public List<VeryFancyButtonWidget> buttons = Lists.newArrayList();
    public int screenHeight = 0;
    public int entryHeight = 0;
    public int scrollAmount = 0;

    public VeryFancyListWidget(List<VeryFancyButtonWidget> list, int x, int y, int width, int height, int screenHeight, int entryHeight) {
        super(x, y, width, height, Text.empty());
        this.buttons = list;
        this.screenHeight = screenHeight;
        this.entryHeight = entryHeight;
    }

    public VeryFancyListWidget(int x, int y, int width, int height, int screenHeight, int entryHeight) {
        super(x, y, width, height, Text.empty());
        this.screenHeight = screenHeight;
        this.entryHeight = entryHeight;
    }

    public void addButton(VeryFancyButtonWidget button) {
        this.buttons.add(button);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.enableScissor(this.getX(), this.getY() - 6, this.getX() + this.width, this.getY() + this.height + 6);
        this.buttons.forEach(button -> {
            button.render(context, mouseX, mouseY, delta);
        });
        context.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.buttons.forEach(fancyButton -> fancyButton.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int buttons = this.buttons.size();
        int scroll = (int) (verticalAmount * 10);
        int min = -buttons * (entryHeight) + this.screenHeight - 100 - entryHeight;
        int max = 0;

        if(this.scrollAmount + scroll <= min || this.scrollAmount + scroll > max) {
            scroll = 0;
        }
        this.scrollAmount = MathHelper.clamp(this.scrollAmount + scroll, min, max);

        for (int i = 0; i < buttons; i++) {
            VeryFancyButtonWidget roomButton = this.buttons.get(i);
            roomButton.setY(roomButton.getY() + scroll);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {

    }

    @Override
    public List<? extends Element> children() {
        return buttons;
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean dragging) {

    }


    @Nullable
    @Override
    public Element getFocused() {
        return null;
    }

    @Override
    public void setFocused(@Nullable Element focused) {

    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
