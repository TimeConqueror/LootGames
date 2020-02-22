package ru.timeconqueror.lootgames.minigame.minesweeper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.intellij.lang.annotations.MagicConstant;
import ru.timeconqueror.lootgames.api.util.NBTUtils;
import ru.timeconqueror.lootgames.api.util.Pos2i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MSBoard {
    private MSField[][] board;
    private int size;
    private int bombCount;

    @SideOnly(Side.CLIENT)
    int cFlaggedFields;

    public MSBoard(int size, int bombCount) {
        this.size = size;
        this.bombCount = bombCount;
    }

    @SideOnly(Side.CLIENT)
    void updateFlaggedFields_c() {
        cFlaggedFields = 0;

        for (MSField[] msFields : board) {
            for (MSField msField : msFields) {
                if (msField.getMark() == MSField.FLAG) {
                    cFlaggedFields++;
                }
            }
        }
    }

    public boolean isGenerated() {
        return board != null;
    }

    void reveal(Pos2i pos) {
        getField(pos).reveal();
    }

    void reveal(int x, int y) {
        getField(x, y).reveal();
    }

    public boolean isHidden(Pos2i pos) {
        return getField(pos).isHidden();
    }

    public boolean isHidden(int x, int y) {
        return getField(x, y).isHidden();
    }

    @MSField.Type
    public int getType(Pos2i pos) {
        return getField(pos).getType();
    }

    @MSField.Type
    public int getType(int x, int y) {
        return getField(x, y).getType();
    }

    void swapMark(Pos2i pos) {
        getField(pos).swapMark();
    }

    @MSField.Mark
    public int getMark(Pos2i pos) {
        return getField(pos).getMark();
    }

    @MSField.Mark
    public int getMark(int x, int y) {
        return getField(x, y).getMark();
    }

    void resetBoard() {
        resetBoard(size, bombCount);
    }

    void resetBoard(int newBoardSize, int newBombCount) {
        size = newBoardSize;
        bombCount = newBombCount;

        board = null;
    }

    boolean isBomb(int x, int y) {
        return getType(x, y) == MSField.BOMB;
    }

    boolean isBomb(Pos2i pos) {
        return getType(pos) == MSField.BOMB;
    }

    public boolean hasFieldOn(Pos2i pos) {
        return pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < size && pos.getY() < size;
    }

    boolean checkWin() {
        boolean winState = true;

        loop:
        for (MSField[] msFields : board) {
            for (MSField msField : msFields) {
                if (msField.type == MSField.BOMB) {
                    if (!msField.isHidden || msField.getMark() != MSField.FLAG) {
                        winState = false;
                        break loop;
                    }
                } else {
                    if (msField.isHidden) {
                        winState = false;
                        break loop;
                    }
                }
            }
        }

        return winState;
    }

    private MSField getField(Pos2i pos) {
        return board[pos.getX()][pos.getY()];
    }

    private MSField getField(int x, int y) {
        return board[x][y];
    }

    public void generate(Pos2i startFieldPos) {
        if (convertToFieldIndex(startFieldPos) > (size * size) - 1) {
            throw new IllegalArgumentException(String.format("Start Pos must be strictly less than Board size. Current values: start pos = %1$s, boardSize = %2$d", startFieldPos, size));
        }

        board = new MSField[size][size];
        int square = size * size;

        //adding bombs
        ArrayList<Integer> fields = new ArrayList<>(square);
        for (int i = 0; i < square; i++) {
            if (i == convertToFieldIndex(startFieldPos)) {
                continue;
            }

            fields.add(i);
        }

        Collections.shuffle(fields);

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new MSField(-2, true, MSField.NO_MARK);
            }
        }

        for (Integer integer : fields.subList(0, bombCount)) {
            board[integer % size][integer / size].type = MSField.BOMB;
        }

        //adding numbers and spaces
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (!isBomb(i, j)) {
                    board[i][j].type = getConnectedBombCount(i, j);
                }
            }
        }

        getField(startFieldPos).reveal();
    }

    /**
     * Converts field index to pos.
     */
    private Pos2i convertToPos(int fieldIndex) {
        return new Pos2i(fieldIndex % size, fieldIndex / size);
    }

    /**
     * Converts pos to field index.
     * <p>Example:
     * <p>Pos {x = 2; y = 4}, gridSize = 5 -> index 22 out of (gridSize * gridSize)
     */
    private int convertToFieldIndex(Pos2i pos) {
        return pos.getY() * size + pos.getX();
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

    public void forEach(Consumer<Pos2i> func) {
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                func.accept(new Pos2i(x, y));
            }
        }
    }

    public void forEach(BiConsumer<Integer, Integer> func) {
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                func.accept(x, y);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    void setField(Pos2i pos, int type, boolean hidden, int mark) {
        MSField oldField = board[pos.getX()][pos.getY()];
        @MSField.Mark int oldMark = oldField.mark;

        if (!hidden) mark = MSField.NO_MARK;

        board[pos.getX()][pos.getY()] = new MSField(type, hidden, mark);


        if (oldMark == MSField.FLAG && mark != MSField.FLAG) {
            cFlaggedFields--;
        } else if (oldMark != MSField.FLAG && mark == MSField.FLAG) {
            cFlaggedFields++;
        }
    }

    void setBoard(MSField[][] board) {
        this.board = board;
    }

    public int size() {
        return size;
    }

    public int getBombCount() {
        return bombCount;
    }

    void setBombCount(int bombCount) {
        this.bombCount = bombCount;
    }

    @SideOnly(Side.CLIENT)
    public int getFlaggedField_c() {
        return cFlaggedFields;
    }

    void setSize(int size) {
        this.size = size;
    }

    NBTTagCompound writeNBTForSaving() {
        return NBTUtils.writeTwoDimArrToNBT(board);
    }

    NBTTagCompound writeNBTForClient() {
        return NBTUtils.<MSField>writeTwoDimArrToNBT(board, field -> {
            NBTTagCompound c = new NBTTagCompound();

            c.setBoolean("hidden", field.isHidden());
            c.setInteger("mark", field.getMark());

            if (!field.isHidden()) {
                c.setInteger("type", field.getType());
            }

            return c;
        });
    }

    public static class MSField implements INBTSerializable<NBTTagCompound> {
        public static final int BOMB = -1;
        public static final int EMPTY = 0;
        public static final int NO_MARK = 0;
        public static final int FLAG = 1;
        public static final int QUESTION_MARK = 2;
        /**
         * Type of cell.
         *
         * <p>   -1      - Bomb.
         * <p>   0       - Empty cell.
         * <p>   1 - 8   - Cell with number.
         */
        private int type;
        /**
         * 0 - not marked.
         * 1 - marked by flag.
         * 2 - marked by question mark.
         */
        private int mark;
        private boolean isHidden;

        MSField(int type, boolean isHidden, int mark) {
            this.type = type;
            this.isHidden = isHidden;
            this.mark = mark;
        }

        private void swapMark() {
            if (mark == 2) {
                mark = 0;
            } else {
                mark += 1;
            }
        }

        private void resetMark() {
            mark = NO_MARK;
        }

        private void reveal() {
            isHidden = false;
            resetMark();
        }

        /**
         * Returns the type of cell.
         *
         * <p>   -1      - Bomb.
         * <p>   0       - Empty cell.
         * <p>   1 - 9   - Cell with number.
         */
        @Type
        public int getType() {
            return type;
        }

        /**
         * Returns the mark of cell.
         *
         * <p>  -1 - not marked.
         * <p>  0 - marked by flag.
         * <p>  1 - marked by question mark.
         */
        @Mark
        public int getMark() {
            return mark;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound field = new NBTTagCompound();
            field.setInteger("mark", mark);
            field.setBoolean("hidden", isHidden);
            field.setInteger("type", type);

            return field;
        }

        public boolean isHidden() {
            return isHidden;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            isHidden = nbt.getBoolean("hidden");
            mark = nbt.getInteger("mark");
            type = nbt.getInteger("type");
        }

        @Override
        public String toString() {
            return "Field{type: " + type + ", hidden: " + isHidden + ", mark: " + mark + "}";
        }

        @MagicConstant(intValues = {BOMB, EMPTY, 1, 2, 3, 4, 5, 6, 7, 8})
        public @interface Type {
        }

        @MagicConstant(intValues = {NO_MARK, FLAG, QUESTION_MARK})
        public @interface Mark {
        }
    }
}
