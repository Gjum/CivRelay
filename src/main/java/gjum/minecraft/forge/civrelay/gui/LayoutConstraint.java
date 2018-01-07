package gjum.minecraft.forge.civrelay.gui;

public class LayoutConstraint {
    private boolean dirty = true;
    private Vec2 minSize;
    private Vec2 maxSize;
    private Vec2 weight = new Vec2(1, 1);

    public Vec2 getMaxSize() {
        if (dirty) check();
        return maxSize;
    }

    public Vec2 getMinSize() {
        if (dirty) check();
        return minSize;
    }

    public Vec2 getWeight() {
        return weight;
    }

    public LayoutConstraint setFixedSize(Vec2 size) {
        maxSize = minSize = size;
        weight = new Vec2(0, 0);
        return this;
    }

    public LayoutConstraint setMaxSize(Vec2 size) {
        dirty = true;
        maxSize = size;
        return this;
    }

    public LayoutConstraint setMinSize(Vec2 size) {
        dirty = true;
        minSize = size;
        return this;
    }

    public LayoutConstraint setWeight(Vec2 weight) {
        this.weight = weight;
        return this;
    }

    public LayoutConstraint check() {
        if (minSize == null) minSize = new Vec2(200, 20);
        if (maxSize == null) maxSize = minSize;

        if (maxSize.x < minSize.x) maxSize = new Vec2(minSize.x, maxSize.y);
        if (maxSize.y < minSize.y) maxSize = new Vec2(maxSize.x, minSize.y);

        dirty = false;

        return this;
    }
}
