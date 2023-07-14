package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.server.level.ServerLevel;
import ru.timeconqueror.lootgames.api.room.Room;

public interface GameEnvironmentGenerator {
    void generate(ServerLevel level, Room room, LootGame<?> game);
}
