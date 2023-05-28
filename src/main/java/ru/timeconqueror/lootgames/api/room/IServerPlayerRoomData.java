package ru.timeconqueror.lootgames.api.room;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface IServerPlayerRoomData {
    void setLastRoomCoords(RoomCoords coords);

    @Nullable
    RoomCoords getLastRoomCoords();

    static IServerPlayerRoomData of(ServerPlayer player) {
        return (IServerPlayerRoomData) player;
    }
}
