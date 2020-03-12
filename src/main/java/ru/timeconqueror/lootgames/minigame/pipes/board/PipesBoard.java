package ru.timeconqueror.lootgames.minigame.pipes.board;

import net.minecraft.nbt.CompoundNBT;
import ru.timeconqueror.lootgames.common.packet.game.PacketPipesGameChangeChunks;

import java.util.*;

public class PipesBoard {

    private static final int CHUNK_SIZE = 5;
    private static final int CELL_BITS = 6;
    private static final int CELL_MASK = 0b111111;

    private int size;
    private int[] chunks; // chunks with CHUNK_SIZE cells per chunk

    private boolean[] dirtyChunks;
    private int dirtyChunksCount = 0;

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

    private class PathCell {

        int x;
        int y;
        int distance;
        int from;
        int pos;

        public PathCell(int x, int y, int distance, int from) {
            this.x = x;
            this.y = y;
            this.pos = x + y * size;
            this.distance = distance;
            this.from = from;
        }
    }

    public int fillBoard(int sinkCount, int pathLength) {
        Random rnd = new Random();

        int sx = size / 2;
        int sy = size / 2;
        int srot = rnd.nextInt(4);
        setState(sx, sy, PipeState.byData(1, srot));

        ArrayList<Integer> possible = new ArrayList<>(4);
        ArrayDeque<PathCell> queue = new ArrayDeque<>();
        queue.add(new PathCell(deltaX(sx, srot), deltaY(sy, srot), 1, (srot + 2) % 4));

        int sinks = 0;
        int realSinks = 0;

        while (!queue.isEmpty()) {
            PathCell cell = queue.removeFirst();

            if (cell.distance < pathLength) {
                main:
                for (int i = 0; i < 4; i++) {
                    if (i != cell.from) {
                        int dx = deltaX(cell.x, i);
                        int dy = deltaY(cell.y, i);
                        if (dx >= 0 && dy >= 0 && dx < size && dy < size && getState(dx, dy).isEmpty()) {
                            for (PathCell pc : queue) {
                                if (pc.x == dx && pc.y == dy) {
                                    continue main;
                                }
                            }
                            possible.add(i);
                        }
                    }
                }
            }

            if (possible.isEmpty()) {
                setState(cell.x, cell.y, PipeState.byData(7, cell.from));
                realSinks++;
            } else {
                boolean extraChild = rnd.nextInt(sinkCount) > sinks;
                if (extraChild) {
                    sinks++;
                }

                int children = Math.min(possible.size(), (extraChild ? 1 : 0) + (cell.distance < pathLength ? 1 : 0));
                Collections.shuffle(possible);

                main:
                for (int type = 2; type <= 6; type++) {
                    for (int rot = 0; rot < 4; rot++) {
                        boolean allConnected = true;
                        PipeState state = PipeState.byData(type, rot);

                        if (state.hasConnectionAt(cell.from)) {
                            for (int c = 0; c < children; c++) {
                                if (!state.hasConnectionAt(possible.get(c))) {
                                    allConnected = false;
                                    break;
                                }
                            }

                            if (allConnected) {
                                setState(cell.x, cell.y, state);
                                for (int c = 0; c < children; c++) {
                                    int drot = possible.get(c);
                                    int dx = deltaX(cell.x, drot);
                                    int dy = deltaY(cell.y, drot);
                                    queue.add(new PathCell(dx, dy, cell.distance + 1, (drot + 2) % 4));
                                }
                                break main;
                            }
                        }
                    }
                }

                possible.clear();
            }
        }

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                PipeState state = getState(x, y);
                if (!state.isEmpty() && (x != sx || y != sy)) {
                    setState(x, y, state.rotate(rnd.nextInt(4) - 2));
                }
            }
        }

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (getState(x, y).isEmpty()) {
                    int type = rnd.nextInt(6);
                    if (type != 0) {
                        if (type > 3 && rnd.nextBoolean()) {
                            type -= 2;
                        }
                        setState(x, y, PipeState.byData(1 + type, rnd.nextInt(4)));
                    }
                }
            }
        }

        rotateAt(sx, sy, rnd.nextInt(4) - 2);

        removeDirty();
        return realSinks;
    }

    private int getStateId(int x, int y) {
        int i = y * size + x;

        int chunk = i / CHUNK_SIZE; // chunk id
        int cell = i % CHUNK_SIZE; // cell in chunk id

        // getting bits for cell from chunk
        return (chunks[chunk] >> (CELL_BITS * cell)) & CELL_MASK;
    }

    private int setStateId(int x, int y, int state) {
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
        return nbt;
    }

    public void deserializeNBT(CompoundNBT nbt) {
        int[] source = nbt.getIntArray("Chunks");
        System.arraycopy(source, 0, chunks, 0, source.length);
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

    public PacketPipesGameChangeChunks exportDirtyChunks() {
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

        return new PacketPipesGameChangeChunks(ids, data);
    }

    public boolean isDirty() {
        return dirtyChunksCount > 0;
    }

    public PipeState getState(int x, int y, int rotation) {
        return getState(deltaX(x, rotation), deltaY(y, rotation));
    }

    private int deltaX(int x, int rotation) {
        return x + (rotation == 1 ? 1 : (rotation == 3 ? -1 : 0));
    }

    private int deltaY(int y, int rotation) {
        return y + (rotation == 2 ? 1 : (rotation == 0 ? -1 : 0));
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
}
