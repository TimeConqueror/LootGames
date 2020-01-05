package ru.timeconqueror.lootgames;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import ru.timeconqueror.lootgames.api.minigame.GameManager;
import ru.timeconqueror.lootgames.command.CommandMain;
import ru.timeconqueror.lootgames.proxy.CommonProxy;
import ru.timeconqueror.timecore.api.auxiliary.debug.LogHelper;
import ru.timeconqueror.timecore.api.auxiliary.debug.Profiler;

import java.util.Random;
//TODO add hashsum check to deny access on servers for hacked mod
@Mod(modid = LootGames.MOD_ID,
        dependencies = "required-after:forge@[14.23.5.2768,);" + "required-after:timecore@[GRADLETOKEN_VERSION,);",//fixme undo gradletoken
        name = LootGames.MOD_NAME,
        version = LootGames.VERSION)
public class LootGames {
    public static final String MOD_ID = "lootgames";
    public static final String MOD_NAME = "LootGames";
    public static final String VERSION = "GRADLETOKEN_VERSION";

    public static final Random RAND = new Random();
    public static final LGCreativeTabs TAB_LOOTGAMES = new LGCreativeTabs(CreativeTabs.getNextID(), LootGames.MOD_ID);


    public static LogHelper logHelper = new LogHelper(MOD_ID);
    public static Profiler profiler = new Profiler();

    public static GameManager gameManager;

    @SidedProxy(clientSide = "ru.timeconqueror.lootgames.proxy.ClientProxy", serverSide = "ru.timeconqueror.lootgames.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance(LootGames.MOD_ID)
    public static LootGames instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (logHelper.isInDev()) {
            logHelper.setPrintDebugToConsole(true);
        }

        proxy.preInit(event);
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
