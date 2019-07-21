package ru.timeconqueror.lootgames;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import ru.timeconqueror.lootgames.command.CommandMain;
import ru.timeconqueror.lootgames.minigame.GameManager;
import ru.timeconqueror.lootgames.proxy.CommonProxy;
import ru.timeconqueror.timecore.util.debug.LogHelper;
import ru.timeconqueror.timecore.util.debug.Profiler;

import java.util.Random;

@Mod(modid = LootGames.MODID,
        dependencies = "required-after:forge@[14.23.5.2768,);" + "required-after:timecore@[1.0.0,);",
        name = LootGames.MODNAME,
        version = LootGames.VERSION)
public class LootGames {
    public static final String MODID = "lootgames";
    public static final String MODNAME = "LootGames";
    public static final String VERSION = "GRADLETOKEN_VERSION";

    public static final LGCreativeTabs TAB_LOOTGAMES = new LGCreativeTabs(CreativeTabs.getNextID(), LootGames.MODID);

    public static Random rand = new Random();

    public static LogHelper logHelper = new LogHelper(MODID);
    public static Profiler profiler = new Profiler();

    public static GameManager gameManager;

//    public static DonorController Donors = null;

    @SidedProxy(clientSide = "ru.timeconqueror.lootgames.proxy.ClientProxy", serverSide = "ru.timeconqueror.lootgames.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance(LootGames.MODID)
    public static LootGames instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (logHelper.isInDev()) {
            logHelper.setPrintDebugToConsole(true);
        }

        proxy.preInit(event);

//        URL donorListSource = null;
//        try {
//            donorListSource = new URL("https://pastebin.com/raw/x1K80mwb");
//            Donors = new DonorController(donorListSource);
//        } catch (MalformedURLException e) {
//            mLog.error("Unable to access the DonorList. No special features will be available");
//        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandMain());
    }
}
