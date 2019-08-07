package ru.timeconqueror.lootgames.minigame.minesweeper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.minigame.AbstractLootGame;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.world.gen.DungeonGenerator;

public class GameMineSweeper extends AbstractLootGame {
    /**
     * Bomb index
     */
    public static final int BOMB = -1;

    private MSBoard board;

    public GameMineSweeper() {
        board = new MSBoard();
    }

    public void generateBoard(BlockPos clickedBlockPos) {

    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound gameCompound = new NBTTagCompound();

        NBTTagCompound boardTag = new NBTTagCompound();
        int[][] boardAsArray = board.getAsArray();
        for (int i = 0; i < boardAsArray.length; i++) {
            boardTag.setIntArray(Integer.toString(i), boardAsArray[i]);
        }
        boardTag.setInteger("size", boardAsArray.length);

        gameCompound.setTag("board", boardTag);

        return gameCompound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagCompound boardTag = nbt.getCompoundTag("board");
        int size = boardTag.getInteger("size");

        int[][] boardAsArray = new int[size][size];
        for (int i = 0; i < size; i++) {
            int[] boardLine = nbt.getIntArray(Integer.toString(i));
            boardAsArray[i] = boardLine;
        }

        board.setBoard(boardAsArray);
    }

    public static class Factory implements ILootGameFactory {
        @Override
        public void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos, BlockPos bottomPos, BlockPos topPos) {
            BlockPos floorCenterPos = puzzleMasterPos.add(0, -DungeonGenerator.PUZZLEROOM_MASTER_TE_OFFSET + 1, 0);
            world.setBlockState(floorCenterPos, ModBlocks.MS_FIELD.getDefaultState());//TODO Change to master
        }
    }
}

