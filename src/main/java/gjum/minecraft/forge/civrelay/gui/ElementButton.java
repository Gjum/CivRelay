package gjum.minecraft.forge.civrelay.gui;

import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public class ElementButton extends ElementBase {
    private final GuiButton button;
    private final Runnable clickHandler;

    public ElementButton(GuiBase gui, Runnable clickHandler, String text) {
        super(gui);
        button = new GuiButton(getId(), 0, 0, text);
        this.clickHandler = clickHandler;

        layoutConstraint = new LayoutConstraint().setFixedSize(
                new Vec2(getStringWidth(text) + 6, 20));
    }

    public ElementBase setText(String text) {
        button.displayString = text;
        // TODO instead of replacing, add this as an additional constraint
        layoutConstraint = new LayoutConstraint().setMinSize(
                new Vec2(getStringWidth(text) + 6, 20));
        return this;
    }

    @Override
    public ElementBase setColor(Color color) {
        button.packedFGColour = color.getRGB();
        return this;
    }

    @Override
    public void onButtonClicked() {
        clickHandler.run();
    }

    @Override
    public GuiButton getButton() {
        return button;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        button.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public void setCoords(Vec2 topLeft) {
        button.x = topLeft.x;
        button.y = topLeft.y;
    }

    @Override
    public void setSize(Vec2 size) {
        super.setSize(size);
        button.width = size.x;
        button.height = size.y;
    }
}
