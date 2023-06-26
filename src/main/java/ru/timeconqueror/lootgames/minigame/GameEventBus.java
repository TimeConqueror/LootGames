package ru.timeconqueror.lootgames.minigame;

import lombok.RequiredArgsConstructor;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;
import ru.timeconqueror.lootgames.utils.EventBus;

@RequiredArgsConstructor
public class GameEventBus extends EventBus {
    private final LootGame<?> game;
    private EventBus stageBus = null;

    public void transferEventBus(Stage newStage) {
        stageBus = new EventBus();
        newStage.subscribeToEvents(stageBus);
    }

    @Override
    public <T> void post(EventType<T> event, T data) {
        if (!game.isStarted()) return;

        super.post(event, data);
        if (stageBus != null) {
            stageBus.post(event, data);
        }
    }
}
