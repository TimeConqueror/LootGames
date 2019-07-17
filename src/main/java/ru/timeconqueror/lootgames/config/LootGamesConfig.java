package ru.timeconqueror.lootgames.config;

import net.minecraft.util.ResourceLocation;
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

    @Config.LangKey("config.lootgames.category.gol")
    @Config.Comment("Regulates \"Game of Light\" minigame.")
    public static GOL gameOfLight = new GOL();

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
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
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

        gameOfLight.initStages();
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
        private String[] dimAndRhombList = new String[]{"0; 20"};
    }

    public static class GOL {
        @Config.LangKey("config.lootgames.gol.stage.1")
        @Config.Comment("Regulates characteristics of stage 1.")//TODO change min rounds, rework items
        public Stage stage1 = new Stage(1, false, 24, "minecraft:chests/simple_dungeon", 2, 4);

        @Config.LangKey("config.lootgames.gol.stage.2")
        @Config.Comment("Regulates characteristics of stage 2.")
        public Stage stage2 = new Stage(2, false, 16, "minecraft:chests/abandoned_mineshaft", 4, 6);

        @Config.LangKey("config.lootgames.gol.stage.3")
        @Config.Comment("Regulates characteristics of stage 3.")
        public Stage stage3 = new Stage(3, false, 12, "minecraft:chests/jungle_temple", 6, 8);

        @Config.LangKey("config.lootgames.gol.stage.4")
        @Config.Comment("Regulates characteristics of stage 4.")
        public Stage stage4 = new Stage(4, true, 10, "minecraft:chests/stronghold_corridor", 8, 10);

        @Config.LangKey("config.lootgames.gol.start_digits")
        @Config.Comment({"How many digits should be randomly chosen at game-start?",
                "Default: 2."
        })
        public int startDigitAmount = 2;

        @Config.LangKey("config.lootgames.gol.max_attempts")
        @Config.Comment({"How many attempts does a player have? I means the struct will fail after the first misclicked block.",
                "Default: 3."
        })
        public int maxAttempts = 3;

        @Config.LangKey("config.lootgames.gol.expand_field_stage")//TODO
        @Config.Comment({"At which stage should the playfield become a full 3x3 pattern? Set 0 to disable and keep the 4-block size; set 1 to always start with 3x3.",
                "Default: 2."
        })
        @Config.RangeInt(min = 0, max = 4)
        public int expandFieldAtStage = 2;

        @Config.LangKey("config.lootgames.gol.on_fail_explode")
        @Config.Comment({"Enable or disable struct exploding on max failed attempts.",
                "Default: true"
        })
        public boolean onFailExplode = true;

        @Config.LangKey("config.lootgames.gol.on_fail_spawn")
        @Config.Comment({"Enable or disable struct filling with monsters on max failed attempts.",
                "Default: true"
        })
        public boolean onFailMosters = true;

        @Config.LangKey("config.lootgames.gol.on_fail_lava")
        @Config.Comment({"Enable or disable struct filling with lava on max failed attempts.",
                "Default: true"
        })
        public boolean onFailLava = true;

        @Config.LangKey("config.lootgames.gol.timeout")
        @Config.Comment({"How long does it take to timeout a game? Value is in seconds. If no player input is done in that time, the game will go to sleep. The next player will start fresh.",
                "Default: 60"
        })
        public int timeout = 60;

        private void initStages() {
            stage1.parseLootTable();
            stage2.parseLootTable();
            stage3.parseLootTable();
            stage4.parseLootTable();
        }

        /**
         * @param index - 1-4 (inclusive)
         *              Can return true, if index will be out of bounds.
         */
        public Stage getStageByIndex(int index) {
            return index == 1 ? stage1 : index == 2 ? stage2 : index == 3 ? stage3 : index == 4 ? stage4 : null;
        }

        public static class Stage {
            @Config.LangKey("config.lootgames.gol.stage.min_rounds_required_to_pass")//TODO Dimensional Config
            @Config.Comment({"Minimum correct rounds required to complete this stage and unlock the chest. This can be adjusted per-Dimension in S:DimensionalConfig.",
                    "Default: Stage 1 -> {5}, Stage 2 -> {10}, Stage 3 -> {15}, Stage 4 -> {20}"
            })
            @Config.RangeInt(min = 2, max = 256)
            public int minRoundsRequiredToPass;

            @Config.LangKey("config.lootgames.gol.stage.randomize")
            @Config.Comment({"If true, the pattern will randomize on each level in this stage.",
                    "Default: Stage 1 -> false, Stage 2 -> false, Stage 3 -> false, Stage 4 -> true"
            })
            public boolean randomizeSequence;

            @Config.LangKey("config.lootgames.gol.stage.display_time")
            @Config.Comment({"The amount of time (in ticks; 20 ticks = 1s) to wait at playback before moving to the next color.",
                    "Default: Stage 1 -> {24}, Stage 2 -> {16}, Stage 3 -> {12}, Stage 4 -> {10}"
            })
            @Config.RangeInt(min = 2, max = 40)
            public int displayTime;

            @Config.LangKey("config.lootgames.gol.stage.loot_table")
            @Config.Comment({"The loottable resourcelocation for the chest in this stage.",
                    "Default: Stage 1 -> minecraft:chests/simple_dungeon, Stage 2 -> minecraft:chests/abandoned_mineshaft, Stage 3 -> minecraft:chests/jungle_temple, Stage 4 -> minecraft:chests/stronghold_corridor"
            })
            public String lootTable;

            @Config.Ignore
            public ResourceLocation lootTableRL;

            @Config.LangKey("config.lootgames.gol.stage.min_items")
            @Config.Comment({"Minimum amount of items to be spawned. Won't be applied, if count of items in bound loot table are less than it. If set to -1, then min limit will be disabled.",
                    "Default: Stage 1 -> {2}, Stage 2 -> {4}, Stage 3 -> {6}, Stage 4 -> {8}"
            })
            @Config.RangeInt(min = -1, max = 256)
            public int minItems;

            @Config.LangKey("config.lootgames.gol.stage.max_items")
            @Config.Comment({"Maximum amount of items to be spawned. If set to -1, then max limit will be disabled.",
                    "Default: Stage 1 -> {4}, Stage 2 -> {6}, Stage 3 -> {8}, Stage 4 -> {10}"
            })
            @Config.RangeInt(min = -1, max = 256)
            public int maxItems;

            public Stage(int minRoundsRequiredToPass, boolean randomizeSequence, int displayTime, String lootTable, int minItems, int maxItems) {
                this.minRoundsRequiredToPass = minRoundsRequiredToPass;
                this.randomizeSequence = randomizeSequence;
                this.displayTime = displayTime;
                this.lootTable = lootTable;
                this.minItems = minItems;
                this.maxItems = maxItems;
            }

            private void parseLootTable() {
                lootTableRL = new ResourceLocation(lootTable);
            }

            //TODO add
//            public HashMap<Integer, DimensionalConfig> DimensionalLoots;
        }
    }
}
