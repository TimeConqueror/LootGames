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
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;
import ru.timeconqueror.timecore.api.util.WorldUtils;

import java.util.function.BiFunction;

public class GameMasterBlock extends GameBlock implements IGameField {
    private final BiFunction<BlockState, IBlockReader, GameMasterTile<?>> tileEntityFactory;

    public GameMasterBlock(BiFunction<BlockState, IBlockReader, GameMasterTile<?>> tileEntityFactory) {
        this.tileEntityFactory = tileEntityFactory;
    }

    public GameMasterBlock(Properties props, BiFunction<BlockState, IBlockReader, GameMasterTile<?>> tileEntityFactory) {
        super(props);
        this.tileEntityFactory = tileEntityFactory;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            WorldUtils.forTypedTileWithWarn(worldIn, pos, GameMasterTile.class, te -> te.onBlockRightClick(((ServerPlayerEntity) player), pos));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @NotNull
    public GameMasterTile<?> createTileEntity(BlockState state, IBlockReader world) {
        return tileEntityFactory.apply(state, world);
    }
}
