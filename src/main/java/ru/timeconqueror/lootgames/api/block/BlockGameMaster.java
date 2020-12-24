package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.timecore.util.WorldUtils;

import java.util.function.BiFunction;

public class BlockGameMaster extends BlockGame implements IGameField {
    private final BiFunction<BlockState, IBlockReader, TileEntityGameMaster<?>> tileEntityFactory;

    public BlockGameMaster(BiFunction<BlockState, IBlockReader, TileEntityGameMaster<?>> tileEntityFactory) {
        this.tileEntityFactory = tileEntityFactory;
    }

    public BlockGameMaster(Properties props, BiFunction<BlockState, IBlockReader, TileEntityGameMaster<?>> tileEntityFactory) {
        super(props);
        this.tileEntityFactory = tileEntityFactory;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            WorldUtils.forTypedTileWithWarn(worldIn, pos, TileEntityGameMaster.class, te -> te.onBlockRightClick(((ServerPlayerEntity) player), pos));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @NotNull
    public TileEntityGameMaster<?> createTileEntity(BlockState state, IBlockReader world) {
        return tileEntityFactory.apply(state, world);
    }
}
