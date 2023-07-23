package ru.timeconqueror.lootgames.room;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomAccess;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGDimensions;

import java.util.List;
import java.util.function.Predicate;

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

    public static boolean inRoomWorld(Level level) {
        return level.dimension() == LGDimensions.TEST_SITE_DIM;
    }

    @Nullable
    public static Room getRoom(Level level, RoomCoords coords) {
        return RoomAccess.getRoom(level, coords);
    }

    @Nullable
    public static Room getLoadedRoom(Level level, RoomCoords coords) {
        return RoomAccess.getLoadedRoom(level, coords);
    }

    public static boolean hasSpecificStartedGame(Level level, RoomCoords coords, ResourceLocation gameId) {
        return hasSpecificGame(level, coords, game -> game != null && game.isStarted() && game.getId().equals(gameId));
    }

    public static boolean hasSpecificStartedGame(Level level, RoomCoords coords, Class<? extends LootGame<?>> gameClass) {
        return hasSpecificGame(level, coords, game -> game != null && game.isStarted() && gameClass.isInstance(game));
    }

    public static boolean hasSpecificGame(Level level, RoomCoords coords, Predicate<@Nullable LootGame<?>> gamePredicate) {
        Room room = getRoom(level, coords);
        if (room == null) return false;

        LootGame<?> game = room.getGame();
        return gamePredicate.test(game);
    }
}
