package ru.timeconqueror.lootgames.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.achievement.AdvancementManager;
import ru.timeconqueror.lootgames.api.minigame.GameManager;
import ru.timeconqueror.lootgames.config.LootGamesConfig;
import ru.timeconqueror.lootgames.minigame.gameoflight.GameOfLight;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.packets.NetworkHandler;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.registry.ModSounds;
import ru.timeconqueror.lootgames.world.gen.LootGamesWorldGen;
import ru.timeconqueror.timecore.api.registry.EasyRegistry;

public class CommonProxy {
    public static final EasyRegistry REGISTRY = new EasyRegistry(LootGames.MODID, LootGames.TAB_LOOTGAMES);

    public void preInit(FMLPreInitializationEvent event) {
        ModBlocks.register();
        ModBlocks.registerTileEntities();

        NetworkHandler.registerPackets();

        AdvancementManager.registerCriteria();
    }

    public void init(FMLInitializationEvent event) {
        LootGamesConfig.initExtras();
        GameRegistry.registerWorldGenerator(new LootGamesWorldGen(), Integer.MAX_VALUE);

        LootGames.gameManager = new GameManager();

        LootGames.gameManager.registerGame(GameOfLight.class, new GameOfLight.Factory());
        LootGames.gameManager.registerGame(GameMineSweeper.class, new GameMineSweeper.Factory());
    }

    public void postInit(FMLPostInitializationEvent event) {
        ModSounds.registerSounds();
    }
}
