package ru.timeconqueror.lootgames.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry;
import ru.timeconqueror.lootgames.api.task.TaskCreateExplosion;
import ru.timeconqueror.lootgames.api.task.TaskRegistry;
import ru.timeconqueror.lootgames.common.packet.game.*;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.minigame.pipes.GamePipes;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LGMiscRegistry {

    @SubscribeEvent//TODO add per-game `disable` config value
    public static void onInit(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TaskRegistry.registerTaskFactory(TaskCreateExplosion.class, TaskCreateExplosion::new);

            LootGamesAPI.getGameManager().registerGameGenerator(new GameMineSweeper.Factory());
            LootGamesAPI.getGameManager().registerGameGenerator(new GamePipes.Factory());

            GamePacketRegistry.GamePacketManager manager = GamePacketRegistry.getManager(LootGames.MODID);
            int id = -1;
            manager.registerPacket(++id, SPChangeStage.class);

            manager.registerPacket(++id, SPPipesGameChangeChunks.class);

            manager.registerPacket(++id, SPMSFieldChanged.class);
            manager.registerPacket(++id, SPMSGenBoard.class);
            manager.registerPacket(++id, SPMSResetFlags.class);
            manager.registerPacket(++id, SPMSSpawnLevelBeatParticles.class);
        });
    }
}
