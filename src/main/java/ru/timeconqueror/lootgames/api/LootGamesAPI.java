package ru.timeconqueror.lootgames.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.GameManager;
import ru.timeconqueror.lootgames.registry.custom.GameInfo;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootGamesAPI {
    public static final ResourceKey<Registry<GameInfo>> GAME_REGISTRY = ResourceKey.createRegistryKey(LootGames.rl("games"));
    private static GameManager gameManager;
//    private static BoardManager boardManager;

    public static GameManager getGameManager() {
        return gameManager;
    }

//    public static BoardManager getBoardManager() {
//        return boardManager;
//    }

    @SubscribeEvent
    public static void init(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            gameManager = new GameManager();
//            boardManager = new BoardManager();
        });
    }
}
