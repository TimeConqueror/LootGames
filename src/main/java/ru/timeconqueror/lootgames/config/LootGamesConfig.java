package ru.timeconqueror.lootgames.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.auxiliary.debug.LogHelper;

import java.util.HashMap;

@Mod.EventBusSubscriber
@Config.LangKey("config.lootgames.category.general")
@Config(modid = LootGames.MOD_ID, name = "lootgames/lootgames")
public class LootGamesConfig {

    @Config.LangKey("config.lootgames.category.worldgen")
    @Config.Comment("Regulates dungeon appearing in world.")
    public static WorldGen worldGen = new WorldGen();

    @Config.LangKey("config.lootgames.minigamesenabled")
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

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(LootGames.MOD_ID)) {
            ConfigManager.sync(LootGames.MOD_ID, Config.Type.INSTANCE);
            initExtras();
        }
    }

    public static void initExtras() {
        LootGames.logHelper.setDebugEnabled(enableDebug);
        LootGames.logHelper.setDebugLevel(debugLevel.equalsIgnoreCase("trace") ? LogHelper.Level.TRACE : LogHelper.Level.DEBUG);

        worldGen.init();
    }

    public static class WorldGen {
        @Config.LangKey("config.lootgames.worldgenenabled")
        @Config.Comment({"If this is equal true, then dungeon generation will be enabled.", "Default: true"})
        public boolean isDungeonWorldGenEnabled = true;

        //TODO add retrogen
//        @Config.LangKey("config.lootgames.retrogenenabled")
//        @Config.Comment({"If this is equal true, then minigame dungeons will appear in already generated chunks.", "Default: false"})
        @Config.Ignore
        public boolean isDungeonRetroGenEnabled = false;

        @Config.LangKey("config.lootgames.dimrhomblist")
        @Config.Comment({"Whitelisted dimensions' ids that were allowed for dungeon generation and rhomb size.",
                "Rhomb size means the size of rhombs, which will imaginary cover the world. Dungeon will be generated in each rhomb.",
                "So the larger the size, the less chance of generation.",
                "Rhomb size must be between 5 and 100.",
                "Example of array element: 0; 20 - this means that dungeons will be generated in rhombs with size equal to 20 in the overworld (ID = 0).",
                "Default: {0; 20}"})
        public String[] dimAndRhombList = new String[]{"0; 20"};

        private HashMap<Integer, Integer> dimRhombs;

        public void init() {
            parseDimAndRhombList();
        }

        public boolean isDimensionEnabledForWG(int worldID) {
            return dimRhombs.get(worldID) != null;
        }

        public int getRhombSizeForDim(int dimensionID) {
            return dimRhombs.get(dimensionID);
        }

        private void parseDimAndRhombList() {
            dimRhombs = new HashMap<>();

            for (String entry : dimAndRhombList) {
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
    }
}
