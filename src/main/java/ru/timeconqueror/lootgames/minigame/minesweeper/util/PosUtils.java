package ru.timeconqueror.lootgames.minigame.minesweeper.util;

import ru.timeconqueror.lootgames.api.util.Pos2i;

public class PosUtils {
    /**
     * Converts pos to field index.
     * <p>Example:
     * <p>Pos {x = 2; y = 4}, gridSize = 5 -> index 22 out of (gridSize * gridSize)
     */
    public static int convertToFieldIndex(Pos2i pos, int gridSize) {
        return pos.getY() * gridSize + pos.getX();
    }

    /**
     * Converts field index to pos.
     */
    public static Pos2i convertToPos(int fieldIndex, int gridSize) {
        return new Pos2i(fieldIndex % gridSize, fieldIndex / gridSize);
    }
}
