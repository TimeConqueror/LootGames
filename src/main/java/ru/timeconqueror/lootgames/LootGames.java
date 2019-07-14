package ru.timeconqueror.lootgames;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import ru.timeconqueror.lootgames.command.CommandMain;
import ru.timeconqueror.lootgames.minigame.GameManager;
import ru.timeconqueror.lootgames.minigame.GameOfLight;
import ru.timeconqueror.lootgames.proxy.CommonProxy;
import ru.timeconqueror.timecore.util.debug.LogHelper;
import ru.timeconqueror.timecore.util.debug.Profiler;

import java.util.Random;

@Mod(modid = LootGames.MODID,
        dependencies = "required-after:forge@[14.23.5.2768,);",
        name = LootGames.MODNAME,
        version = LootGames.VERSION)
public class LootGames {
    public static final String MODID = "lootgames";
    public static final String MODNAME = "LootGames";
    public static final String VERSION = "GRADLETOKEN_VERSION";

    public static Random rand = new Random();

    public static LogHelper logHelper = new LogHelper(MODID);
    public static Profiler profiler = new Profiler();

    public static GameManager gameManager;

//    public static CheaterHandler CheatHandler;
//
//    public static DonorController Donors = null;
//
//    public static NetDispatcher NW;
//    public static LootGamesConfig ModConfig;
//    public static LootGamesWorldGen WorldGen;

    @SidedProxy(clientSide = "ru.timeconqueror.lootgames.proxy.ClientProxy", serverSide = "ru.timeconqueror.lootgames.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance(LootGames.MODID)
    public static LootGames INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (logHelper.isInDev()) {
            logHelper.setPrintDebugToConsole(true);
        }

        proxy.preInit(event);

//        ModConfig = new LootGamesConfig(event.getModConfigurationDirectory(), MODNAME, MODID);
//        if (!ModConfig.LoadConfig())
//            mLog.error(String.format("%s could not load its config file. Things are going to be weird!", MODID));
//
//        URL donorListSource = null;
//        try {
//            donorListSource = new URL("https://pastebin.com/raw/x1K80mwb");
//            Donors = new DonorController(donorListSource);
//        } catch (MalformedURLException e) {
//            mLog.error("Unable to access the DonorList. No special features will be available");
//        }
//
//        Profiler = new ProfilingStorage();
//        CheatHandler = new CheaterHandler();
//        DungeonLogger = new LootGamesDungeonLogger();
//        try {
//            DungeonLogger.setup();
//        } catch (IOException e) {
//            mLog.error("Unable to open DungeonLogger logfile. Spawned dungeons will not get recorded!");
//            e.printStackTrace();
//        }
//
//        WorldGen = new LootGamesWorldGen();
//        GameRegistry.registerWorldGenerator(WorldGen, Integer.MAX_VALUE);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
//        initBaseBlocks();
//
        gameManager = new GameManager();
        gameManager.registerGame(new GameOfLight());
//
//        NW = new NetDispatcher();
//        NW.registerPackets();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
//        LootGameAchievement.registerAchievementPage();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandMain());
//        event.registerServerCommand(new LootGamesCommand());
//        event.registerServerCommand(new ProfilingCommand());
//        if (YAMCore.isDebug())
//            event.registerServerCommand(new PeacefulEntityCommand());
    }

//    private void initBaseBlocks() {
//        DungeonWallBlock = new DungeonBrick();
//        DungeonLightBlock = new DungeonLightSource();
//        MasterBlock = new LootGamesMasterBlock();
//
//        GameRegistry.registerTileEntity(TELootGamesMasterBlock.class, "LOOTGAMES_MASTER_TE");
//        GameRegistry.registerBlock(MasterBlock, "LootGamesMasterBlock");
//        GameRegistry.registerBlock(DungeonWallBlock, ItemBlockMetaBlock.class, "LootGamesDungeonWall");
//        GameRegistry.registerBlock(DungeonLightBlock, ItemBlockMetaBlock.class, "LootGamesDungeonLight");
//    }
}
