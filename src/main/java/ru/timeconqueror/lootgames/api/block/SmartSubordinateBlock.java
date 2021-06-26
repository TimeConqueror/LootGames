package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;
import ru.timeconqueror.timecore.api.util.NetworkUtils;
import ru.timeconqueror.timecore.api.util.WorldUtils;

import java.util.function.BiConsumer;

/**
 * Subordinate block for minigames. Will find master block and notify it. The master block must be at the north-west corner of the game border
 * and its tileentity must extend {@link GameMasterTile <>}!
 */
public class SmartSubordinateBlock extends GameBlock implements ILeftInteractible, ISubordinateProvider {

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!worldIn.isClientSide()) {
            LootGamesAPI.getFieldManager().onFieldBlockBroken(worldIn, () -> getMasterPos(worldIn, pos));
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        BlockPos masterPos = getMasterPos(worldIn, pos);
        TileEntity te = worldIn.getBlockEntity(masterPos);
        if (te instanceof GameMasterTile<?>) {
            ((GameMasterTile<?>) te).onBlockRightClick(player, pos);
        } else {
            NetworkUtils.sendMessage(player, new StringTextComponent(String.format("The game doesn't seem to work on %s. Please, send the issue to mod author.", worldIn.isClientSide() ? "client" : "server")));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    @SuppressWarnings("CodeBlock2Expr")
    public boolean onLeftClick(World world, PlayerEntity player, BlockPos pos, Direction face) {
        if (face == Direction.UP && !player.isShiftKeyDown()) {
            forMasterTile(world, pos, (tileEntityGameMaster, masterPos) -> {
                WorldUtils.forTypedTileWithWarn(world, masterPos, GameMasterTile.class, master -> master.onBlockLeftClick(player, pos));
            });

            return true;
        }

        return false;
    }

    private void forMasterTile(World world, BlockPos pos, BiConsumer<GameMasterTile<?>, BlockPos> action) {
        BlockPos masterPos = getMasterPos(world, pos);
        WorldUtils.forTypedTileWithWarn(world, masterPos, GameMasterTile.class, tileEntityGameMaster -> action.accept(tileEntityGameMaster, masterPos));
    }

    public static BlockPos getMasterPos(World world, BlockPos pos) {
        BlockPos.Mutable currentPos = pos.mutable();
        int limit = 128;

        while (currentPos.equals(pos) || world.getBlockState(currentPos).getBlock() instanceof ISubordinateProvider) {
            currentPos.move(-1, 0, 0);
            if (--limit == 0) break;
        }
        currentPos.move(1, 0, 0);

        while (currentPos.equals(pos) || world.getBlockState(currentPos).getBlock() instanceof ISubordinateProvider) {
            currentPos.move(0, 0, -1);
            if (--limit == 0) break;
        }
        currentPos.move(0, 0, 1);

        // moving to corner, because master is there
        return currentPos.move(-1, 0, -1).immutable();
    }
}
