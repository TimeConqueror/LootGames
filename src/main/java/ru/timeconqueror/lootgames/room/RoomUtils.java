package ru.timeconqueror.lootgames.room;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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

    public static AABB getRoomBox(Level level, RoomCoords coords) {
        return new AABB(coords.minPos(level), coords.maxPos(level));
    }

    public static List<Player> getPlayers(Level level, AABB aabb) {
        return level.getEntitiesOfClass(Player.class, aabb);
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
