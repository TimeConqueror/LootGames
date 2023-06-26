//package ru.timeconqueror.lootgames.minigame.pipes.board;
//
//import java.util.ArrayDeque;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Random;
//
//import static ru.timeconqueror.lootgames.minigame.pipes.board.PipesUtils.deltaX;
//import static ru.timeconqueror.lootgames.minigame.pipes.board.PipesUtils.deltaY;
//
//public class PipesBoardGenerator {
//
//    private final PipesBoard board;
//
//    public PipesBoardGenerator(PipesBoard board) {
//        this.board = board;
//    }
//
//    private class PathCell {
//
//        int x;
//        int y;
//        int distance;
//        int from;
//        int pos;
//
//        public PathCell(int x, int y, int distance, int from) {
//            this.x = x;
//            this.y = y;
//            this.pos = x + y * board.getSize();
//            this.distance = distance;
//            this.from = from;
//        }
//    }
//
//    public void fillBoard(int sinkCount, int pathLength) {
//        Random rnd = new Random();
//
//        int size = board.getSize();
//        int sx = size / 2;
//        int sy = size / 2;
//        int srot = rnd.nextInt(4);
//        board.setState(sx, sy, PipeState.byData(1, srot));
//
//        ArrayList<Integer> possible = new ArrayList<>(4);
//        ArrayDeque<PathCell> queue = new ArrayDeque<>();
//        queue.add(new PathCell(deltaX(sx, srot), deltaY(sy, srot), 1, (srot + 2) % 4));
//
//        int sinks = 0;
//        int realSinks = 0;
//
//        while (!queue.isEmpty()) {
//            PathCell cell = queue.removeFirst();
//
//            if (cell.distance < pathLength) {
//                main:
//                for (int i = 0; i < 4; i++) {
//                    if (i != cell.from) {
//                        int dx = deltaX(cell.x, i);
//                        int dy = deltaY(cell.y, i);
//                        if (dx >= 0 && dy >= 0 && dx < size && dy < size && board.getState(dx, dy).isEmpty()) {
//                            for (PathCell pc : queue) {
//                                if (pc.x == dx && pc.y == dy) {
//                                    continue main;
//                                }
//                            }
//                            possible.add(i);
//                        }
//                    }
//                }
//            }
//
//            if (possible.isEmpty()) {
//                board.setState(cell.x, cell.y, PipeState.byData(PipeState.Types.SINK, cell.from));
//                realSinks++;
//            } else {
//                boolean extraChild = rnd.nextInt(sinkCount) > sinks;
//                if (extraChild) {
//                    sinks++;
//                }
//
//                int children = Math.min(possible.size(), (extraChild ? 1 : 0) + (cell.distance < pathLength ? 1 : 0));
//                Collections.shuffle(possible);
//
//                main:
//                for (int type = PipeState.Types.DEAD_END; type <= PipeState.Types.CROSS; type++) {
//                    for (int rot = 0; rot < 4; rot++) {
//                        boolean allConnected = true;
//                        PipeState state = PipeState.byData(type, rot);
//
//                        if (state.hasConnectionAt(cell.from)) {
//                            for (int c = 0; c < children; c++) {
//                                if (!state.hasConnectionAt(possible.get(c))) {
//                                    allConnected = false;
//                                    break;
//                                }
//                            }
//
//                            if (allConnected) {
//                                board.setState(cell.x, cell.y, state);
//                                for (int c = 0; c < children; c++) {
//                                    int drot = possible.get(c);
//                                    int dx = deltaX(cell.x, drot);
//                                    int dy = deltaY(cell.y, drot);
//                                    queue.add(new PathCell(dx, dy, cell.distance + 1, (drot + 2) % 4));
//                                }
//                                break main;
//                            }
//                        }
//                    }
//                }
//
//                possible.clear();
//            }
//        }
//
//        // randomly rotates pipes tree
//        for (int x = 0; x < size; x++) {
//            for (int y = 0; y < size; y++) {
//                PipeState state = board.getState(x, y);
//                if (!state.isEmpty() && (x != sx || y != sy)) {
//                    board.setState(x, y, state.rotate(rnd.nextInt(4) - 2));
//                }
//            }
//        }
//
//        // randomly adds junk pipes
//        for (int x = 0; x < size; x++) {
//            for (int y = 0; y < size; y++) {
//                if (board.getState(x, y).isEmpty()) {
//                    int type = rnd.nextInt(PipeState.Types.CROSS);
//                    if (type != 0) {
//                        if (type > 3 && rnd.nextBoolean()) {
//                            type -= 2;
//                        }
//                        board.setState(x, y, PipeState.byData(type + 1, rnd.nextInt(4)));
//                    }
//                }
//            }
//        }
//
//        // randomly rotates mid source pipe (do not merge two rotates!)
//        board.rotateAt(sx, sy, 1);
//        board.rotateAt(sx, sy, rnd.nextInt(4) - 2);
//
//        board.removeDirty();
//        board.setSinkCount(realSinks);
//    }
//}
