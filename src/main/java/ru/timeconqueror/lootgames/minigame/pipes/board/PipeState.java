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

    public static final PipeState EMPTY = byId(0);

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
        return pipeType == Types.SOURCE;
    }

    public boolean isSink() {
        return pipeType == Types.SINK || pipeType == Types.SINK_POWERED;
    }

    public boolean isPowered() {
        return !isEmpty() && (isSource() || isPoweredNotSource());
    }

    public boolean canBePowered() {
        return pipeType >= Types.DEAD_END && pipeType <= Types.SINK;
    }

    public boolean isPoweredNotSource() {
        return pipeType >= Types.DEAD_END_POWERED;
    }

    public boolean isEmpty() {
        return pipeType == Types.EMPTY;
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
                return pipeType == Types.FORK || pipeType == Types.CROSS
                        || pipeType == Types.FORK_POWERED || pipeType == Types.CROSS_POWERED;
            case 2:
                return pipeType == Types.LINE || pipeType == Types.CROSS
                        || pipeType == Types.LINE_POWERED || pipeType == Types.CROSS_POWERED;
            default:
                return pipeType == Types.TWIST || pipeType == Types.FORK || pipeType == Types.CROSS
                        || pipeType == Types.TWIST_POWERED || pipeType == Types.FORK_POWERED || pipeType == Types.CROSS_POWERED;
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

    public static class Types {
        public static final int EMPTY = 0;

        public static final int SOURCE = 1;

        public static final int DEAD_END = 2;
        public static final int LINE = 3;
        public static final int TWIST = 4;
        public static final int FORK = 5;
        public static final int CROSS = 6;
        public static final int SINK = 7;

        public static final int DEAD_END_POWERED = 8;
        public static final int LINE_POWERED = 9;
        public static final int TWIST_POWERED = 10;
        public static final int FORK_POWERED = 11;
        public static final int CROSS_POWERED = 12;
        public static final int SINK_POWERED = 13;
    }
}
