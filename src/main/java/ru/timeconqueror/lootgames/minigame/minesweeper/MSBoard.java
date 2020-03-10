package ru.timeconqueror.lootgames.minigame.minesweeper;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import org.intellij.lang.annotations.MagicConstant;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.util.NBTUtils;
import ru.timeconqueror.lootgames.api.util.Pos2i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MSBoard {
    int cFlaggedFields;
    private MSField[][] board;
    private int size;
    private int bombCount;

    public MSBoard(int size, int bombCount) {
        this.size = size;
        this.bombCount = bombCount;
    }

    void updateFlaggedFields_c() {
        cFlaggedFields = 0;

        for (MSField[] msFields : board) {
            for (MSField msField : msFields) {
                if (msField.mark == Mark.FLAG) {
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
        return getField(pos).isHidden;
    }

    public boolean isHidden(int x, int y) {
        return getField(x, y).isHidden;
    }

    @MSField.Type
    public int getType(Pos2i pos) {
        return getField(pos).type;
    }

    @MSField.Type
    public int getType(int x, int y) {
        return getField(x, y).type;
    }

    void swapMark(Pos2i pos) {
        getField(pos).swapMark();
    }

    public Mark getMark(Pos2i pos) {
        return getField(pos).mark;
    }

    public Mark getMark(int x, int y) {
        return getField(x, y).mark;
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
                    if (!msField.isHidden || msField.mark != Mark.FLAG) {
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
                board[i][j] = new MSField(-2, true, Mark.NO_MARK);
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

    public void cSetField(Pos2i pos, int type, Mark mark, boolean hidden) {
        MSField oldField = board[pos.getX()][pos.getY()];
        Mark oldMark = oldField.mark;

        if (!hidden) mark = Mark.NO_MARK;

        board[pos.getX()][pos.getY()] = new MSField(type, hidden, mark);


        if (oldMark == Mark.FLAG && mark != Mark.FLAG) {
            cFlaggedFields--;
        } else if (oldMark != Mark.FLAG && mark == Mark.FLAG) {
            cFlaggedFields++;
        }
    }

    private void setBoard(MSField[][] board) {
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

    public int cGetFlaggedField() {
        return cFlaggedFields;
    }

    public void cSetFlaggedFields(int cFlaggedFields) {
        this.cFlaggedFields = cFlaggedFields;
    }

    void setSize(int size) {
        this.size = size;
    }

    CompoundNBT writeNBTForSaving() {
        return NBTUtils.writeTwoDimArrToNBT(board);
    }

    CompoundNBT writeNBTForClient() {
        return NBTUtils.<MSField>writeTwoDimArrToNBT(board, field -> {
            CompoundNBT c = new CompoundNBT();

            c.putBoolean("hidden", field.isHidden());
            c.putInt("mark", field.getMark().id);

            if (!field.isHidden()) {
                c.putInt("type", field.getType());
            }

            return c;
        });
    }

    void readNBTFromClient(CompoundNBT boardTag) {
        MSField[][] boardArr = NBTUtils.readTwoDimArrFromNBT(boardTag, MSField.class, compoundIn ->
                new MSField(compoundIn.contains("type") ? compoundIn.getInt("type") : MSField.EMPTY, compoundIn.getBoolean("hidden"), Mark.byID(compoundIn.getInt("mark"))));
        setBoard(boardArr);
        updateFlaggedFields_c();
    }

    void readNBTFromSave(CompoundNBT boardTag) {
        MSField[][] boardArr = NBTUtils.readTwoDimArrFromNBT(boardTag, MSField.class, () -> new MSField(MSField.EMPTY, true, Mark.NO_MARK));
        setBoard(boardArr);
    }

    public enum Mark {
        NO_MARK(0),
        FLAG(1),
        QUESTION_MARK(2);

        private static final Mark[] LOOKUP;

        static {
            LOOKUP = new Mark[values().length];
            for (Mark value : values()) {
                LOOKUP[value.id] = value;
            }
        }

        private int id;

        Mark(int id) {
            this.id = id;
        }

        public static Mark byID(int id) {
            if (id >= LOOKUP.length) {
                LootGames.LOGGER.error("Provided unknown id: {}, switched to NO_MARK (0).", id);
            }
            return LOOKUP[id];
        }

        public static Mark getNext(Mark mark) {
            return mark.id >= LOOKUP.length ? LOOKUP[0] : LOOKUP[++mark.id];
        }

        public Mark getNext() {
            return getNext(this);
        }

        public int getID() {
            return id;
        }
    }

    public static class MSField implements INBTSerializable<CompoundNBT> {
        public static final int BOMB = -1;
        public static final int EMPTY = 0;
        /**
         * Type of cell.
         *
         * <p>   -1      - Bomb.
         * <p>   0       - Empty cell.
         * <p>   1 - 8   - Cell with number.
         */
        private int type;

        private Mark mark;
        private boolean isHidden;

        MSField(int type, boolean isHidden, Mark mark) {
            this.type = type;
            this.isHidden = isHidden;
            this.mark = mark;
        }

        private void swapMark() {
            mark = mark.getNext();
        }

        private void resetMark() {
            mark = Mark.NO_MARK;
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

        public Mark getMark() {
            return mark;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT field = new CompoundNBT();
            field.putInt("mark", mark.id);
            field.putBoolean("hidden", isHidden);
            field.putInt("type", type);

            return field;
        }

        public boolean isHidden() {
            return isHidden;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            isHidden = nbt.getBoolean("hidden");
            mark = Mark.byID(nbt.getInt("mark"));
            type = nbt.getInt("type");
        }

        @Override
        public String toString() {
            return "Field{type: " + type + ", hidden: " + isHidden + ", mark: " + mark + "}";
        }

        @MagicConstant(intValues = {BOMB, EMPTY, 1, 2, 3, 4, 5, 6, 7, 8})
        public @interface Type {
        }
    }
}
