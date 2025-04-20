package ru.timeconqueror.lootgames.api.room;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.registry.LGDimensions;
import ru.timeconqueror.lootgames.room.ServerRoomStorage;
import ru.timeconqueror.lootgames.room.client.ClientRoom;

import java.util.Objects;

public class RoomAccess {
    @Nullable
    public static Room getRoom(RoomCoords coords, boolean isClientSide) {
        if (isClientSide) {
            return ClientRoom.getInstance();
        } else {
            return getServerStorage().getRoom(coords, false);
        }
    }

    public static ServerRoomStorage getServerStorage() {
        ServerLevel roomWorld = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(LGDimensions.TEST_SITE_DIM));
        return ServerRoomStorage.getInstance(roomWorld).orElseThrow();
    }

    @Nullable
    public static Room getClientRoom() {
        return ClientRoom.getInstance();
    }
}
