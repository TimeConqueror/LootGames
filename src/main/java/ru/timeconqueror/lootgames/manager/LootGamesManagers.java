package ru.timeconqueror.lootgames.manager;

import lombok.Getter;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import ru.timeconqueror.lootgames.api.minigame.GameManager;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootGamesManagers {
    @Getter
    private static GameManager gameManager;
    @Getter
    private static BlockCycleManager blockCycleManager;

    @SubscribeEvent
    public static void init(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            gameManager = new GameManager();
            blockCycleManager = new BlockCycleManager();
        });
    }

    @SubscribeEvent
    public static void commonInt(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> blockCycleManager.init());
    }
}
