package ru.timeconqueror.lootgames.minigame;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class EventType<I> {
    @Getter
    private final EventSide eventSide;

    public enum EventSide {
        CLIENT, SERVER, BOTH
    }
}
