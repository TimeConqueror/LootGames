package ru.timeconqueror.lootgames.minigame;

import net.minecraft.server.level.ServerLevel;
import ru.timeconqueror.lootgames.api.minigame.GameEnvironmentGenerator;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.Room;

public class EmptyGameEnvironmentGenerator implements GameEnvironmentGenerator {
    public static final GameEnvironmentGenerator INSTANCE = new EmptyGameEnvironmentGenerator();

    @Override
    public void generate(ServerLevel level, Room room, LootGame<?> game) {

    }
}
