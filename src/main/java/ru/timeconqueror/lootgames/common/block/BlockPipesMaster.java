package ru.timeconqueror.lootgames.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.block.BlockGameMaster;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityPipesMaster;

import java.util.Objects;

public class BlockPipesMaster extends BlockGameMaster {

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            TileEntityGameMaster<?> te = (TileEntityGameMaster<?>) worldIn.getTileEntity(pos);

            Objects.requireNonNull(te);

            te.onSubordinateBlockClicked(((ServerPlayerEntity) player), pos);
        }

        return true;
    }

    @Override
    public @NotNull TileEntityGameMaster<?> createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityPipesMaster();
    }
}
