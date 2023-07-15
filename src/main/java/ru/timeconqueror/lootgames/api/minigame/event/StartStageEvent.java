package ru.timeconqueror.lootgames.api.minigame.event;

import ru.timeconqueror.lootgames.api.minigame.Stage;

public record StartStageEvent(Stage stage, boolean isClientSide) {
}