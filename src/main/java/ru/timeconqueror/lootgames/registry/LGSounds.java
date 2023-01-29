package ru.timeconqueror.lootgames.registry;

import net.minecraft.sounds.SoundEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.registry.SoundRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

@AutoRegistrable.Entries("sound_event")
public class LGSounds {
    public static SoundEvent GOL_START_GAME;
    public static SoundEvent GOL_SEQUENCE_WRONG;
    public static SoundEvent GOL_SEQUENCE_COMPLETE;
    public static SoundEvent GAME_WIN;
    public static SoundEvent GAME_LOSE;

    public static SoundEvent MS_START_GAME;
    public static SoundEvent MS_ON_EMPTY_REVEAL_NEIGHBOURS;
    public static SoundEvent MS_BOMB_ACTIVATED;

    public static SoundEvent PUZZLE_MASTER_STRANGE;

    @AutoRegistrable
    private static final SoundRegister REGISTER = new SoundRegister(LootGames.MODID);

    @AutoRegistrable.Init
    private static void register() {
        REGISTER.register("gol_start_game");
        REGISTER.register("gol_sequence_wrong");
        REGISTER.register("gol_sequence_complete");
        REGISTER.register("game_win");
        REGISTER.register("game_lose");
        REGISTER.register("ms_start_game");
        REGISTER.register("ms_on_empty_reveal_neighbours");
        REGISTER.register("ms_bomb_activated");
        REGISTER.register("puzzle_master_strange");
    }
}
