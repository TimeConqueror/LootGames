package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.util.Requirements;

public abstract class BoardLootGame<T extends BoardLootGame<T>> extends LootGame<T> {
    @Override
    public BlockPos getGameCenter() {
        return masterTileEntity.getBlockPos().offset(getBoardSize() / 2, 0, getBoardSize() / 2);
    }

    @Override
    protected BlockPos getRoomFloorPos() {
        return getMasterPos();
    }

    public abstract int getBoardSize();

    /**
     * Generates flat game board.
     *
     * @param world       world in which structure will be placed.
     * @param masterPos   pos, where master block will be placed.
     *                    Board will be generated relatively to this pos, considering it as the origin
     * @param size        size of the board, should be greater than zero
     * @param masterBlock blockstate which will be placed on {@code masterPos}
     * @throws IllegalArgumentException if size is less or equal 0
     */
    public static void generateGameBoard(World world, BlockPos masterPos, int size, BlockState masterBlock) {
        Requirements.greaterThan(size, 0);

        BlockPos.betweenClosed(masterPos, masterPos.offset(size - 1, 0, size - 1))
                .forEach(pos -> {
                    if (pos.equals(masterPos)) {
                        world.setBlockAndUpdate(pos, masterBlock);
                    } else {
                        world.setBlockAndUpdate(pos, LGBlocks.SMART_SUBORDINATE.defaultBlockState());
                    }
                });
    }
}
