package ru.timeconqueror.lootgames.api.room;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.room.PlayerData;

public interface PlayerRoomData {
    void setLastAllowedCoords(@Nullable RoomCoords coords);

    @Nullable
    RoomCoords getLastAllowedCoords();

    static LazyOptional<PlayerRoomData> of(ServerPlayer player) {
        return PlayerData.of(player)
                .lazyMap(playerData -> playerData);
    }
}
