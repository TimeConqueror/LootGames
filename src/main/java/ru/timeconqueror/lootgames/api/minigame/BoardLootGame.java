package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.block.tile.TileBoardGameMaster;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.utils.MouseClickType;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;
import ru.timeconqueror.timecore.api.util.Requirements;

/**
 * Loot game that is flat.
 */
public abstract class BoardLootGame<T extends BoardLootGame<T>> extends LootGame<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static boolean disableMasterCheckWarning;

    @Override
    public void setMasterTileEntity(TileEntityGameMaster<T> masterTileEntity) {
        super.setMasterTileEntity(masterTileEntity);

        if (EnvironmentUtils.isInDev() && !disableMasterCheckWarning && !(masterTileEntity instanceof TileBoardGameMaster<?>)) {
            LOGGER.warn("You probably forgot that {} needs to extend {} to handle {} properly.", masterTileEntity.getClass().getSimpleName(), TileBoardGameMaster.class.getSimpleName(), BoardLootGame.class.getSimpleName());
        }
    }

    @Override
    public BlockPos getGameCenter() {
        int size = getCurrentBoardSize();
        return getBoardOrigin().offset(size / 2, 0, size / 2);
    }

    @Override
    protected BlockPos getRoomFloorPos() {
        return getMasterPos();
    }

    public abstract int getCurrentBoardSize();

    public abstract int getAllocatedBoardSize();

    @Override
    public void onDestroy() {
        LootGamesAPI.getBoardGenerator().clearBoard(((ServerWorld) getWorld()), getMasterPos(), getAllocatedBoardSize(), getAllocatedBoardSize());
    }

    public Pos2i convertToGamePos(BlockPos subordinatePos) {
        BlockPos result = subordinatePos.subtract(getBoardOrigin());
        return new Pos2i(result.getX(), result.getZ());
    }

    public BlockPos getBoardOrigin() {
        int offset = getAllocatedBoardSize() - getCurrentBoardSize();
        return getMasterPos().mutable().move(1, 0, 1).move(offset / 2, 0, offset / 2).immutable();
    }

    public void onClick(ServerPlayerEntity player, Pos2i pos, MouseClickType type) {

    }

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
