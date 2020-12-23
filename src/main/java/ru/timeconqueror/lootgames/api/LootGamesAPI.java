package ru.timeconqueror.lootgames.api;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import ru.timeconqueror.lootgames.api.minigame.FieldManager;
import ru.timeconqueror.lootgames.api.minigame.GameManager;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootGamesAPI {
    private static GameManager gameManager;
    private static FieldManager fieldManager;

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static FieldManager getFieldManager() {
        return fieldManager;
    }

    @SubscribeEvent
    public static void init(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            gameManager = new GameManager();
            fieldManager = new FieldManager();
        });
    }
}
