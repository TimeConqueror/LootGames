package ru.timeconqueror.lootgames.minigame.pipes.board;

public class PipeState {

    // all registered state : 8 types with 4 rotations
    private static PipeState[] states = new PipeState[32];

    static {
        for (int type = 0; type < 8; type++) {
            for (int rotation = 0; rotation < 4; rotation++) {
                PipeState state = new PipeState(type, rotation);
                states[state.id] = state;
            }
        }
    }

    public static PipeState byId(int id) {
        return states[id];
    }

    private int pipeType;
    private int rotation;

    private int id;

    public PipeState(int pipeType, int rotation) {
        this.pipeType = pipeType;
        this.rotation = rotation;
        this.id = pipeType * 4 + rotation;
    }

    /**
     * @return id of pipe type without rotation from 0 to 8 (excluded)
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

    public PipeState rotate(int delta) {
        int rotatedId = pipeType * 4 + (rotation + delta) % 4;
        return byId(rotatedId);
    }

    public int getId() {
        return id;
    }
}
