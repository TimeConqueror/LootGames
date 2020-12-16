package ru.timeconqueror.lootgames.minigame.pipes;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.minigame.pipes.board.PipesBoard;
import ru.timeconqueror.lootgames.registry.LGBlocks;

public class GamePipes extends LootGame<GamePipes> {

    private PipesBoard board;
    private final float difficulty;

    public GamePipes(int size, float difficulty) {
        this.board = new PipesBoard(size);
        this.difficulty = difficulty;

        this.board.fillBoard(5, size * 2);
    }

    @Override
    protected BlockPos getCentralRoomPos() {
        return masterTileEntity.getBlockPos().offset(getBoardSize() / 2, 0, getBoardSize() / 2);
    }

    @Override
    protected BlockPos getRoomFloorPos() {
        return getMasterPos();
    }

    @Override
    public Stage<GamePipes> createStageFromNBT(String id, CompoundNBT stageNBT) {
        return null;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public int getBoardSize() {
        return board.getSize();
    }

    public PipesBoard getBoard() {
        return board;
    }

    @Override
    public void writeCommonNBT(CompoundNBT compound) {
        super.writeCommonNBT(compound);
        compound.putInt("Size", getBoardSize());
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

    public void clickField(ServerPlayerEntity player, Pos2i pos) {
        board.rotateAt(pos.getX(), pos.getY(), player.isShiftKeyDown() ? -1 : 1);
        if (board.isDirty()) {
            masterTileEntity.setChanged();
            sendUpdatePacket(board.exportDirtyChunks());
        }
    }

    public static class Factory implements ILootGameFactory {
        @Override
        public void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos) {
            BlockPos floorCenterPos = puzzleMasterPos.offset(0, -3/*instead of GameDungeonStructure.MASTER_BLOCK_OFFSET*/ + 1, 0);
            world.setBlockAndUpdate(floorCenterPos, LGBlocks.PIPES_ACTIVATOR.defaultBlockState());
        }
    }

    public static void generateGameStructure(World world, BlockPos centerPos, int level) {
        int size = 19;
        BlockPos startPos = centerPos.offset(-size / 2, 0, -size / 2);

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                BlockPos pos = startPos.offset(x, 0, z);
                if (x == 0 && z == 0) {
                    world.setBlockAndUpdate(pos, LGBlocks.PIPES_MASTER.defaultBlockState());
                } else {
                    world.setBlockAndUpdate(pos, LGBlocks.SMART_SUBORDINATE.defaultBlockState());
                }
            }
        }
    }
}
