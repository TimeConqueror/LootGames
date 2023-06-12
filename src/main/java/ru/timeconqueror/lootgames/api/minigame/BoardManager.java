package ru.timeconqueror.lootgames.api.minigame;

import com.google.common.collect.Iterables;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.block.BoardBorderBlock;
import ru.timeconqueror.lootgames.api.block.IGameField;
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.timecore.api.util.BlockPosUtils;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.api.util.LevelUtils;
import ru.timeconqueror.timecore.api.util.PlayerUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Can be accessed via {@link LootGamesAPI#getBoardManager()}.
 * <p>
 * Manager that can be used to handle generation and clearing flat board from {@link BoardLootGame}
 */
public class BoardManager {

    private boolean fieldUnderBreaking = false;

    /**
     * Destroys the full structure, which this block belongs to.
     */
    public void onFieldBlockBroken(Level worldIn, Supplier<BlockPos> masterPosGetter) {
        if (tryStartFieldBreaking()) {
            BlockPos masterPos = masterPosGetter.get();
            BlockEntity tileEntity = worldIn.getBlockEntity(masterPos);

            if (tileEntity instanceof GameMasterTile<?>) {
                ((GameMasterTile<?>) tileEntity).onDestroy();
            }
            stopFieldBreaking();
        }
    }

    public boolean tryStartFieldBreaking() {
        if (!fieldUnderBreaking) {
            fieldUnderBreaking = true;
            return true;
        }
        return false;
    }

    public void stopFieldBreaking() {
        fieldUnderBreaking = false;
    }

    public boolean canReplaceAreaWithBoard(Level world, BlockPos cornerPos, int xSize, int ySize, int zSize, @Nullable BlockPos except) {
        Iterable<BlockPos> positions = Iterables.concat(
                BlockPosUtils.between(cornerPos, xSize, 1, zSize), // board positions
                //second area is smaller because we don't need to check if the player can fit the place in the corner blocks above the border.
                BlockPosUtils.between(cornerPos.offset(1, 1, 1), xSize - 2, ySize - 1, zSize - 2) // positions above the board
        );

        return CollectionUtils.allMatch(positions, (pos) -> {
            BlockState state = world.getBlockState(pos);
            return state.getBlock() == LGBlocks.SHIELDED_DUNGEON_FLOOR || state.canBeReplaced() || pos.equals(except);
        });
    }

    /**
     * Tries to replace area with flat board.
     *
     * @param xSize  size of field without border
     * @param height height of area above field exclude floor block
     * @param zSize  size of field without border
     * @param player to notify about fail
     * @return true if field placed.
     */
    public GenerationChain trySetupBoard(ServerLevel world, BlockPos centerPos, int xSize, int height, int zSize, BlockState masterBlock, @Nullable Player player) {
        BlockPos cornerPos = centerPos.offset(-xSize / 2 - 1, 0, -zSize / 2 - 1);
        BlockPos.MutableBlockPos borderPos = cornerPos.mutable();
        if (!canReplaceAreaWithBoard(world, borderPos, xSize + 2, height + 1, zSize + 2, centerPos)) {
            if (player != null) {
                PlayerUtils.sendMessage(player, Component.translatable("msg.lootgames.field.not_enough_space", xSize + 2, height + 1, zSize + 2).withStyle(NotifyColor.FAIL.getColor()));
                world.playSound(null, centerPos, LGSounds.GAME_LOSE, SoundSource.BLOCKS, 0.4F, 1.0F);//TODO change sound
            }
            return new GenerationChain(null, null, false);
        }

        // Filling field with subordinate blocks
        for (BlockPos pos : BlockPosUtils.between(cornerPos.offset(1, 0, 1), xSize, 1, zSize)) {
            world.setBlock(pos, LGBlocks.SMART_SUBORDINATE.defaultBlockState(), 2);
        }

        // Filling area above field with air
        if (height > 0) {
            BlockPos abovePos = borderPos.offset(1, 1, 1);
            for (BlockPos pos : BlockPosUtils.between(abovePos, xSize, height, zSize)) {
                world.removeBlock(pos, false);
            }
        }

        // Filling border corners and master block
        world.setBlock(cornerPos, masterBlock, 3);
        borderPos.move(xSize + 1, 0, 0);
        world.setBlock(borderPos, LGBlocks.BOARD_BORDER.defaultBlockState().setValue(BoardBorderBlock.TYPE, BoardBorderBlock.Type.TOP_RIGHT), 2);
        borderPos.move(0, 0, zSize + 1);
        world.setBlock(borderPos, LGBlocks.BOARD_BORDER.defaultBlockState().setValue(BoardBorderBlock.TYPE, BoardBorderBlock.Type.BOTTOM_RIGHT), 2);
        borderPos.move(-xSize - 1, 0, 0);
        world.setBlock(borderPos, LGBlocks.BOARD_BORDER.defaultBlockState().setValue(BoardBorderBlock.TYPE, BoardBorderBlock.Type.BOTTOM_LEFT), 2);

        // Filling border edges
        borderPos.set(cornerPos);
        for (int i = 0; i < xSize; i++) {
            borderPos.move(1, 0, 0);
            world.setBlock(borderPos, LGBlocks.BOARD_BORDER.defaultBlockState().setValue(BoardBorderBlock.TYPE, BoardBorderBlock.Type.HORIZONTAL), 2);
            borderPos.move(0, 0, zSize + 1);
            world.setBlock(borderPos, LGBlocks.BOARD_BORDER.defaultBlockState().setValue(BoardBorderBlock.TYPE, BoardBorderBlock.Type.HORIZONTAL), 2);
            borderPos.move(0, 0, -zSize - 1);
        }
        borderPos.set(cornerPos);
        for (int i = 0; i < xSize; i++) {
            borderPos.move(0, 0, 1);
            world.setBlock(borderPos, LGBlocks.BOARD_BORDER.defaultBlockState().setValue(BoardBorderBlock.TYPE, BoardBorderBlock.Type.VERTICAL), 2);
            borderPos.move(xSize + 1, 0, 0);
            world.setBlock(borderPos, LGBlocks.BOARD_BORDER.defaultBlockState().setValue(BoardBorderBlock.TYPE, BoardBorderBlock.Type.VERTICAL), 2);
            borderPos.move(-xSize - 1, 0, 0);
        }

        return new GenerationChain(world, cornerPos, true);
    }

    public void clearBoard(ServerLevel world, BlockPos start, int sizeX, int sizeZ) {
        Iterable<BlockPos> gameBlocks = BlockPos.betweenClosed(start.offset(-1, 0, -1), start.offset(sizeX + 1, 0, sizeZ + 1));

        for (BlockPos pos : gameBlocks) {
            if (world.getBlockState(pos).getBlock() instanceof IGameField) {
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    public static class GenerationChain {
        private final Level world;
        private final BlockPos pos;
        private final boolean succeed;

        private GenerationChain(Level world, BlockPos masterPos, boolean succeed) {
            this.world = world;
            this.succeed = succeed;
            this.pos = masterPos;
        }

        public <T extends GameMasterTile<?>> GenerationChain forTileIfSucceed(Class<T> tileClass, Consumer<T> action) {
            if (succeed) {
                LevelUtils.forTileWithReqt(world, pos, tileClass, action);
            }

            return this;
        }

        public boolean isSucceed() {
            return succeed;
        }
    }
}
