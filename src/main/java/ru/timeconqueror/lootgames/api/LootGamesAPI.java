package ru.timeconqueror.lootgames.api;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.api.minigame.GameManager;
import ru.timeconqueror.timecore.api.common.event.FMLModConstructedEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
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
