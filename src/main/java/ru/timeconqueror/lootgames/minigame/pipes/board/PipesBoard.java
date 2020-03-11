package ru.timeconqueror.lootgames.minigame.pipes.board;

public class PipesBoard {

    private static final int CHUNK_SIZE = 6;
    private static final int CELL_BITS = 5;
    private static final int CELL_MASK = 0b11111;

    private int size;
    private int[] field; // chunks with CHUNK_SIZE cells per chunk

    public PipesBoard(int size) {
        this.size = size;

        // number of chunks at field (CHUNK_SIZE cells per chunk)
        int chunksCount = size * size / CHUNK_SIZE;
        if (size * size % CHUNK_SIZE != 0) {
            chunksCount++; // if we need one more not full chunk
        }

        this.field = new int[chunksCount];
    }

    private int getStateId(int x, int y) {
        int i = y * size + x;

        int chunk = i / CHUNK_SIZE; // chunk id
        int cell = i % CHUNK_SIZE; // cell in chunk id

        // getting bits for cell from chunk
        return (field[chunk] >> (CELL_BITS * cell)) & CELL_MASK;
    }

    private int setStateId(int x, int y, int state) {
        int i = y * size + x;

        int chunk = i / CHUNK_SIZE; // chunk id
        int cell = i % CHUNK_SIZE; // cell in chunk id

        // getting chunk, removing bits for cell, setting bits for cell
        field[chunk] = field[chunk] & (~(CELL_MASK << (CELL_BITS * cell))) | (state << (CELL_BITS * cell));
        return chunk;
    }

    public PipeState getState(int x, int y) {
        return PipeState.byId(getStateId(x, y));
    }

    /**
     * @return id of modified chunk
     */
    public int setState(int x, int y, PipeState state) {
        return setStateId(x, y, state.getId());
    }

    /**
     * @return size of field by one dimension
     */
    public int getSize() {
        return size;
    }

    /**
     * @return number on internal (not world) chunks in field
     */
    public int getChunkCount() {
        return field.length;
    }
}
