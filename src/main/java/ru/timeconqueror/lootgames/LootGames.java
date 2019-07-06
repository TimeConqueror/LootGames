package ru.timeconqueror.lootgames;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = LootGames.MODID,
        dependencies = "required-after:Forge@[14.23.5.2768,);",
        name = LootGames.MODNAME,
        version = LootGames.VERSION)
public class LootGames {
    public static final String MODID = "lootgames";
    public static final String MODNAME = "LootGames";
    public static final String VERSION = "GRADLETOKEN_VERSION";
//    public static Random Rnd = new Random();
//    public static ModCreativeTab CreativeTab;
//    public static LootGamesDungeonLogger DungeonLogger;
//    public static CheaterHandler CheatHandler;
//    public static ProfilingStorage Profiler;
//    public static LootGamesMasterBlock MasterBlock;
//
//    public static DonorController Donors = null;
//
//    public static DungeonBrick DungeonWallBlock;
//    public static DungeonLightSource DungeonLightBlock;
//
//    public static GameManager GameMgr;
//
//    public static LogHelper mLog = new LogHelper("LootGames");
//    public static NetDispatcher NW;
//    public static LootGamesConfig ModConfig;
//    public static LootGamesWorldGen WorldGen;
//
//    @Instance(LootGames.MODID)
//    public static LootGames INSTANCE;
//
//    @EventHandler
//    public void PreLoad(FMLPreInitializationEvent PreEvent) {
//        ModConfig = new LootGamesConfig(PreEvent.getModConfigurationDirectory(), MODNAME, MODID);
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
//        CreativeTab = new ModCreativeTab(MODNAME, Items.book);
//
//        WorldGen = new LootGamesWorldGen();
//        GameRegistry.registerWorldGenerator(WorldGen, Integer.MAX_VALUE);
//    }
//
//    @EventHandler
//    public void init(FMLInitializationEvent event) {
//        initBaseBlocks();
//
//        GameMgr = new GameManager();
//        GameMgr.initGames();
//
//        NW = new NetDispatcher();
//        NW.registerPackets();
//    }
//
//    @EventHandler
//    public void postInit(FMLPostInitializationEvent event) {
//        LootGameAchievement.registerAchievementPage();
//    }
//
//    @EventHandler
//    public void serverLoad(FMLServerStartingEvent pEvent) {
//        pEvent.registerServerCommand(new LootGamesCommand());
//        pEvent.registerServerCommand(new ProfilingCommand());
//        if (YAMCore.isDebug())
//            pEvent.registerServerCommand(new PeacefulEntityCommand());
//    }
//
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
