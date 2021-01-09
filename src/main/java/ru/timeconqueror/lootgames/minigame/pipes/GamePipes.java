package ru.timeconqueror.lootgames.minigame.pipes;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.minigame.pipes.board.PipesBoard;
import ru.timeconqueror.lootgames.minigame.pipes.board.PipesBoardGenerator;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.utils.MouseClickType;

public class GamePipes extends BoardLootGame<GamePipes> {
    private PipesBoard board;

    public GamePipes(int size) {
        this.board = new PipesBoard(size);
    }

    public GamePipes() {
        this(5);
    }

    @Override
    public void onPlace() {
        if (isServerSide()) {
            setupInitialStage(new GameStage(0));
        }
    }

    public int getCurrentBoardSize() {
        return board.getSize();
    }

    @Override
    public int getAllocatedBoardSize() {
        return 15;
    }

    public PipesBoard getBoard() {
        return board;
    }

    @Override
    public void writeCommonNBT(CompoundNBT compound) {
        super.writeCommonNBT(compound);
        compound.putInt("Size", getCurrentBoardSize());
        compound.put("Board", board.serializeNBT());
    }

    @Override
    public void readCommonNBT(CompoundNBT compound) {
        super.readCommonNBT(compound);
        int size = compound.getInt("Size");
        if (board == null || size != board.getSize()) {
            board = new PipesBoard(size);
        }
        board.deserializeNBT(compound.getCompound("Board"));
    }

    @Override
    public void onClick(ServerPlayerEntity player, Pos2i pos, MouseClickType type) {
        if (stage instanceof GameStage) {
            board.rotateAt(pos.getX(), pos.getY(), type == MouseClickType.LEFT ? -1 : 1);

            if (board.isCompleted()) {
                switchStage(new WinningStage(((GameStage) stage).cycleId, 0));
            }

            checkForDirtyBoard();
        }
    }

    private void checkForDirtyBoard() {
        if (board.isDirty()) {
            masterTileEntity.setChanged();
            sendUpdatePacket(board.exportDirtyChunks());
        }
    }

    private void sendBoard() {
        sendUpdatePacket(board.exportBoard());
    }

    public void setBoardData(int size, int[] chunks) {
        board = new PipesBoard(size, chunks);
    }

    @Override
    public Stage<GamePipes> createStageFromNBT(String id, CompoundNBT stageNBT) {
        switch (id) {
            case GameStage.ID:
                return new GameStage(stageNBT.getInt("CycleId"));
            case WinningStage.ID:
                return new WinningStage(stageNBT.getInt("PrevStageId"), stageNBT.getInt("TicksPassed"));
            default:
                throw new IllegalArgumentException("Unknown state with id: " + id + "!");
        }
    }

    private class GameStage extends Stage<GamePipes> {
        public static final String ID = "game";

        private final int cycleId;

        public GameStage(int cycleId) {
            this.cycleId = cycleId;
        }

        @Override
        protected void onStart(LootGame<GamePipes> game) {
            if (game.isServerSide()) {
                if (cycleId != 0) {
                    board = new PipesBoard(board.getSize() + 2);
                    masterTileEntity.setChanged();
                }

                PipesBoardGenerator generator = new PipesBoardGenerator(board);
                generator.fillBoard(cycleId + 1, board.getSize() * 2);

                if (cycleId != 0) {
                    sendBoard();
                }
            }
        }

        @Override
        public CompoundNBT serialize() {
            CompoundNBT nbt = super.serialize();
            nbt.putInt("CycleId", cycleId);
            return nbt;
        }

        @Override
        public String getID() {
            return ID;
        }
    }

    private class WinningStage extends Stage<GamePipes> {
        public static final String ID = "winning";

        private final int prevCycleId;
        private int ticksPassed;

        public WinningStage(int prevCycleId, int ticksPassed) {
            this.prevCycleId = prevCycleId;
            this.ticksPassed = ticksPassed;
        }

        @Override
        protected void onStart(LootGame<GamePipes> game) {
            if (game.isServerSide()) {
                getWorld().playSound(null, getGameCenter(), SoundEvents.PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.75F, 1.0F);
                board.removeNonPoweredPipes();
            }
        }

        @Override
        protected void onTick(LootGame<GamePipes> game) {
            if (isServerSide()) {
                ticksPassed++;
                if (ticksPassed >= 60) {
                    switchStage(new GameStage(prevCycleId + 1));
                }
            }
        }

        @Override
        public CompoundNBT serialize() {
            CompoundNBT nbt = super.serialize();
            nbt.putInt("PrevCycleId", prevCycleId);
            nbt.putInt("TicksPassed", ticksPassed);
            return nbt;
        }

        @Override
        public String getID() {
            return ID;
        }
    }

    public static class Factory implements ILootGameFactory {
        @Override
        public void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos) {
            BlockPos floorCenterPos = puzzleMasterPos.offset(0, -3/*instead of GameDungeonStructure.MASTER_BLOCK_OFFSET*/ + 1, 0);
            world.setBlockAndUpdate(floorCenterPos, LGBlocks.PIPES_ACTIVATOR.defaultBlockState());
        }
    }
}
