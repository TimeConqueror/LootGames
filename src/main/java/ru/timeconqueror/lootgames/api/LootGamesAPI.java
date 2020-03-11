package ru.timeconqueror.lootgames.api;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import ru.timeconqueror.lootgames.api.minigame.GameManager;
import ru.timeconqueror.timecore.api.common.event.FMLModConstructedEvent;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable(target = TimeAutoRegistrable.Target.CLASS)
public class LootGamesAPI {
    private static GameManager gameManager;

    public static GameManager getGameManager() {
        return gameManager;
    }

    @SubscribeEvent
    public static void init(FMLModConstructedEvent event) {
        gameManager = new GameManager();
    }
}
