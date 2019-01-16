package gjum.minecraft.forge.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Contains several {@link LayoutBoundingBox} which are aligned next to each other horizontally or vertically.
 */
public class LayoutContainer implements LayoutBoundingBox {
    private final Vec2.Direction direction;
    private final List<LayoutBoundingBox> children = new ArrayList<>();

    private Vec2 currentSize;
    public Vec2 layoutWeight = new Vec2(0, 0);

    public LayoutContainer(Vec2.Direction direction) {
        this.direction = direction;
    }

    public LayoutContainer add(LayoutBoundingBox element) {
        children.add(element);
        return this;
    }

    public LayoutContainer addAll(Collection<LayoutBoundingBox> elements) {
        children.addAll(elements);
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
        while (distributable > 0 && distributable >= flex.size()) {
            final ArrayList<LayoutBoundingBox> currentFlex = new ArrayList<>(flex);
            flex.clear();

            final int currentWeights = totalWeights;
            totalWeights = 0;
            int currentDistributable = distributable;

            for (LayoutBoundingBox child : currentFlex) {
                final int oldSize = child.getCurrentSize().getDim(direction);
                final int max = child.getLayoutConstraint().getMaxSize().getDim(direction);
                final int weight = child.getLayoutConstraint().getWeight().getDim(direction);
                // since we use integer rounding here, the last few pixels need manual distribution, see below
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
            }

            if (distributable == currentDistributable) {
                // nothing changed - couldn't distribute the remaining space
                // this should never happen, as we should
                // either distribute at least one pixel per flex entry,
                // or (when we have less than flex.size() children left) the loop should exit
                break;
            }

            distributable = currentDistributable;
        }

        // distribute the last few pixels, this is < flex.size() and only occurs
        // because we use integer rounding while distributing
        for (LayoutBoundingBox child : flex) {
            if (distributable <= 0) break;
            final int oldSize = child.getCurrentSize().getDim(direction);
            child.setSize(Vec2.setDims(1 + oldSize, otherAvail, direction));
            distributable -= 1;
        }

        // practically the same as setting to availableSize,
        // unless there are no flex elements for a direction
        currentSize = Vec2.setDims(mainAvail - distributable, otherAvail, direction);
    }

    public LayoutContainer setWeight(Vec2 weight) {
        layoutWeight = weight;
        return this;
    }
}
