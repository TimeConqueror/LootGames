package ru.timeconqueror.lootgames.api.room;

import org.jetbrains.annotations.Contract;

public interface RoomStorage {
    int reserveFreeIndex();

    /**
     * Retrieves the room at the specified coordinates.
     * <p>
     * If {@code loadIfUnloaded} is {@code true}, the method guarantees a non-null result,
     * loading the room if necessary. Otherwise, the method may return {@code null}
     * if the room is not already loaded.
     *
     * @param coords         the coordinates of the room
     * @param loadIfUnloaded whether to load the room if it is not already loaded
     * @return the room at the given coordinates, or {@code null} if it is not loaded and
     * {@code loadIfUnloaded} is {@code false}
     */
    @Contract("_, true -> !null; _, false -> _")
    Room getRoom(RoomCoords coords, boolean loadIfUnloaded);
}
