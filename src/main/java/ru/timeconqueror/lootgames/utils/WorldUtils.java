package ru.timeconqueror.lootgames.utils;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.timecore.TimeCore;

import java.util.function.Consumer;

//TODO move to TimeCore
public class WorldUtils {

    public static <T> void ifHas(World world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        TileEntity tile = world.getBlockEntity(pos);

        if (clazz.isInstance(tile)) {
            action.accept((T) tile);
        }
    }

    public static <T> void forTileWithWarn(World world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        TileEntity tile = world.getBlockEntity(pos);

        if (tile == null) {
            TimeCore.LOGGER.warn("Can't handle this tile, because it's null on {}", pos, new IllegalStateException());
            return;
        }

        if (clazz.isInstance(tile)) {
            action.accept((T) tile);
        } else {
            TimeCore.LOGGER.warn("Can't handle this tile, because it's {} instead of {} on {}", tile.getClass(), clazz.getName(), pos, new IllegalStateException());
        }
    }

    public static <T> void forTileWithReqt(World world, BlockPos pos, Class<T> clazz, Consumer<T> action) {
        TileEntity tile = world.getBlockEntity(pos);

        if (tile == null) {
            throw new IllegalStateException("Can't handle this tile, because it's null on " + pos);
        }

        if (clazz.isInstance(tile)) {
            action.accept((T) tile);
        } else {
            throw new IllegalStateException("Can't handle this tile, because it's " + tile.getClass() + " instead of " + clazz.getName() + " on " + pos);
        }
    }
}
