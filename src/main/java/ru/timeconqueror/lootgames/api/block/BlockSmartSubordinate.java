package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.tileentity.TileEntityGameMaster;

/**
 * Subordinate block for minigames. Will find master block and notify it. The master block must be at the north-west corner of the game
 * and its tileentity must extend {@link TileEntityGameMaster<>}!
 */
public class BlockSmartSubordinate extends BlockGame {
    @Override
    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state) {
        if (!destroyStructure(worldIn, pos)) {
            super.onPlayerDestroy(worldIn, pos, state);
        }
    }

    @Override
    public void onBlockExploded(World world, @NotNull BlockPos pos, @NotNull Explosion explosion) {
        if (!destroyStructure(world, pos)) {
            super.onBlockExploded(world, pos, explosion);
        }
    }

    /**
     * Destroys the full structure, which this block belongs to.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean destroyStructure(World worldIn, BlockPos pos) {
        if (!worldIn.isRemote) {
            BlockPos masterPos = getMasterPos(worldIn, pos);
            TileEntity te = worldIn.getTileEntity(masterPos);
            if (te instanceof TileEntityGameMaster<?>) {
                ((TileEntityGameMaster<?>) te).destroyGameBlocks();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            BlockPos masterPos = getMasterPos(worldIn, pos);
            TileEntity te = worldIn.getTileEntity(masterPos);
            if (te instanceof TileEntityGameMaster<?>) {
                ((TileEntityGameMaster<?>) te).onSubordinateBlockClicked(pos, playerIn);
            } else {
                playerIn.sendMessage(new TextComponentString("The game doesn't seem to work. Please, send the issue to mod author."));
            }
        }

        return true;
    }

    public BlockPos getMasterPos(World world, BlockPos pos) {
        BlockPos foundPos = pos;
        IBlockState state;

        if (world.getBlockState(pos) instanceof BlockSmartSubordinate) {
            return pos;
        }

        while (true) {
            foundPos = foundPos.add(-1, 0, 0);

            state = world.getBlockState(foundPos);

            if (world.getTileEntity(foundPos) instanceof TileEntityGameMaster<?>) {
                break;
            }

            if (!(state.getBlock() instanceof BlockSmartSubordinate)) {
                foundPos = foundPos.add(1, 0, 0);
                break;
            }
        }

        while (true) {
            foundPos = foundPos.add(0, 0, -1);

            state = world.getBlockState(foundPos);

            if (world.getTileEntity(foundPos) instanceof TileEntityGameMaster<?>) {
                break;
            }

            if (!(state.getBlock() instanceof BlockSmartSubordinate)) {
                foundPos = foundPos.add(0, 0, 1);
                break;
            }
        }

        return foundPos;
    }
}
