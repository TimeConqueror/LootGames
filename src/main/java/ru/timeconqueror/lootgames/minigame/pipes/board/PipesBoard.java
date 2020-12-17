package ru.timeconqueror.lootgames.minigame.pipes.board;

import net.minecraft.nbt.CompoundNBT;
import ru.timeconqueror.lootgames.common.packet.game.SPPipesGameChangeChunks;

import java.util.HashSet;
import java.util.Set;

import static ru.timeconqueror.lootgames.minigame.pipes.board.PipesUtils.deltaX;
import static ru.timeconqueror.lootgames.minigame.pipes.board.PipesUtils.deltaY;

public class PipesBoard {

    private static final int CHUNK_SIZE = 5;
    private static final int CELL_BITS = 6;
    private static final int CELL_MASK = 0b111111;

    private final int size;
    private final int[] chunks; // chunks with CHUNK_SIZE cells per chunk

    private final boolean[] dirtyChunks;
    private int dirtyChunksCount = 0;

    private int sinkCount;
    private int poweredSinkCount;

    public PipesBoard(int size) {
        this.size = size;

        // number of chunks at field (CHUNK_SIZE cells per chunk)
        int chunksCount = size * size / CHUNK_SIZE;
        if (size * size % CHUNK_SIZE != 0) {
            chunksCount++; // if we need one more not full chunk
        }

        this.chunks = new int[chunksCount];
        this.dirtyChunks = new boolean[chunksCount];
    }

    private int getStateId(int x, int y) {
        int i = y * size + x;

        int chunk = i / CHUNK_SIZE; // chunk id
        int cell = i % CHUNK_SIZE; // cell in chunk id

        // getting bits for cell from chunk
        return (chunks[chunk] >> (CELL_BITS * cell)) & CELL_MASK;
    }

    private int setStateId(int x, int y, int state) {
        PipeState oldState = PipeState.byId(getStateId(x, y));
        PipeState newState = PipeState.byId(state);
        boolean isOldStatePoweredSink = oldState.isSink() && oldState.isPowered();
        boolean isNewStatePoweredSink = newState.isSink() && newState.isPowered();

        if (!isOldStatePoweredSink && isNewStatePoweredSink) poweredSinkCount++;
        else if (isOldStatePoweredSink && !isNewStatePoweredSink) poweredSinkCount--;

        int i = y * size + x;

        int chunk = i / CHUNK_SIZE; // chunk id
        int cell = i % CHUNK_SIZE; // cell in chunk id

        // getting chunk, removing bits for cell, setting bits for cell
        int oldChunk = chunks[chunk];
        int newChunk = oldChunk & (~(CELL_MASK << (CELL_BITS * cell))) | (state << (CELL_BITS * cell));
        if (oldChunk != newChunk) {
            chunks[chunk] = newChunk;
            if (!dirtyChunks[chunk]) {
                dirtyChunksCount++;
                dirtyChunks[chunk] = true;
            }
        }

        return chunk;
    }

    public PipeState getState(int x, int y) {
        if (x >= 0 && x < size && y >= 0 && y < size) {
            return PipeState.byId(getStateId(x, y));
        } else {
            return PipeState.byId(0);
        }
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
        return chunks.length;
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putIntArray("Chunks", chunks);
        nbt.putInt("SinkCount", sinkCount);
        nbt.putInt("PoweredSinkCount", poweredSinkCount);
        return nbt;
    }

    public void deserializeNBT(CompoundNBT nbt) {
        int[] source = nbt.getIntArray("Chunks");
        System.arraycopy(source, 0, chunks, 0, source.length);
        sinkCount = nbt.getInt("SinkCount");
        poweredSinkCount = nbt.getInt("PoweredSinkCount");
    }

    public void setChunks(int[] ids, int[] chunks) {
        for (int i = 0; i < ids.length; i++) {
            this.chunks[ids[i]] = chunks[i];
        }
    }

    public void removeDirty() {
        dirtyChunksCount = 0;
        for (int i = 0; i < chunks.length; i++) {
            dirtyChunks[i] = false;
        }
    }

