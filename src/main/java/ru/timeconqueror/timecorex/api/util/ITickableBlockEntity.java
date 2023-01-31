package ru.timeconqueror.timecorex.api.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface ITickableBlockEntity {
    void tick(Level level);

    static <P extends BlockEntity, E extends BlockEntity & ITickableBlockEntity> BlockEntityTicker<P> makeTicker(BlockEntityType<P> providedType, BlockEntityType<E> requiredType) {
        return WorldUtilsX.makeSimpleTicker(providedType, requiredType, (level, pos, state, blockEntity) -> blockEntity.tick(level));
    }
}
