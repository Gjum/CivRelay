package gjum.minecraft.forge.gui;

public interface LayoutBoundingBox {
    LayoutConstraint getLayoutConstraint();

    Vec2 getCurrentSize();

    void setCoords(Vec2 topLeft);

    void setSize(Vec2 size);
}
