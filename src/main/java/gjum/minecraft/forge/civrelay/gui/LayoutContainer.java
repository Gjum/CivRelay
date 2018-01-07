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

            mainMax += Vec2.getDim(sizeMax, direction);
            otherMax = Vec2.max(otherMax, Vec2.getDim(sizeMax, direction.other()));

            mainMin += Vec2.getDim(sizeMin, direction);
            otherMin = Vec2.max(otherMin, Vec2.getDim(sizeMin, direction.other()));
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
        int main = Vec2.getDim(topLeft, direction);
        int other = Vec2.getDim(topLeft, direction.other());
        for (LayoutBoundingBox child : children) {
            child.setCoords(Vec2.setDims(main, other, direction));
            main += Vec2.getDim(child.getCurrentSize(), direction);
        }
    }

    @Override
    public void setSize(Vec2 availableSize) {
        final int mainAvail = Vec2.getDim(availableSize, direction);
        final int otherAvail = Vec2.getDim(availableSize, direction.other());

        final ArrayList<LayoutBoundingBox> flex = new ArrayList<>();
        int distributable = mainAvail;

        int totalWeights = 0;
        for (LayoutBoundingBox child : children) {
            final LayoutConstraint constraint = child.getLayoutConstraint();
            final int current = Vec2.getDim(constraint.getMinSize(), direction);

            child.setSize(Vec2.setDims(current, otherAvail, direction));
            distributable -= current;

            final int weight = Vec2.getDim(constraint.getWeight(), direction);
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
                final int oldSize = Vec2.getDim(child.getCurrentSize(), direction);
                final int max = Vec2.getDim(child.getLayoutConstraint().getMaxSize(), direction);
                final int weight = Vec2.getDim(child.getLayoutConstraint().getWeight(), direction);
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

    //    void ArrangeGrid(Container container) {
//        Insets insets = container.getInsets();
//        Component[] components = container.getComponents();
//        Rectangle rectangle = new Rectangle();
//        if (components.length == 0 && (this._columnWidths == null || this._columnWidths.length == 0) && (this._rowHeights == null || this._rowHeights.length == 0)) {
//            return;
//        }
//
//        GridBagLayoutInfo layoutInfo = this._getLayoutInfo(container, 2);
//        Dimension dimension = this._getMinSize(container, layoutInfo);
//        if (container.width < dimension.width || container.height < dimension.height) {
//            layoutInfo = this._getLayoutInfo(container, 1);
//            dimension = this._getMinSize(container, layoutInfo);
//        }
//
//        rectangle.width = dimension.width;
//        rectangle.height = dimension.height;
//        int remainingWidth = container.width - rectangle.width;
//        int i; // loops
//        double var12;
//        int var15;
//        if (remainingWidth != 0) {
//            var12 = 0.0D;
//
//            for (i = 0; i < layoutInfo.width; ++i) {
//                var12 += layoutInfo.weightX[i];
//            }
//
//            if (var12 > 0.0D) {
//                for (i = 0; i < layoutInfo.width; ++i) {
//                    var15 = (int) ((double) remainingWidth * layoutInfo.weightX[i] / var12);
//                    layoutInfo.minWidth[i] += var15;
//                    rectangle.width += var15;
//                    if (layoutInfo.minWidth[i] < 0) {
//                        rectangle.width -= layoutInfo.minWidth[i];
//                        layoutInfo.minWidth[i] = 0;
//                    }
//                }
//            }
//
//            remainingWidth = container.width - rectangle.width;
//        } else {
//            remainingWidth = 0;
//        }
//
//        int var11 = container.height - rectangle.height;
//        if (var11 != 0) {
//            var12 = 0.0D;
//
//            for (i = 0; i < layoutInfo.height; ++i) {
//                var12 += layoutInfo.weightY[i];
//            }
//
//            if (var12 > 0.0D) {
//                for (i = 0; i < layoutInfo.height; ++i) {
//                    var15 = (int) ((double) var11 * layoutInfo.weightY[i] / var12);
//                    layoutInfo.minHeight[i] += var15;
//                    rectangle.height += var15;
//                    if (layoutInfo.minHeight[i] < 0) {
//                        rectangle.height -= layoutInfo.minHeight[i];
//                        layoutInfo.minHeight[i] = 0;
//                    }
//                }
//            }
//
//            var11 = container.height - rectangle.height;
//        } else {
//            var11 = 0;
//        }
//
//        layoutInfo.startx = remainingWidth / 2 + insets.left;
//        layoutInfo.starty = var11 / 2 + insets.top;
//
//        for (Component component : components) {
//            if (component.isVisible()) {
//                GridBagConstraints var4 = this._lookupConstraints(component);
//                rectangle.x = layoutInfo.startx;
//
//                for (i = 0; i < var4.tempX; ++i) {
//                    rectangle.x += layoutInfo.minWidth[i];
//                }
//
//                rectangle.y = layoutInfo.starty;
//
//                for (i = 0; i < var4.tempY; ++i) {
//                    rectangle.y += layoutInfo.minHeight[i];
//                }
//
//                rectangle.width = 0;
//
//                for (i = var4.tempX; i < var4.tempX + var4.tempWidth; ++i) {
//                    rectangle.width += layoutInfo.minWidth[i];
//                }
//
//                rectangle.height = 0;
//
//                for (i = var4.tempY; i < var4.tempY + var4.tempHeight; ++i) {
//                    rectangle.height += layoutInfo.minHeight[i];
//                }
//
//                this._componentAdjusting = component;
//                this._adjustForGravity(var4, rectangle);
//                if (rectangle.x < 0) {
//                    rectangle.width += rectangle.x;
//                    rectangle.x = 0;
//                }
//
//                if (rectangle.y < 0) {
//                    rectangle.height += rectangle.y;
//                    rectangle.y = 0;
//                }
//
//                if (rectangle.width > 0 && rectangle.height > 0) {
//                    if (component.x != rectangle.x || component.y != rectangle.y || component.width != rectangle.width || component.height != rectangle.height) {
//                        component.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
//                    }
//                } else {
//                    component.setBounds(0, 0, 0, 0);
//                }
//            }
//        }
//    }

}
