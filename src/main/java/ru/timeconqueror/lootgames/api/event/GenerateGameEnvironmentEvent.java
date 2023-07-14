package ru.timeconqueror.lootgames.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraftforge.eventbus.api.Event;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.room.ServerRoom;

@AllArgsConstructor
public class GenerateGameEnvironmentEvent extends Event {
    @Getter
    private final ServerRoom room;
    @Getter
    private final LootGame<?> game;
}
