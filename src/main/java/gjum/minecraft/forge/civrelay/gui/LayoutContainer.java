package gjum.minecraft.forge.civrelay.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains several {@link LayoutBoundingBox} which are aligned next to each other horizontally or vertically.
 */
public class LayoutContainer implements LayoutBoundingBox {
    private final Vec2.Direction direction;
    private final List<LayoutBoundingBox> children = new ArrayList<>();

    private Vec2 currentSize;
    public Vec2 layoutWeight = new Vec2(0,0);

    public LayoutContainer(Vec2.Direction direction) {
        this.direction = direction;
    }

    public LayoutContainer add(LayoutBoundingBox e) {
        children.add(e);
        return this;
    }

    @Override
    public LayoutConstraint getLayoutConstraint() {
        int mainMax = 0;
        int otherMax = 0;
        int mainMin = 0;
        int otherMin = 0;

        for (LayoutBoundingBox child : children) {
            final Vec2 sizeMax = child.getLayoutConstraint().getMaxSize();
            final Vec2 sizeMin = child.getLayoutConstraint().getMinSize();

            mainMax += sizeMax.getDim(direction);
            otherMax = Vec2.max(otherMax, sizeMax.getDim(direction.other()));

            mainMin += sizeMin.getDim(direction);
            otherMin = Vec2.max(otherMin, sizeMin.getDim(direction.other()));
        }

        final Vec2 maxSize = Vec2.setDims(mainMax, otherMax, direction);
        final Vec2 minSize = Vec2.setDims(mainMin, otherMin, direction);

        return new LayoutConstraint()
                .setWeight(layoutWeight)
                .setMaxSize(maxSize)
                .setMinSize(minSize);
    }

    @Override
    public Vec2 getCurrentSize() {
        return currentSize;
    }

    @Override
    public void setCoords(Vec2 topLeft) {
        int main = topLeft.getDim(direction);
        int other = topLeft.getDim(direction.other());
        for (LayoutBoundingBox child : children) {
            child.setCoords(Vec2.setDims(main, other, direction));
            main += child.getCurrentSize().getDim(direction);
        }
    }

    @Override
    public void setSize(Vec2 availableSize) {
        final int mainAvail = availableSize.getDim(direction);
        final int otherAvail = availableSize.getDim(direction.other());

        final ArrayList<LayoutBoundingBox> flex = new ArrayList<>();
        int distributable = mainAvail;

        int totalWeights = 0;
        for (LayoutBoundingBox child : children) {
            final LayoutConstraint constraint = child.getLayoutConstraint();
            final int current = constraint.getMinSize().getDim(direction);

            child.setSize(Vec2.setDims(current, otherAvail, direction));
            distributable -= current;

            final int weight = constraint.getWeight().getDim(direction);
            if (weight > 0) {
                // flexible, assign later
                totalWeights += weight;
                flex.add(child);
            }
        }

        // always terminates when either...
        // - all space is distributed or
        // - all flex elements are maxed out or
        // - space cannot be distributed anymore (TODO integer division)
        while (0 < distributable && distributable >= flex.size()) {
            final ArrayList<LayoutBoundingBox> currentFlex = new ArrayList<>(flex);
            flex.clear();

            final int currentWeights = totalWeights;
            totalWeights = 0;
            int currentDistributable = distributable;
//
//            System.out.println(String.format("Distributing %s among %s with total weight of %s", currentDistributable, currentFlex.size(), currentWeights));

            for (LayoutBoundingBox child : currentFlex) {
                final int oldSize = child.getCurrentSize().getDim(direction);
                final int max = child.getLayoutConstraint().getMaxSize().getDim(direction);
                final int weight = child.getLayoutConstraint().getWeight().getDim(direction);
                int newSize = oldSize + distributable * weight / currentWeights;
                if (newSize < max) {
                    // still flexible, assign later
                    totalWeights += weight;
                    flex.add(child);
                } else {
                    newSize = max;
                }

                child.setSize(Vec2.setDims(newSize, otherAvail, direction));
                currentDistributable -= newSize - oldSize;

//                System.out.println(String.format("Resizing %s from %s to %s",
//                        child.getClass().getName(), oldSize, newSize));
            }

            if (distributable == currentDistributable) {
                // can't distribute the remaining space
                break;
            }

            distributable = currentDistributable;
        }

        currentSize = Vec2.setDims(mainAvail - distributable, otherAvail, direction);
    }

    public LayoutContainer setWeight(Vec2 weight) {
        layoutWeight = weight;
        return this;
    }
}
