package ru.timeconqueror.lootgames.api.util;

public class Pos2i {
    private final int x;
    private final int y;

    public Pos2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pos2i add(Pos2i pos) {
        return add(pos.x, pos.y);
    }

    public Pos2i add(int x, int y) {
        return new Pos2i(this.x + x, this.y + y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int manhattanDistanceTo(Pos2i pos) {
        return Math.abs(x - pos.x) + Math.abs(y - pos.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }

        if (obj instanceof Pos2i) {
            Pos2i pos = ((Pos2i) obj);
            return this.x == pos.x && this.y == pos.y;
        }

        return false;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
