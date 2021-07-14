package eu.usrv.legacylootgames;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import eu.usrv.legacylootgames.auxiliary.CheaterHandler;
import eu.usrv.legacylootgames.auxiliary.GameManager;
import eu.usrv.legacylootgames.auxiliary.LGDonorController;
import eu.usrv.legacylootgames.auxiliary.ProfilingStorage;
import eu.usrv.legacylootgames.command.LootGamesCommand;
import eu.usrv.legacylootgames.command.PeacefulEntityCommand;
import eu.usrv.legacylootgames.command.ProfilingCommand;
import eu.usrv.legacylootgames.config.LegacyLGConfig;
import eu.usrv.legacylootgames.worldgen.LootGamesWorldGen;
import eu.usrv.yamcore.YAMCore;
import ru.timeconqueror.lootgames.LootGames;

import java.io.IOException;
import java.util.Random;

public class LootGamesLegacy {
    public static Random Rnd = new Random();
    public static LootGamesDungeonLogger DungeonLogger;
    public static CheaterHandler CheatHandler;
    public static ProfilingStorage Profiler;

    public static final LGDonorController DONOR_CONTROLLER = new LGDonorController("https://pastebin.com/raw/x1K80mwb");

    public static GameManager GameMgr;

    public static LegacyLGConfig ModConfig;
    public static LootGamesWorldGen WorldGen;

    public static void PreLoad(FMLPreInitializationEvent PreEvent) {
        ModConfig = new LegacyLGConfig(PreEvent.getModConfigurationDirectory(), LootGames.MODNAME, LootGames.MODID);
        if (!ModConfig.LoadConfig())
            LootGames.LOGGER.error(String.format("%s could not load its config file. Things are going to be weird!", LootGames.MODID));

        DONOR_CONTROLLER.loadDonors();

        Profiler = new ProfilingStorage();
        CheatHandler = new CheaterHandler();
        DungeonLogger = new LootGamesDungeonLogger();
        try {
            DungeonLogger.setup();
        } catch (IOException e) {
            LootGames.LOGGER.error("Unable to open DungeonLogger logfile. Spawned dungeons will not get recorded!");
            e.printStackTrace();
        }

        WorldGen = new LootGamesWorldGen();
        GameRegistry.registerWorldGenerator(WorldGen, Integer.MAX_VALUE);
    }

    public static void init(FMLInitializationEvent event) {
        GameMgr = new GameManager();
        GameMgr.initGames();
    }

    public static void onComplete(FMLLoadCompleteEvent event) {
        DONOR_CONTROLLER.fetchDonors();
    }

    public static void serverLoad(FMLServerStartingEvent pEvent) {
        pEvent.registerServerCommand(new LootGamesCommand());
        pEvent.registerServerCommand(new ProfilingCommand());
        if (YAMCore.isDebug())
            pEvent.registerServerCommand(new PeacefulEntityCommand());
    }
}
