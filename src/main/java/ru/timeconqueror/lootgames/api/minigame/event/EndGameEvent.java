package ru.timeconqueror.lootgames.api.minigame.event;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class EndGameEvent {
    /**
     * If set to true, all default features, such as sending notifications upon event and playing finish sound
     * will be suppressed.
     */
    @Accessors(fluent = true)
    private boolean doNotUseDefaultBehaviour = false;
}
