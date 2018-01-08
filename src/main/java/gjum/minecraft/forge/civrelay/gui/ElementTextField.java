package gjum.minecraft.forge.civrelay.gui;

import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiTextField;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.function.Predicate;

public class ElementTextField extends ElementBase {
    private final GuiTextField textField;
    private final Predicate<String> validator;

    public ElementTextField(GuiBase gui, Predicate<String> validator, String text) {
        super(gui);
        this.validator = validator;

        textField = new GuiTextField(getId(), mc.fontRenderer, 0, 0, 0, 0);
        textField.setMaxStringLength(99999);
        textField.setGuiResponder(new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int i, boolean b) {
            }

            @Override
            public void setEntryValue(int i, float v) {

            }

            @Override
            public void setEntryValue(int i, @Nonnull String s) {
                onChanged();
            }
        });

        if (text != null) setText(text);
        textField.setCursorPositionZero();
        onChanged();

        layoutConstraint = new LayoutConstraint()
                .setMinSize(new Vec2(50, 20))
                .setMaxSize(new Vec2(99999, 20));
    }

    public String getText() {
        return textField.getText();
    }

    public ElementTextField setText(String text) {
        textField.setText(text);
        onChanged();
        return this;
    }

    private void onChanged() {
        if (validator == null) return;
        final boolean valid = validator.test(getText());
        if (valid) setColor(Color.WHITE);
        else setColor(Color.RED);
    }

    @Override
    public ElementBase setColor(Color color) {
        textField.setTextColor(color.getRGB());
        return this;
    }

    @Override
    public GuiTextField getTextField() {
        return textField;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        textField.drawTextBox();
    }

    @Override
    public void setCoords(Vec2 topLeft) {
        textField.x = topLeft.x;
        textField.y = topLeft.y;
    }

    @Override
    public void setSize(Vec2 size) {
        super.setSize(size);
        textField.width = size.x;
        textField.height = size.y;
    }
}
