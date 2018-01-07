package gjum.minecraft.forge.civrelay.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

import java.awt.*;

public abstract class ElementBase implements LayoutBoundingBox {
    private static int idCounter = 0;
    protected Vec2 currentSize;

    public static final Minecraft mc = Minecraft.getMinecraft();

    public final int id;

    protected LayoutConstraint layoutConstraint;

    public ElementBase(GuiBase gui) {
        id = nextId();
        gui.addVisible(this);
    }

    public static int getStringWidth(String s) {
        if (mc == null || mc.fontRenderer == null) {
            return s.length() * 3;
        }
        return mc.fontRenderer.getStringWidth(s);
    }

    public ElementBase setFixedSize(Vec2 size) {
        getLayoutConstraint().setFixedSize(size);
        return this;
    }

    public ElementBase setWeight(Vec2 weight) {
        getLayoutConstraint().setWeight(weight);
        return this;
    }

    @Override
    public LayoutConstraint getLayoutConstraint() {
        return layoutConstraint;
    }

    public abstract void draw(int mouseX, int mouseY, float partialTicks);

    public abstract ElementBase setColor(Color color);

    /**
     * Called by the containing {@link GuiBase}
     * when this is a button and it is clicked.
     */
    public void onButtonClicked() {
        // override if wrapping a button
    }

    /**
     * @return the wrapped {@link GuiButton} instance, or null if not wrapping a button
     */
    public GuiButton getButton() {
        return null;
    }

    /**
     * @return the wrapped {@link GuiTextField} instance, or null if not wrapping a text field
     */
    public GuiTextField getTextField() {
        return null;
    }

    public int getId() {
        return id;
    }

    @Override
    public Vec2 getCurrentSize() {
        return currentSize;
    }

    @Override
    public void setSize(Vec2 size) {
        currentSize = size;
    }

    private static int nextId() {
        return idCounter++;
    }
}
