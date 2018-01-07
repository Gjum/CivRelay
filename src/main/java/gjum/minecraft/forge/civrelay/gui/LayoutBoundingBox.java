package gjum.minecraft.forge.civrelay.gui;

public interface LayoutBoundingBox {
    LayoutConstraint getLayoutConstraint();

    Vec2 getCurrentSize();

    void setCoords(Vec2 topLeft);

    void setSize(Vec2 size);
}
