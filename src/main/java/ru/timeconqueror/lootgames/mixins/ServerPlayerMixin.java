package ru.timeconqueror.lootgames.mixins;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import ru.timeconqueror.lootgames.api.room.IServerPlayerRoomData;
import ru.timeconqueror.lootgames.api.room.RoomCoords;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IServerPlayerRoomData {
    @Unique
    private RoomCoords lastRoomCoords;

    @Override
    public void setLastRoomCoords(RoomCoords coords) {
        lastRoomCoords = coords;
    }

    @Override
    public RoomCoords getLastRoomCoords() {
        return lastRoomCoords;
    }
}
