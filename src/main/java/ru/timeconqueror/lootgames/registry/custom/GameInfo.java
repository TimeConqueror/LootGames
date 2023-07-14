package ru.timeconqueror.lootgames.registry.custom;

import lombok.Builder;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.lootgames.api.minigame.GameEnvironmentGenerator;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.minigame.EmptyGameEnvironmentGenerator;

import java.util.function.BiFunction;

@Builder
public class GameInfo {
    private final BiFunction<ResourceLocation, Room, ? extends LootGame<?>> creator;
    @Builder.Default
    private GameEnvironmentGenerator generator = EmptyGameEnvironmentGenerator.INSTANCE;

    public LootGame<?> createGame(ResourceLocation id, Room room) {
        return creator.apply(id, room);
    }

    public GameEnvironmentGenerator getGenerator() {
        return generator;
    }
}