package gjum.minecraft.forge.civrelay.gui;

public class LayoutSpacer implements LayoutBoundingBox {
    private LayoutConstraint layoutConstraint;
    private Vec2 currentSize;

    public LayoutSpacer() {
        layoutConstraint = new LayoutConstraint()
                .setWeight(new Vec2(1, 1))
                .setMinSize(new Vec2(0, 0))
                .setMaxSize(new Vec2(99999, 99999));
    }

    public LayoutSpacer(Vec2 fixedSize) {
        layoutConstraint = new LayoutConstraint().setFixedSize(fixedSize);
    }

    @Override
    public LayoutConstraint getLayoutConstraint() {
        return layoutConstraint;
    }

    @Override
    public Vec2 getCurrentSize() {
        return currentSize;
    }

    @Override
    public void setCoords(Vec2 topLeft) {
        // we don't draw anything, so we just ignore the coords
    }

    @Override
    public void setSize(Vec2 size) {
        currentSize = size;
    }
}
