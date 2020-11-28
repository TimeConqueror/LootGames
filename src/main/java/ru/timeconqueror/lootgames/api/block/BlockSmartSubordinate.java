package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.utils.WorldUtils;
import ru.timeconqueror.timecore.util.NetworkUtils;

import java.util.function.BiConsumer;

/**
 * Subordinate block for minigames. Will find master block and notify it. The master block must be at the north-west corner of the game
 * and its tileentity must extend {@link TileEntityGameMaster<>}!
 */
public class BlockSmartSubordinate extends BlockGame implements ILeftInteractible {
    private static boolean underBreaking = false;

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!worldIn.isClientSide() && !underBreaking) {
            underBreaking = true;
            destroyStructure(worldIn, pos);
            underBreaking = false;
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    /**
     * Destroys the full structure, which this block belongs to.
     */
    private void destroyStructure(World worldIn, BlockPos pos) {
        BlockPos masterPos = getMasterPos(worldIn, pos);
        TileEntity te = worldIn.getBlockEntity(masterPos);

        if (te instanceof TileEntityGameMaster<?>) {
            ((TileEntityGameMaster<?>) te).destroyGameBlocks();
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            BlockPos masterPos = getMasterPos(worldIn, pos);
            TileEntity te = worldIn.getBlockEntity(masterPos);
            if (te instanceof TileEntityGameMaster<?>) {
                ((TileEntityGameMaster<?>) te).onBlockRightClicked(((ServerPlayerEntity) player), pos);
            } else {
                NetworkUtils.sendMessage(player, new StringTextComponent("The game doesn't seem to work. Please, send the issue to mod author."));
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean onLeftClick(World world, PlayerEntity player, BlockPos pos, Direction face) {
        if (BlockGameMaster.shouldHandleLeftClick(player, face)) {
            if (!world.isClientSide()) {
                forMasterTile(world, pos, (tileEntityGameMaster, masterPos) -> BlockGameMaster.handleLeftClick(player, world, masterPos, pos, face));
            }

            return true;
        }

        return false;
    }

    private void forMasterTile(World world, BlockPos pos, BiConsumer<TileEntityGameMaster<?>, BlockPos> action) {
        BlockPos masterPos = getMasterPos(world, pos);
        WorldUtils.forTypedTileWithWarn(world, masterPos, TileEntityGameMaster.class, tileEntityGameMaster -> action.accept(tileEntityGameMaster, masterPos));
    }

    public static BlockPos getMasterPos(World world, BlockPos pos) {
        BlockPos foundPos = pos;
        BlockState state;

        while (true) {
            foundPos = foundPos.west();

            state = world.getBlockState(foundPos);

            if (world.getBlockEntity(foundPos) instanceof TileEntityGameMaster<?>) {
                break;
            }

            if (!(state.getBlock() instanceof BlockSmartSubordinate)) {
                foundPos = foundPos.east();
                break;
            }
        }

        while (true) {
            foundPos = foundPos.north();

            state = world.getBlockState(foundPos);

            if (world.getBlockEntity(foundPos) instanceof TileEntityGameMaster<?>) {
                break;
            }

            if (!(state.getBlock() instanceof BlockSmartSubordinate)) {
                foundPos = foundPos.south();
                break;
            }
        }

        return foundPos;
    }
}
