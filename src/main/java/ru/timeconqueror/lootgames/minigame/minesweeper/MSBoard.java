package ru.timeconqueror.lootgames.minigame.minesweeper;

import ru.timeconqueror.lootgames.minigame.minesweeper.util.PosUtils;
import ru.timeconqueror.lootgames.util.Pos2i;

import java.util.ArrayList;
import java.util.Collections;

public class MSBoard {
    private int[][] board;

    public boolean isBomb(Pos2i pos) {
        return isBomb(pos.getX(), pos.getY());
    }

    public boolean isBomb(int x, int y) {
        return getCellTypeByPos(x, y) == GameMineSweeper.BOMB;
    }

    public boolean isEmpty(Pos2i pos) {
        return getCellTypeByPos(pos) == 0;
    }

    /**
     * Returns the type of cell that was got from the board by pos.
     *
     * <p>   -1      - Bomb.
     * <p>   0       - Empty cell.
     * <p>   1 - 9   - Cell with number.
     */
    public int getCellTypeByPos(Pos2i pos) {
        return getCellTypeByPos(pos.getX(), pos.getY());
    }

    /**
     * Returns the type of cell that was got from the board by pos.
     *
     * <p>   -1      - Bomb.
     * <p>   0       - Empty cell.
     * <p>   1 - 9   - Cell with number.
     */
    public int getCellTypeByPos(int x, int y) {
        return board[x][y];
    }

    public void generate(int boardSize, int bombCount, Pos2i startFieldPos) {
        if (bombCount > boardSize - 1) {
            throw new IllegalArgumentException(String.format("Bomb count must be strictly less than Board size. Current values: bomb count = %1$d, boardSize = %2$d", bombCount, boardSize));
        } else if (PosUtils.convertToFieldIndex(startFieldPos, boardSize) > boardSize - 1) {
            throw new IllegalArgumentException(String.format("Start Pos must be strictly less than Board size. Current values: start pos = %1$s, boardSize = %2$d", startFieldPos, boardSize));
        }

        board = new int[boardSize][boardSize];
        int square = boardSize * boardSize;

        //adding bombs
        ArrayList<Integer> fields = new ArrayList<>(square);
        for (int i = 0; i < square; i++) {
            if (i == PosUtils.convertToFieldIndex(startFieldPos, boardSize)) {
                continue;
            }

            fields.add(i);
        }

        Collections.shuffle(fields);

        for (Integer integer : fields.subList(0, bombCount)) {
            board[integer % boardSize][integer / boardSize] = GameMineSweeper.BOMB;
        }

        //adding numbers and spaces
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (!isBomb(i, j)) {
                    board[i][j] = getConnectedBombCount(i, j);
                }
            }
        }
    }

    /**
     * Returns count of bombs that were stood right next to given field.
     */
    private int getConnectedBombCount(Pos2i pos) {
        return getConnectedBombCount(pos.getX(), pos.getY());
    }

    /**
     * Returns count of bombs that were stood right next to given field.
     */
    private int getConnectedBombCount(int x, int y) {
        int bombCount = 0;
        for (int i = -1; i <= 1; i++) {

            int xCoord = x + i;
            if (xCoord >= 0 && xCoord < size()) {
                for (int j = -1; j <= 1; j++) {

                    int yCoord = y + j;
                    if (yCoord >= 0 && yCoord < size()) {
                        if (isBomb(xCoord, yCoord)) {
                            bombCount++;
                        }
                    }

                }
            }

        }

        return bombCount;
    }

    int[][] getAsArray() {
        return board;
    }

    void setBoard(int[][] board) {
        this.board = board;
    }

    public int size() {
        return board.length;
    }
}
