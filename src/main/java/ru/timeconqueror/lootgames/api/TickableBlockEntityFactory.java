package ru.timeconqueror.lootgames.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ru.timeconqueror.timecore.api.util.ITickableBlockEntity;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TickableBlockEntityFactory {
    private final Supplier<BlockEntityType<? extends ITickableBlockEntity>> type;
    private final CreateFunction createFactory;

    public TickableBlockEntityFactory(Supplier<BlockEntityType<? extends ITickableBlockEntity>> type, CreateFunction createFactory) {
        this.type = type;
        this.createFactory = createFactory;
    }

    public BlockEntity makeFrom(BlockPos pos, BlockState state) {
        return createFactory.apply(pos, state);
    }

    public BlockEntityType<? extends ITickableBlockEntity> getType() {
        return type.get();
    }

    public interface CreateFunction extends BiFunction<BlockPos, BlockState, BlockEntity> {
        @Override
        BlockEntity apply(BlockPos blockPos, BlockState state);
    }
}
