package ru.timeconqueror.lootgames.api.room;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class RoomUtils {
    public static boolean isRoomHolder(ChunkPos chunkPos) {
        int chunkShift = RoomCoords.BLOCK_SHIFT - 4;
        int cornerCX = chunkPos.x >> chunkShift << chunkShift;
        int cornerCZ = chunkPos.z >> chunkShift << chunkShift;

        return chunkPos.x == cornerCX && chunkPos.z == cornerCZ;
    }

    public static ChunkPos getRoomHolderChunkPos(BlockPos pos) {
        int chunkShift = RoomCoords.BLOCK_SHIFT - 4;
        int cornerCX = pos.getX() >> RoomCoords.BLOCK_SHIFT << chunkShift;
        int cornerCZ = pos.getZ() >> RoomCoords.BLOCK_SHIFT << chunkShift;

        return new ChunkPos(cornerCX, cornerCZ);
    }
}
