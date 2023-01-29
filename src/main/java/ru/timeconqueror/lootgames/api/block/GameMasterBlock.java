package ru.timeconqueror.lootgames.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;
import ru.timeconqueror.timecore.api.util.WorldUtils;

import java.util.function.BiFunction;

public class GameMasterBlock extends GameBlock implements IGameField {
    private final BiFunction<BlockState, BlockGetter, GameMasterTile<?>> tileEntityFactory;

    public GameMasterBlock(BiFunction<BlockState, BlockGetter, GameMasterTile<?>> tileEntityFactory) {
        this.tileEntityFactory = tileEntityFactory;
    }

    public GameMasterBlock(Properties props, BiFunction<BlockState, BlockGetter, GameMasterTile<?>> tileEntityFactory) {
        super(props);
        this.tileEntityFactory = tileEntityFactory;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        WorldUtils.forTypedTileWithWarn(worldIn, pos, GameMasterTile.class, te -> te.onBlockRightClick(player, pos));

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @NotNull
    public GameMasterTile<?> createTileEntity(BlockState state, BlockGetter world) {
        return tileEntityFactory.apply(state, world);
    }
}
