package ru.timeconqueror.lootgames.api.room;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.room.RoomUtils;
import ru.timeconqueror.lootgames.room.ServerRoomStorage;
import ru.timeconqueror.lootgames.room.client.ClientRoom;

public class RoomAccess {
    @Nullable
    public static Room getRoom(Level level, RoomCoords coords) {
        if (!RoomUtils.inRoomWorld(level)) {
            return null;
        }

        if (level.isClientSide) {
            return ClientRoom.getInstance();
        } else {
            return ServerRoomStorage.getInstance(level)
                    .map(storage -> storage.getRoom(coords))
                    .orElse(null);
        }
    }

    @Nullable
    public static Room getLoadedRoom(Level level, RoomCoords coords) {
        if (!RoomUtils.inRoomWorld(level)) {
            return null;
        }

        if (level.isClientSide) {
            return ClientRoom.getInstance();
        } else {
            return ServerRoomStorage.getInstance(level)
                    .map(storage -> storage.getLoadedRoom(coords))
                    .orElse(null);
        }
    }
}
