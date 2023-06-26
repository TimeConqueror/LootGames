package ru.timeconqueror.lootgames.registry.custom;

import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.Room;

import java.util.function.BiFunction;

public class GameInfo {
    private final BiFunction<ResourceLocation, Room, ? extends LootGame<?>> creator;

    public GameInfo(BiFunction<ResourceLocation, Room, ? extends LootGame<?>> creator) {
        this.creator = creator;
    }

    public LootGame<?> createGame(ResourceLocation id, Room room) {
        return creator.apply(id, room);
    }
}