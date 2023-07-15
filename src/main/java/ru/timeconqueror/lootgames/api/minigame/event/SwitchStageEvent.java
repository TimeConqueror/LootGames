package ru.timeconqueror.lootgames.api.minigame.event;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.Stage;

public record SwitchStageEvent(@Nullable Stage from, Stage to) {
}