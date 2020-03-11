package ru.timeconqueror.lootgames.api;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.lootgames.api.minigame.GameManager;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable
public class LootGamesAPI {
    private static GameManager gameManager;

    public static GameManager getGameManager() {
        return gameManager;
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        gameManager = new GameManager();
    }
}
