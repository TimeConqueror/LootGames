package ru.timeconqueror.lootgames.api;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import ru.timeconqueror.lootgames.api.minigame.BoardManager;
import ru.timeconqueror.lootgames.api.minigame.GameManager;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootGamesAPI {
    private static GameManager gameManager;
    private static BoardManager boardManager;

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static BoardManager getBoardManager() {
        return boardManager;
    }

    @SubscribeEvent
    public static void init(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            gameManager = new GameManager();
            boardManager = new BoardManager();
        });
    }
}
