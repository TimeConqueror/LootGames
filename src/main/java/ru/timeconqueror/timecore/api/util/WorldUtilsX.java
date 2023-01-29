package ru.timeconqueror.timecore.api.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class WorldUtilsX {
    @Nullable
    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> makeSimpleTicker(BlockEntityType<A> providedType, BlockEntityType<E> requiredType, BlockEntityTicker<? super E> ticker) {
        return requiredType == providedType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> makeSimpleTicker(BlockEntityType<A> providedType, Supplier<BlockEntityType<E>> requiredType, BlockEntityTicker<? super E> ticker) {
        return requiredType.get() == providedType ? (BlockEntityTicker<A>) ticker : null;
    }
}
