package ru.timeconqueror.lootgames.room;

import com.google.common.collect.Iterables;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGBlocks;

public class RoomGenerator {
    public static void generateRoomBorders(ServerRoom room) {
        ServerLevel level = room.getLevel();

        RoomCoords coords = room.getCoords();
        BlockPos min = coords.minPos(level);
        BlockPos max = coords.maxPos(level);

        Iterables.concat(
                // walls
                BlockPos.betweenClosed(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), min.getZ()),
                BlockPos.betweenClosed(min.getX(), min.getY(), min.getZ() + 1, min.getX(), max.getY(), max.getZ() - 1),
                BlockPos.betweenClosed(max.getX(), min.getY(), min.getZ() + 1, max.getX(), max.getY(), max.getZ() - 1),
                BlockPos.betweenClosed(min.getX(), min.getY(), max.getZ(), max.getX(), max.getY(), max.getZ()),
                // floor
                BlockPos.betweenClosed(min.getX() + 1, min.getY(), min.getZ() + 1, max.getX() - 1, min.getY(), max.getZ() - 1),
                // ceiling
                BlockPos.betweenClosed(min.getX() + 1, max.getY(), min.getZ() + 1, max.getX() - 1, max.getY(), max.getZ() - 1)
        ).forEach(blockPos -> setOuterSpace(level, blockPos));
    }

    private static void setOuterSpace(Level level, BlockPos pos) {
        level.setBlock(pos, LGBlocks.SPACE_FABRIC.defaultBlockState(), Block.UPDATE_CLIENTS);
    }
}
