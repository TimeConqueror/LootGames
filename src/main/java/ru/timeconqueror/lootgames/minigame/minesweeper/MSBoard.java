package ru.timeconqueror.lootgames.minigame.minesweeper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.minigame.minesweeper.util.PosUtils;

import java.util.ArrayList;
import java.util.Collections;

public class MSBoard {
    private MSField[][] board;
    private int size;
    private int bombCount;

    public MSBoard(int size, int bombCount) {
        this.size = size;
        this.bombCount = bombCount;
    }

    public boolean isGenerated() {
        return board != null;
    }

    public boolean isBomb(Pos2i pos) {
        return isBomb(pos.getX(), pos.getY());
    }

    public boolean isBomb(int x, int y) {
        return getFieldTypeByPos(x, y) == MSField.BOMB;
    }

    public boolean isEmpty(Pos2i pos) {
        return getFieldTypeByPos(pos) == 0;
    }

    public boolean hasFieldOn(Pos2i pos) {
        return pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < size && pos.getY() < size;
    }

    /**
     * Returns the type of cell that was got from the board by pos.
     *
     * <p>   -1      - Bomb.
     * <p>   0       - Empty cell.
     * <p>   1 - 9   - Cell with number.
     */
    public int getFieldTypeByPos(Pos2i pos) {
        return getFieldTypeByPos(pos.getX(), pos.getY());
    }

    MSField getFieldByPos(Pos2i pos) {
        return board[pos.getX()][pos.getY()];
    }

    /**
     * Returns the type of cell that was got from the board by pos.
     *
     * <p>   -1      - Bomb.
     * <p>   0       - Empty cell.
     * <p>   1 - 9   - Cell with number.
     */
    public int getFieldTypeByPos(int x, int y) {
        return board[x][y].type;
    }

    public int getFieldMarkByPos(int x, int y) {
        return board[x][y].mark;
    }

    public void generate(Pos2i startFieldPos) {
        if (PosUtils.convertToFieldIndex(startFieldPos, size) > (size * size) - 1) {
            throw new IllegalArgumentException(String.format("Start Pos must be strictly less than Board size. Current values: start pos = %1$s, boardSize = %2$d", startFieldPos, size));
        }

        board = new MSField[size][size];
        int square = size * size;

        //adding bombs
        ArrayList<Integer> fields = new ArrayList<>(square);
        for (int i = 0; i < square; i++) {
            if (i == PosUtils.convertToFieldIndex(startFieldPos, size)) {
                continue;
            }

            fields.add(i);
        }

        Collections.shuffle(fields);

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new MSField(-2, true, -1);
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

        getFieldByPos(startFieldPos).reveal();
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

    public MSField[][] asArray() {
        return board;
    }

    @SideOnly(Side.CLIENT)
    void setField(Pos2i pos, int type, boolean hidden, int mark) {
        board[pos.getX()][pos.getY()] = new MSField(type, hidden, mark);
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

    void setSize(int size) {
        this.size = size;
    }

    static class MSField implements INBTSerializable<NBTTagCompound> {
        /**
         * Bomb index
         */
        public static final int BOMB = -1;

        private int type;
        private int mark;
        private boolean isHidden;
        /**
         * -1 - not marked.
         * 0 - marked by flag.
         * 1 - marked by question mark.
         */
        private int markType;

        public MSField(int type, boolean isHidden, int mark) {
            this.type = type;
            this.isHidden = isHidden;
            this.mark = mark;
        }

        void reveal() {
            isHidden = false;
        }

        void swapMark() {
            if (mark == 1) {
                mark = -1;
            } else {
                mark += 1;
            }
        }

        void resetMark() {
            mark = -1;
        }

        /**
         * Returns the type of cell.
         *
         * <p>   -1      - Bomb.
         * <p>   0       - Empty cell.
         * <p>   1 - 9   - Cell with number.
         */
        public int getType() {
            return type;
        }

        public boolean isHidden() {
            return isHidden;
        }

        /**
         * Returns the mark of cell.
         *
         * <p>  -1 - not marked.
         * <p>  0 - marked by flag.
         * <p>  1 - marked by question mark.
         */
        public int getMark() {
            return mark;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound field = new NBTTagCompound();
            field.setBoolean("hidden", isHidden);
            field.setInteger("mark", mark);
            field.setInteger("type", type);

            return field;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            isHidden = nbt.getBoolean("hidden");
            mark = nbt.getInteger("mark");
            type = nbt.getInteger("type");
        }

        @Override
        public String toString() {
            return "Field{type: " + type + ", hidden:" + isHidden + ", mark:" + mark + "}";
        }
    }
}
