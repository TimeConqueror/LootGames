package ru.timeconqueror.lootgames.minigame.pipes.board;

public class PipeState {

    // all registered state : 14 types with 4 rotations
    private static PipeState[] states = new PipeState[56];

    static {
        for (int type = 0; type < 14; type++) {
            for (int rotation = 0; rotation < 4; rotation++) {
                PipeState state = new PipeState(type, rotation);
                states[state.id] = state;
            }
        }
    }

    public static PipeState byId(int id) {
        return states[id];
    }

    public static PipeState byData(int type, int rotation) {
        return byId(type * 4 + rotation);
    }

    private final int pipeType;
    private final int rotation;

    private final int id;

    public PipeState(int pipeType, int rotation) {
        this.pipeType = pipeType;
        this.rotation = rotation;
        this.id = pipeType * 4 + rotation;
    }

    /**
     * @return id of pipe type without rotation from 0 to 14 (excluded)
     */
    public int getPipeType() {
        return pipeType;
    }

    /**
     * @return angle of rotation from 0 to 4 (excluded)
     */
    public int getRotation() {
        return rotation;
    }

    public boolean isSource() {
        return pipeType == 1;
    }

    public boolean isSink() {
        return pipeType == 7 || pipeType == 13;
    }

    public boolean isPowered() {
        return !isEmpty() && (isSource() || isPoweredNotSource());
    }

    public boolean canBePowered() {
        return pipeType > 1 && pipeType < 8;
    }

    public boolean isPoweredNotSource() {
        return pipeType > 7;
    }

    public boolean isEmpty() {
        return pipeType == 0;
    }

    public PipeState rotate(int delta) {
        return byData(pipeType, (rotation + 4 + delta) % 4);
    }

    public boolean hasConnectionFrom(int rotation) {
        return hasConnectionAt((rotation + 2) % 4);
    }

    public boolean hasConnectionAt(int rotation) {
        int rot = (8 + rotation - this.rotation) % 4;
        switch (rot) {
            case 0:
                return !isEmpty();
            case 1:
                return pipeType == 5 || pipeType == 6 || pipeType == 11 || pipeType == 12;
            case 2:
                return pipeType == 3 || pipeType == 6 || pipeType == 9 || pipeType == 12;
            default:
                return pipeType == 4 || pipeType == 5 || pipeType == 6 || pipeType == 10 || pipeType == 11 || pipeType == 12;
        }
    }

    public PipeState toggle() {
        if (pipeType > 7) {
            return byData(pipeType - 6, rotation);
        } else if (pipeType > 1) {
            return byData(pipeType + 6, rotation);
        }
        return this;
    }

    public int getId() {
        return id;
    }
}
