package ru.timeconqueror.lootgames.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.event.OnConfigReloadedEvent;
import ru.timeconqueror.timecore.util.debug.LogHelper;

import java.util.HashMap;

@Mod.EventBusSubscriber
@Config(modid = LootGames.MODID, type = Config.Type.INSTANCE)
public class LootGamesConfig {

    @Config.LangKey("config.lootgames.category.worldgen")
    @Config.Comment("Regulates dungeon appearing in world.")
    public static WorldGen worldGen = new WorldGen();

    //TODO add minigame disabling
    @Config.LangKey("config.lootgames.minigamesenabled")
    @Config.Comment({"If this is set to false, then dungeon generation will be enabled.", "Default: true"})
    public static boolean areMinigamesEnabled = true;

    @Config.LangKey("config.lootgames.enabledebug")
    @Config.Comment({"If this is equal true, then it will print additional info to log files.", "Default: false"})
    public static boolean enableDebug = false;


    @Config.LangKey("config.lootgames.debuglevel")
    @Config.Comment({"Available variants: debug, trace",
            "Debug: additionally prints debug messages.",
            "Trace: additionally prints debug and trace messages.",
            "Default: debug"})
    public static String debugLevel = "debug";
    private static HashMap<Integer, Integer> dimRhombs;

    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(LootGames.MODID)) {
            ConfigManager.sync(LootGames.MODID, Config.Type.INSTANCE);
            initExtras();
        }
    }

    @SubscribeEvent
    public static void onConfigReloaded(OnConfigReloadedEvent event) {
        if (event.getModID().equals(LootGames.MODID)) {
            initExtras();
        }
    }

    public static void initExtras() {
        parseDimAndRhombList();
        LootGames.logHelper.setDebugEnabled(enableDebug);
        LootGames.logHelper.setDebugLevel(debugLevel.equalsIgnoreCase("trace") ? LogHelper.Level.TRACE : LogHelper.Level.DEBUG);
    }

    public static boolean isDimensionEnabledForWG(int worldID) {
        return dimRhombs.get(worldID) != null;
    }

    public static int getRhombSizeForDim(int dimensionID) {
        return dimRhombs.get(dimensionID);
    }

    private static void parseDimAndRhombList() {
        dimRhombs = new HashMap<>();

        for (String entry : worldGen.dimAndRhombList) {
            if (entry.length() == 0)
                return;

            String[] arr = entry.split(";");
            if (arr.length != 2) {
                LootGames.logHelper.error("Invalid dimension rhomb entry found: {}. Syntax is <dimensionID>;<rhomb size>. This entry will be skipped.", entry);
            }

            try {
                int dimID = Integer.parseInt(arr[0].trim());
                int rhombSize = Integer.parseInt(arr[1].trim());

                if (rhombSize < 5 || rhombSize > 100) {
                    LootGames.logHelper.error("Invalid dimension rhomb entry found: {}. Rhomb size must be between 5 and 100.", entry);
                } else {
                    if (!dimRhombs.containsKey(dimID)) {
                        dimRhombs.put(dimID, rhombSize);
                        LootGames.logHelper.info("Worldgen enabled in dimension {} with rhomb size {}.", dimID, rhombSize);
                    } else
                        LootGames.logHelper.error("Invalid dimension rhomb entry found: {}. DimensionID is already defined.", entry);
                }

            } catch (NumberFormatException e) {
                LootGames.logHelper.error("Invalid dimension rhomb entry found: {}. DimensionID or Rhomb size is not an Integer. This entry will be skipped.", entry);
            }
        }
    }

    public static class WorldGen {
        @Config.LangKey("config.lootgames.worldgenenabled")
        @Config.Comment({"If this is equal true, then dungeon generation will be enabled.", "Default: true"})
        public boolean isDungeonWorldGenEnabled = true;

        //TODO add retrogen
        @Config.LangKey("config.lootgames.retrogenenabled")
        @Config.Comment({"If this is equal true, then minigames will appear in already generated chunks.", "Default: false"})
        public boolean isDungeonRetroGenEnabled = false;

        @Config.LangKey("config.lootgames.dimrhomblist")
        @Config.Comment({"Whitelisted dimensions' ids that were allowed for dungeon generation and rhomb size.",
                "Rhomb size means the size of rhombs, which will imaginary cover the world. Dungeon will be generated in each rhomb.",
                "So the larger the size, the less chance of generation.",
                "Rhomb size must be between 5 and 100.",
                "Example of array element: 0; 20 - this means that rhombs with size equal to 20 will be generated in the overworld (ID = 0).",
                "Default: {0}"})
        public String[] dimAndRhombList = new String[]{"0; 20"};
    }
}
