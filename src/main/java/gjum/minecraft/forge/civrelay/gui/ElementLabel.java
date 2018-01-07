package gjum.minecraft.forge.civrelay.gui;

import java.awt.*;

public class ElementLabel extends ElementBase {
    public enum Alignment {ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT}

    private String text; // on change, update minSize/maxSize/idealSize too ... this would benefit much from a rewrite of the layout engine
    public Alignment alignment;
    public Vec2 coords;

    private int color = Color.WHITE.getRGB();

    public ElementLabel(GuiBase gui, String text, Alignment alignment) {
        super(gui);
        this.text = text;
        this.alignment = alignment;

        layoutConstraint = new LayoutConstraint().setFixedSize(
                new Vec2(getStringWidth(text) + 6, 20));
    }

    @Override
    public ElementBase setColor(Color color) {
        this.color = color.getRGB();
        return this;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        final int x;
        switch (alignment) {
            case ALIGN_CENTER:
                int w = getStringWidth(text);
                x = coords.x + (currentSize.x - w) / 2;
                break;
            case ALIGN_LEFT:
                x = coords.x;
                break;
            case ALIGN_RIGHT:
                int w2 = getStringWidth(text);
                x = coords.x + (currentSize.x - w2);
                break;
            default:
                throw new IllegalStateException("Unexpected alignment " + alignment);
        }
        final int dy = (currentSize.y - mc.fontRenderer.FONT_HEIGHT) / 2; // TODO configure vertical alignment
        mc.fontRenderer.drawStringWithShadow(text, x, coords.y + dy, color);
    }

    @Override
    public void setCoords(Vec2 topLeft) {
        coords = topLeft;
    }
}
