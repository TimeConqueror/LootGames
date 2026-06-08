package ru.timeconqueror.lootgames.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.manager.LootGamesManagers;
import ru.timeconqueror.lootgames.api.minigame.GameManager;
import ru.timeconqueror.lootgames.registry.custom.GameDefinition;

public class LootGamesAPI {
    public static final ResourceKey<Registry<GameDefinition>> GAME_REGISTRY = ResourceKey.createRegistryKey(LootGames.rl("games"));

    public static GameManager getGameManager() {
        return LootGamesManagers.getGameManager();
    }
}
