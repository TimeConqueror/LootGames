package ru.timeconqueror.lootgames.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;
import ru.timeconqueror.timecore.api.util.LevelUtils;

import java.util.function.BiConsumer;

/**
 * Subordinate block for minigames. Will find master block and notify it. The master block must be at the north-west corner of the game border
 * and its tileentity must extend {@link GameMasterTile <>}!
 */
public class SmartSubordinateBlock extends GameBlock implements LeftInteractible, SubordinateProvider {

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!worldIn.isClientSide()) {
//            LootGamesAPI.getBoardManager().onFieldBlockBroken(worldIn, () -> getMasterPos(worldIn, pos));
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        forMasterTile(player, worldIn, pos, (master, blockPos) -> master.onBlockRightClick(player, pos));

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onLeftClick(Level world, Player player, BlockPos pos, Direction face) {
        if (face == Direction.UP && !player.isShiftKeyDown()) {
            forMasterTile(player, world, pos, (master, masterPos) -> master.onBlockLeftClick(player, pos));
            return true;
        }

        return false;
    }

    private void forMasterTile(Player player, Level world, BlockPos pos, BiConsumer<GameMasterTile<?>, BlockPos> action) {
        BlockPos masterPos = getMasterPos(world, pos);
        LevelUtils.forTypedTileWithWarn(player, world, masterPos, GameMasterTile.class, master -> action.accept(master, masterPos));
    }

    public static BlockPos getMasterPos(Level world, BlockPos pos) {
        BlockPos.MutableBlockPos currentPos = pos.mutable();
        int limit = 128;

        while (currentPos.equals(pos) || world.getBlockState(currentPos).getBlock() instanceof SubordinateProvider) {
            currentPos.move(-1, 0, 0);
            if (--limit == 0) break;
        }
        currentPos.move(1, 0, 0);

        while (currentPos.equals(pos) || world.getBlockState(currentPos).getBlock() instanceof SubordinateProvider) {
            currentPos.move(0, 0, -1);
            if (--limit == 0) break;
        }
        currentPos.move(0, 0, 1);

        // moving to corner, because master is there
        return currentPos.move(-1, 0, -1).immutable();
    }
}
