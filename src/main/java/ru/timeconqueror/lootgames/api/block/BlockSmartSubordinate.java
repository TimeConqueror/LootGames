package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;

/**
 * Subordinate block for minigames. Will find master block and notify it. The master block must be at the north-west corner of the game
 * and its tileentity must extend {@link TileEntityGameMaster<>}!
 */
public class BlockSmartSubordinate extends BlockGame {
    private static boolean underBreaking = false;

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!worldIn.isRemote && !underBreaking) {
            underBreaking = true;
            destroyStructure(worldIn, pos);
            underBreaking = false;
        }

        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    /**
     * Destroys the full structure, which this block belongs to.
     */
    private void destroyStructure(World worldIn, BlockPos pos) {
        BlockPos masterPos = getMasterPos(worldIn, pos);
        TileEntity te = worldIn.getTileEntity(masterPos);

        if (te instanceof TileEntityGameMaster<?>) {
            ((TileEntityGameMaster<?>) te).destroyGameBlocks();
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            BlockPos masterPos = getMasterPos(worldIn, pos);
            TileEntity te = worldIn.getTileEntity(masterPos);
            if (te instanceof TileEntityGameMaster<?>) {
                ((TileEntityGameMaster<?>) te).onSubordinateBlockClicked(pos, player);
            } else {
                player.sendMessage(new StringTextComponent("The game doesn't seem to work. Please, send the issue to mod author."));
            }
        }

        return true;
    }

    public BlockPos getMasterPos(World world, BlockPos pos) {
        BlockPos foundPos = pos;
        BlockState state;

        while (true) {
            foundPos = foundPos.west();

            state = world.getBlockState(foundPos);

            if (world.getTileEntity(foundPos) instanceof TileEntityGameMaster<?>) {
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

            if (world.getTileEntity(foundPos) instanceof TileEntityGameMaster<?>) {
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
