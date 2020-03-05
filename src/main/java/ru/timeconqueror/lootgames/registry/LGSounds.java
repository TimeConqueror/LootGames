package ru.timeconqueror.lootgames.registry;

import ru.timeconqueror.timecore.api.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.api.registry.sound.SoundTimeRegistry;
import ru.timeconqueror.timecore.api.registry.sound.TimeSound;

@TimeAutoRegistry
public class LGSounds extends SoundTimeRegistry {
    public static final TimeSound GOL_START_GAME = createSound("gol_start_game");
    public static final TimeSound GOL_SEQUENCE_WRONG = createSound("gol_sequence_wrong");
    public static final TimeSound GOL_SEQUENCE_COMPLETE = createSound("gol_sequence_complete");
    public static final TimeSound GOL_GAME_WIN = createSound("gol_gameover_win");
    public static final TimeSound GOL_GAME_LOSE = createSound("gol_gameover_lose");

    public static final TimeSound MS_START_GAME = createSound("ms_start_game");
    public static final TimeSound MS_ON_EMPTY_REVEAL_NEIGHBOURS = createSound("ms_empty_revel_neighbours");
    public static final TimeSound MS_BOMB_ACTIVATED = createSound("bomb_activated");

    public static TimeSound PUZZLE_MASTER_STRANGE = createSound("puzzle_master_strange");

    @Override
    public void register() {
        regSound(GOL_START_GAME);
        regSound(GOL_SEQUENCE_WRONG);
        regSound(GOL_SEQUENCE_COMPLETE);
        regSound(GOL_GAME_WIN);
        regSound(GOL_GAME_LOSE);
        regSound(MS_START_GAME);
        regSound(MS_ON_EMPTY_REVEAL_NEIGHBOURS);
        regSound(MS_BOMB_ACTIVATED);
        regSound(PUZZLE_MASTER_STRANGE);
    }
}
