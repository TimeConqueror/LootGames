package ru.timeconqueror.lootgames.minigame.pipes.board;

public class PipesUtils {

    public static int deltaX(int x, int rotation) {
        return x + (rotation == 1 ? 1 : (rotation == 3 ? -1 : 0));
    }

    public static int deltaY(int y, int rotation) {
        return y + (rotation == 2 ? 1 : (rotation == 0 ? -1 : 0));
    }
}
