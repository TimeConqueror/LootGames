package ru.timeconqueror.lootgames.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;
import ru.timeconqueror.timecore.api.util.WorldUtils;
import ru.timeconqueror.timecorex.api.util.ITickableBlockEntity;

import java.util.function.Supplier;

public class GameMasterBlock extends GameBlock implements IGameField, EntityBlock {
    private final Supplier<BlockEntityType<? extends GameMasterTile<?>>> blockEntityType;

    public GameMasterBlock(Supplier<BlockEntityType<? extends GameMasterTile<?>>> blockEntityType) {
        this.blockEntityType = blockEntityType;
    }

    public GameMasterBlock(Properties props, Supplier<BlockEntityType<? extends GameMasterTile<?>>> blockEntityType) {
        super(props);
        this.blockEntityType = blockEntityType;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        WorldUtils.forTypedTileWithWarn(worldIn, pos, GameMasterTile.class, te -> te.onBlockRightClick(player, pos));

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level_, BlockState state_, BlockEntityType<T> blockEntityType_) {
        return ITickableBlockEntity.makeTicker(blockEntityType_, blockEntityType.get());
    }
}
