package ru.timeconqueror.lootgames.api.minigame.event;

import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;
import ru.timeconqueror.lootgames.minigame.EventType;
import ru.timeconqueror.lootgames.minigame.EventType.EventSide;

public class GameEvents {
    /**
     * Should be used for game initialization.
     * Here you can set any initial data.
     * After the method all serializable data will be automatically synced.
     */
    public static final EventType<Void> START_GAME = new EventType<>(EventSide.SERVER);
    public static final EventType<Void> FINISH_GAME = new EventType<>(EventSide.SERVER);
    public static final EventType<Void> LOSE_GAME = new EventType<>(EventSide.SERVER);
    public static final EventType<Void> WIN_GAME = new EventType<>(EventSide.SERVER);
    /**
     * Called when the game was switched to this stage at the moment, when the previous stage is ended,
     * but next is not started yet.
     * <br>
     * For server: called only when you manually change stage on server side via {@link LootGame#setupInitialStage()}
     * or {@link LootGame#switchStage(Stage)}.
     * <br>
     * For client: called every time when server sends new state, including deserializing from saved nbt.
     */
    public static final EventType<SwitchStageEvent> SWITCH_STAGE = new EventType<>(EventSide.BOTH);
    /**
     * Called right after the stage was started:
     * <ol>
     *     <li>by changing stage via {@link LootGame#setupInitialStage()} or {@link LootGame#switchStage(Stage)}</li>
     *     <li>by deserializing and syncing</li>
     * </ol>
     */
    public static final EventType<StartStageEvent> START_STAGE = new EventType<>(EventSide.BOTH);
    public static final EventType<BlockClickEvent> CLICK_BLOCK = new EventType<>(EventSide.BOTH);
}
