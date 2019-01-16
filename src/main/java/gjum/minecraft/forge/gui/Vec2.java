package gjum.minecraft.forge.gui;

import static gjum.minecraft.forge.gui.Vec2.Direction.ROW;

public class Vec2 {
    public final int x;
    public final int y;

    public Vec2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec2 vec2 = (Vec2) o;

        if (x != vec2.x) return false;
        return y == vec2.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    public int getDim(Direction direction) {
        if (direction == ROW) {
            return x;
        } else {
            return y;
        }
    }

    public static Vec2 setDims(int main, int other, Direction direction) {
        if (direction == ROW) {
            return new Vec2(main, other);
        } else {
            return new Vec2(other, main);
        }
    }

    @Override
    public String toString() {
        return "Vec2{" + x + ", " + y + '}';
    }

    public enum Direction {
        ROW, COLUMN;

        public Direction other() {
            if (this == ROW) {
                return COLUMN;
            } else {
                return ROW;
            }
        }
    }
}
