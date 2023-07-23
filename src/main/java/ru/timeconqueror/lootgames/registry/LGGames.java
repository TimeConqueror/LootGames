package ru.timeconqueror.lootgames.registry;

import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.minigame.BoardGameEnvironmentGenerator;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.custom.GameInfo;
import ru.timeconqueror.timecore.api.registry.SimpleVanillaRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class LGGames {
    @AutoRegistrable
    private static final SimpleVanillaRegister<GameInfo> REGISTER = new SimpleVanillaRegister<>(LootGamesAPI.GAME_REGISTRY, LootGames.MODID);

    public static final ResourceLocation MINESWEEPER = REGISTER.register("minesweeper", () -> GameInfo.builder()
                    .creator(GameMineSweeper::new)
                    .generator(new BoardGameEnvironmentGenerator(() -> LGBlocks.MINESWEEPER_GLASSY_MATTER.defaultBlockState()))
                    .build())
            .getId();
}
