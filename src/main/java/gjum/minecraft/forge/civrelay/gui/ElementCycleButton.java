package gjum.minecraft.forge.civrelay.gui;

import net.minecraft.client.gui.GuiButton;

import java.awt.*;
import java.util.function.Consumer;

public class ElementCycleButton extends ElementBase {
    private final GuiButton button;
    private final Consumer<String> onChange;
    private String[] options;
    private int selectionIndex;

    public ElementCycleButton(GuiBase gui, Consumer<String> onChange, String currentOption, String... options) {
        super(gui);
        button = new GuiButton(getId(), 0, 0, currentOption);

        this.onChange = onChange;
        this.options = options;

        int longestOption = 0;
        for (int i = options.length - 1; i >= 0; i--) {
            String value = options[i];

            final int width = getStringWidth(value);
            if (longestOption < width) longestOption = width;

            if (currentOption.equals(value)) {
                selectionIndex = i;
            }
        }

        layoutConstraint = new LayoutConstraint().setFixedSize(
                new Vec2(longestOption + 6, 20));
    }

    public ElementCycleButton setOptions(String... options) {
        this.options = options;
        return this;
    }

    @Override
    public ElementBase setColor(Color color) {
        button.packedFGColour = color.getRGB();
        return this;
    }

    @Override
    public void onButtonClicked() {
        selectionIndex++;
        selectionIndex %= options.length;
        button.displayString = options[selectionIndex];
        onChange.accept(options[selectionIndex]);
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
