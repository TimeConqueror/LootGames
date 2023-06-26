//package ru.timeconqueror.lootgames.minigame.pipes;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
//import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
//import ru.timeconqueror.lootgames.api.util.Pos2i;
//import ru.timeconqueror.lootgames.minigame.pipes.board.PipesBoard;
//import ru.timeconqueror.lootgames.minigame.pipes.board.PipesBoardGenerator;
//import ru.timeconqueror.lootgames.registry.LGBlocks;
//import ru.timeconqueror.lootgames.utils.MouseClickType;
//import ru.timeconqueror.timecore.api.common.tile.SerializationType;
//
////FIXME secure nbt sync!!!
////TODO custom sounds of open
//
//
//public class GamePipes extends BoardLootGame<GamePipes> {
//    private PipesBoard board;
//
//    public GamePipes(int size) {
//        this.board = new PipesBoard(size);
//    }
//
//    public GamePipes() {
//        this(5);
//    }
//
//    @Override
//    protected BoardStage makeInitialStage() {
//        return new GameStage(0);
//    }
//
//    public int getCurrentBoardSize() {
//        return board.getSize();
//    }
//
//    @Override
//    public int getAllocatedBoardSize() {
//        return 15;
//    }
//
//    public PipesBoard getBoard() {
//        return board;
//    }
//
//    @Override
//    public void writeNBT(CompoundTag nbt, SerializationType type) {
//        super.writeNBT(nbt, type);
//
//        nbt.putInt("Size", getCurrentBoardSize());
//        nbt.put("Board", board.serializeNBT());
//    }
//
//    @Override
//    public void readNBT(CompoundTag nbt, SerializationType type) {
//        super.readNBT(nbt, type);
//
//        int size = nbt.getInt("Size");
//        if (board == null || size != board.getSize()) {
//            board = new PipesBoard(size);
//        }
//        board.deserializeNBT(nbt.getCompound("Board"));
//    }
//
//    private void checkForDirtyBoard() {
//        if (board.isDirty()) {
//            masterTileEntity.setChanged();
//            sendUpdatePacketToNearby(board.exportDirtyChunks());
//        }
//    }
//
//    private void sendBoard() {
//        sendUpdatePacketToNearby(board.exportBoard());
//    }
//
//    public void setBoardData(int size, int[] chunks) {
//        board = new PipesBoard(size, chunks);
//    }
//
//    @Override
//    public BoardStage createStageFromNBT(String id, CompoundTag stageNBT, SerializationType serializationType) {
//        switch (id) {
//            case GameStage.ID:
//                return new GameStage(stageNBT.getInt("CycleId"));
//            case WinningStage.ID:
//                return new WinningStage(stageNBT.getInt("PrevStageId"), stageNBT.getInt("TicksPassed"));
//            default:
//                throw new IllegalArgumentException("Unknown state with id: " + id + "!");
//        }
//    }
//
//    private class GameStage extends BoardStage {
//        public static final String ID = "game";
//
//        private final int cycleId;
//
//        public GameStage(int cycleId) {
//            this.cycleId = cycleId;
//        }
//
//        @Override
//        protected void onClick(Player player, Pos2i pos, MouseClickType type) {
//            if (isServerSide()) {
//                board.rotateAt(pos.getX(), pos.getY(), type == MouseClickType.LEFT ? -1 : 1);
//
//                if (board.isCompleted()) {
//                    switchStage(new WinningStage(cycleId, 0));
//                }
//
//                checkForDirtyBoard();
//            }
//        }
//
//        @Override
//        public void postInit() {
//            if (cycleId != 0) {
//                board = new PipesBoard(board.getSize() + 2);
//            }
//
//            PipesBoardGenerator generator = new PipesBoardGenerator(board);
//            generator.fillBoard(cycleId + 1, board.getSize() * 2);
//
//            if (cycleId != 0) {
//                sendBoard();
//            }
//        }
//
//        @Override
//        public CompoundTag serialize(SerializationType serializationType) {
//            CompoundTag nbt = super.serialize(serializationType);
//            nbt.putInt("CycleId", cycleId);
//            return nbt;
//        }
//
//        @Override
//        public String getID() {
//            return ID;
//        }
//    }
//
//    private class WinningStage extends BoardStage {
//        public static final String ID = "winning";
//
//        private final int prevCycleId;
//        private int ticksPassed;
//
//        public WinningStage(int prevCycleId, int ticksPassed) {
//            this.prevCycleId = prevCycleId;
//            this.ticksPassed = ticksPassed;
//        }
//
//        @Override
//        protected void onTick() {
//            if (isServerSide()) {
//                ticksPassed++;
//                if (ticksPassed >= 60) {
//                    switchStage(new GameStage(prevCycleId + 1));
//                }
//            }
//        }
//
//        @Override
//        public CompoundTag serialize(SerializationType serializationType) {
//            CompoundTag nbt = super.serialize(serializationType);
//            nbt.putInt("PrevCycleId", prevCycleId);
//            nbt.putInt("TicksPassed", ticksPassed);
//            return nbt;
//        }
//
//        @Override
//        public String getID() {
//            return ID;
//        }
//
//        @Override
//        public void postInit() {
//            getLevel().playSound(null, getGameCenter(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.75F, 1.0F);
//            board.removeNonPoweredPipes();
//        }
//    }
//
//    public static class Factory implements ILootGameFactory {
//        @Override
//        public void genOnPuzzleMasterClick(Level world, BlockPos puzzleMasterPos) {
//            BlockPos floorCenterPos = puzzleMasterPos.offset(0, -3/*instead of GameDungeonStructure.MASTER_BLOCK_OFFSET*/ + 1, 0);
//            world.setBlockAndUpdate(floorCenterPos, LGBlocks.PIPES_ACTIVATOR.defaultBlockState());
//        }
//    }
//}
