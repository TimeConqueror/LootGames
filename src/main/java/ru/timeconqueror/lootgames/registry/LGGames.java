package ru.timeconqueror.lootgames.registry;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.minigame.BoardGameEnvironmentGenerator;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.custom.GameInfo;
import ru.timeconqueror.timecore.api.registry.SimpleVanillaRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.Promised;

public class LGGames {
    @AutoRegistrable
    private static final SimpleVanillaRegister<GameInfo> REGISTER = new SimpleVanillaRegister<>(LootGamesAPI.GAME_REGISTRY, LootGames.MODID);

    public static final Promised<GameInfo> GAME_OF_LIGHT = REGISTER.register("game_of_light", () -> GameInfo.builder()
            .langKey("game.lootgames.game_of_light")
            .microverseHolderItem(() -> LGItems.BOXED_GAME_OF_LIGHT_MICROVERSE)
            .gameInstanceCreator(GameOfLight::new)
            .generator(new BoardGameEnvironmentGenerator(() -> LGBlocks.BOARD_GLASSY_MATTER.defaultBlockState()))
            .build());

    public static final Promised<GameInfo> MINESWEEPER = REGISTER.register("minesweeper", () -> GameInfo.builder()
            .langKey("game.lootgames.minesweeper")
            .microverseHolderItem(() -> LGItems.BOXED_MINESWEEPER_MICROVERSE)
            .gameInstanceCreator(GameMineSweeper::new)
            .generator(new BoardGameEnvironmentGenerator(() -> LGBlocks.BOARD_GLASSY_MATTER.defaultBlockState()))
            .build());
}
