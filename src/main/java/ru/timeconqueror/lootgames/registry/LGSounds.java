package ru.timeconqueror.lootgames.registry;

import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.common.sound.TimeSound;
import ru.timeconqueror.timecore.registry.AutoRegistrable;
import ru.timeconqueror.timecore.registry.newreg.SoundRegister;

import static ru.timeconqueror.timecore.util.Hacks.promise;

@ObjectHolder(LootGames.MODID)
public class LGSounds {
    public static final TimeSound GOL_START_GAME = promise();
    public static final TimeSound GOL_SEQUENCE_WRONG = promise();
    public static final TimeSound GOL_SEQUENCE_COMPLETE = promise();
    public static final TimeSound GOL_GAME_WIN = promise();
    public static final TimeSound GOL_GAME_LOSE = promise();

    public static final TimeSound MS_START_GAME = promise();
    public static final TimeSound MS_ON_EMPTY_REVEAL_NEIGHBOURS = promise();
    public static final TimeSound MS_BOMB_ACTIVATED = promise();

    public static TimeSound PUZZLE_MASTER_STRANGE;

    private static class Init {
        @AutoRegistrable
        private static final SoundRegister REGISTER = new SoundRegister(LootGames.MODID);

        @AutoRegistrable.InitMethod
        private static void register() {
            REGISTER.register("gol_start_game");
            REGISTER.register("gol_sequence_wrong");
            REGISTER.register("gol_sequence_complete");
            REGISTER.register("gol_game_win");
            REGISTER.register("gol_game_lose");
            REGISTER.register("ms_start_game");
            REGISTER.register("ms_on_empty_reveal_neighbours");
            REGISTER.register("ms_bomb_activated");
            REGISTER.register("puzzle_master_strange");
        }
    }
}
