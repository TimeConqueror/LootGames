package ru.timeconqueror.lootgames.api;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.lootgames.api.minigame.GameManager;

public class LootGamesAPI {
    private static GameManager gameManager;

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static void init(FMLCommonSetupEvent event) {
        gameManager = new GameManager();
    }
}
