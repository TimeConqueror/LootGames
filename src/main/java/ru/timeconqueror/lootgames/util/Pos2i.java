package ru.timeconqueror.lootgames.util;

public class Pos2i {
    private int x;
    private int y;

    public Pos2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void add(Pos2i pos) {
        this.x += pos.x;
        this.y += pos.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
        return "Pos: (" + x + ", " + y + ")";
    }
}
