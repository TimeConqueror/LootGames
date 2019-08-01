import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MSGrid {
    /**
     * Bomb index
     */
    public static final int BOMB = -1;

    private int[][] grid;

    public boolean isBomb(Pos2i pos) {
        return isBomb(pos.getX(), pos.getY());
    }

    public boolean isBomb(int x, int y) {
        return getCellTypeByPos(x, y) == BOMB;
    }

    public boolean isEmpty(Pos2i pos) {
        return getCellTypeByPos(pos) == 0;
    }

    /**
     * Returns the type of cell that was got from the grid by pos.
     *
     * <p>   -1      - Bomb.
     * <p>   0       - Empty cell.
     * <p>   1 - 9   - Cell with number.
     */
    public int getCellTypeByPos(Pos2i pos) {
        return getCellTypeByPos(pos.getX(), pos.getY());
    }

    /**
     * Returns the type of cell that was got from the grid by pos.
     *
     * <p>   -1      - Bomb.
     * <p>   0       - Empty cell.
     * <p>   1 - 9   - Cell with number.
     */
    public int getCellTypeByPos(int x, int y) {
        return grid[x][y];
    }

    public void generateGrid(int gridSize, int bombCount) {
        grid = new int[gridSize][gridSize];
        int square = gridSize * gridSize;

        //adding bombs
        ArrayList<Integer> fields = new ArrayList<>(square);
        for (int i = 0; i < square; i++) {
            fields.add(i);
        }

        Collections.shuffle(fields);

        for (Integer integer : fields.subList(0, bombCount)) {
            grid[integer % gridSize][integer / gridSize] = BOMB;
        }

        //adding numbers and spaces
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (!isBomb(i, j)) {
                    grid[i][j] = getConnectedBombsCount(i, j);
                }
            }
        }

        for (int i = 0; i < grid.length; i++) {
            System.out.println(Arrays.toString(grid[i]));
        }
    }

    /**
     * Returns count of bombs that were stood right next to given field.
     */
    private int getConnectedBombsCount(Pos2i pos) {
        return getConnectedBombsCount(pos.getX(), pos.getY());
    }

    /**
     * Returns count of bombs that were stood right next to given field.
     */
    private int getConnectedBombsCount(int x, int y) {
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

    public int size() {
        return grid.length;
    }
}
