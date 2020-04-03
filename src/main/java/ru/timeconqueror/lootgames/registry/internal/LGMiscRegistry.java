package ru.timeconqueror.lootgames.registry.internal;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry;
import ru.timeconqueror.lootgames.api.task.TaskCreateExplosion;
import ru.timeconqueror.lootgames.api.task.TaskRegistry;
import ru.timeconqueror.lootgames.common.packet.game.*;
import ru.timeconqueror.lootgames.minigame.pipes.GamePipes;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable(target = TimeAutoRegistrable.Target.CLASS)
public class LGMiscRegistry {

    @SubscribeEvent
    public static void onInit(FMLCommonSetupEvent event) {
        TaskRegistry.registerTaskFactory(TaskCreateExplosion.class, TaskCreateExplosion::new);

        GamePacketRegistry.GamePacketManager manager = GamePacketRegistry.getManager();

        LootGamesAPI.getGameManager().registerGameGenerator(new GamePipes.Factory());

        int id = -1;
        manager.registerPacket(++id, SPChangeStage.class);

        manager.registerPacket(++id, SPPipesGameChangeChunks.class);

        manager.registerPacket(++id, SPMSFieldChanged.class);
        manager.registerPacket(++id, SPMSGenBoard.class);
        manager.registerPacket(++id, SPMSResetFlags.class);
        manager.registerPacket(++id, SPMSSpawnLevelBeatParticles.class);
    }
}
