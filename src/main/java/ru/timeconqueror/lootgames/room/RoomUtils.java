package ru.timeconqueror.lootgames.room;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGDimensions;
import ru.timeconqueror.lootgames.room.client.ClientRoom;

import java.util.List;

public class RoomUtils {
    public static boolean isRoomHolder(ChunkPos chunkPos) {
        int cornerCX = chunkPos.x >> RoomCoords.CHUNK_SHIFT << RoomCoords.CHUNK_SHIFT;
        int cornerCZ = chunkPos.z >> RoomCoords.CHUNK_SHIFT << RoomCoords.CHUNK_SHIFT;

        return chunkPos.x == cornerCX && chunkPos.z == cornerCZ;
    }

    public static ChunkPos getRoomHolderChunkPos(BlockPos pos) {
        int cornerCX = (pos.getX() >> RoomCoords.BLOCK_SHIFT) << RoomCoords.CHUNK_SHIFT;
        int cornerCZ = pos.getZ() >> RoomCoords.BLOCK_SHIFT << RoomCoords.CHUNK_SHIFT;

        return new ChunkPos(cornerCX, cornerCZ);
    }

    public static AABB getRoomBox(Level level, RoomCoords coords) {
        return new AABB(coords.minPos(level), coords.maxPos(level));
    }

    public static List<ServerPlayer> getPlayers(ServerLevel level, AABB aabb) {
        return level.getPlayers(player -> aabb.intersects(player.getBoundingBox()));
    }

    public static boolean inRoomWorld(@Nullable Level level) {
        return level != null && level.dimension() == LGDimensions.TEST_SITE_DIM;
    }

    @Nullable
    public static Room getRoom(Level level, RoomCoords coords, boolean loadIfUnloaded) {
        if (!RoomUtils.inRoomWorld(level)) {
            return null;
        }

        if (level.isClientSide) {
            return ClientRoom.getInstance();
        } else {
            return ServerRoomStorage.getInstance(level)
                    .map(storage -> storage.getRoom(coords, loadIfUnloaded))
                    .orElse(null);
        }
    }
}
