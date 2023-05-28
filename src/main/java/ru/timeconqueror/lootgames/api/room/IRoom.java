package ru.timeconqueror.lootgames.api.room;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import java.util.List;

public interface IRoom {
    RoomCoords getCoords();

    AABB getRoomBox();

    List<ServerPlayer> getPlayers();

    ServerLevel getLevel();

    void allowEnterDuringTick(ServerPlayer player);

    boolean isAllowedToEnter(ServerPlayer player);
}
