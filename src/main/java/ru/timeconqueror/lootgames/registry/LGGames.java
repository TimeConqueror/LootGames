package ru.timeconqueror.lootgames.registry;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.custom.GameInfo;
import ru.timeconqueror.timecore.api.registry.SimpleVanillaRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class LGGames {
    @AutoRegistrable
    private static final SimpleVanillaRegister<GameInfo> REGISTER = new SimpleVanillaRegister<>(LootGamesAPI.GAME_REGISTRY, LootGames.MODID);

    @AutoRegistrable.Init
    private static void register() {
        REGISTER.register("minesweeper", () -> new GameInfo(GameMineSweeper::new));
    }
}