    public SPPipesGameChangeChunks exportDirtyChunks() {
        int[] ids = new int[dirtyChunksCount];
        int[] data = new int[dirtyChunksCount];

        for (int i = 0; i < chunks.length; i++) {
            if (dirtyChunks[i]) {
                dirtyChunksCount--;
                dirtyChunks[i] = false;
                ids[dirtyChunksCount] = i;
                data[dirtyChunksCount] = chunks[i];
            }
        }

        return new SPPipesGameChangeChunks(ids, data);
    }

    public boolean isDirty() {
        return dirtyChunksCount > 0;
    }

    public PipeState getState(int x, int y, int rotation) {
        return getState(deltaX(x, rotation), deltaY(y, rotation));
    }


    private void setUnpowered(int x, int y) {
        PipeState state = getState(x, y);
        if (state.isPowered()) {
            setState(x, y, state.toggle());
        }
    }

    private void setPowered(int x, int y) {
        PipeState state = getState(x, y);
        if (!state.isPowered()) {
            setState(x, y, state.toggle());
        }
    }

    private boolean hasPoweredNeighbor(PipeState state, int x, int y) {
        for (int i = 0; i < 4; i++) {
            if (state.hasConnectionAt(i)) {
                PipeState neighbor = getState(x, y, i);
                if (neighbor.isPowered() && neighbor.hasConnectionAt((i + 2) % 4)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void powerPipesTree(PipeState state, int x, int y, int from) {
        setState(x, y, state.toggle());
        for (int i = 0; i < 4; i++) {
            if (i != from && state.hasConnectionAt(i)) {
                int dx = deltaX(x, i);
                int dy = deltaY(y, i);
                PipeState neighbor = getState(dx, dy);
                if (neighbor.canBePowered() && neighbor.hasConnectionFrom(i)) {
                    powerPipesTree(neighbor, dx, dy, (i + 2) % 4);
                }
            }
        }
    }

    private boolean hasAccessToSource(PipeState state, int x, int y, int from, Set<Integer> used) {
        used.add(y * size + x);

        boolean result = state.isSource();
        for (int i = 0; i < 4; i++) {
            if (i != from && state.hasConnectionAt(i)) {
                int dx = deltaX(x, i);
                int dy = deltaY(y, i);
                if (!used.contains(dy * size + dx)) {
                    PipeState neighbor = getState(dx, dy);
                    if (neighbor.hasConnectionFrom(i)) {
                        result = result | hasAccessToSource(neighbor, dx, dy, (i + 2) % 4, used);
                    }
                }
            }
        }
        return result;
    }

    public void rotateAt(int x, int y, int delta) {
        PipeState state = getState(x, y);
        if (state.getPipeType() != 0) {
            PipeState rotated = state.rotate(delta);
            setState(x, y, rotated);

            if (rotated == state) {
                return;
            }

            if (!rotated.isPowered()) {
                if (hasPoweredNeighbor(rotated, x, y)) {
                    powerPipesTree(rotated, x, y, -1);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    int dx = deltaX(x, i);
                    int dy = deltaY(y, i);
                    PipeState neighbor = getState(dx, dy);
                    if (neighbor.hasConnectionFrom(i) && state.hasConnectionAt(i) && !rotated.hasConnectionAt(i)) {
                        Set<Integer> oldConnected = new HashSet<>();
                        if (!hasAccessToSource(neighbor, dx, dy, (i + 2) % 4, oldConnected)) {
                            for (int c : oldConnected) {
                                setUnpowered(c % size, c / size);
                            }
                        }
                    }
                }

                Set<Integer> connected = new HashSet<>();
                boolean powered = hasAccessToSource(rotated, x, y, -1, connected);
                for (int c : connected) {
                    if (powered) {
                        setPowered(c % size, c / size);
                    } else {
                        setUnpowered(c % size, c / size);
                    }
                }
            }
        }
    }

    public void removeNonPoweredPipes() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                PipeState state = getState(x, y);
                if (!state.isPowered() && !state.isEmpty()) {
                    setState(x, y, PipeState.EMPTY);
                }
            }
        }
    }

    public int getSinkCount() {
        return sinkCount;
    }

    public int getPoweredSinkCount() {
        return poweredSinkCount;
    }

    public void setSinkCount(int sinkCount) {
        this.sinkCount = sinkCount;
    }

    public boolean isCompleted() {
        return sinkCount != 0 && poweredSinkCount >= sinkCount;
    }
}
