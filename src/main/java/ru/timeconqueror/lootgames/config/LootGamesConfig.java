package ru.timeconqueror.lootgames.config;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.auxiliary.IntHelper;
import ru.timeconqueror.timecore.api.auxiliary.debug.LogHelper;
import ru.timeconqueror.timecore.api.event.OnConfigReloadedEvent;

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

    @Config.LangKey("config.lootgames.minigamesenabled")
    @Config.Comment({"If this is set to false, then puzzle master won't start any game.", "Default: true"})
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
                "Default: {0}"})
        private String[] dimAndRhombList = new String[]{"0; 20"};
    }

    public static class GOL {
        @Config.LangKey("config.lootgames.gol.stage.1")
        @Config.Comment("Regulates characteristics of stage 1.")
        public Stage stage1 = new Stage(5, false, 24, "minecraft:chests/simple_dungeon", 15, 15);

        @Config.LangKey("config.lootgames.gol.stage.2")
        @Config.Comment("Regulates characteristics of stage 2.")
        public Stage stage2 = new Stage(10, false, 16, "minecraft:chests/abandoned_mineshaft", -1, -1);

        @Config.LangKey("config.lootgames.gol.stage.3")
        @Config.Comment("Regulates characteristics of stage 3.")
        public Stage stage3 = new Stage(15, false, 12, "minecraft:chests/jungle_temple", -1, -1);

        @Config.LangKey("config.lootgames.gol.stage.4")
        @Config.Comment("Regulates characteristics of stage 4.")
        public Stage stage4 = new Stage(16, true, 10, "minecraft:chests/stronghold_corridor", -1, -1);

        @Config.LangKey("config.lootgames.gol.start_digits")
        @Config.Comment({"How many digits should be randomly chosen and shown at game-start?",
                "Default: 2."
        })
        public int startDigitAmount = 2;

        @Config.LangKey("config.lootgames.gol.max_attempts")
        @Config.Comment({"How many attempts does a player have? The structure will fail after max attempt count is passed, or the player will win if he beat at least 1 stage.",
                "Default: 3."
        })
        public int maxAttempts = 3;

        @Config.LangKey("config.lootgames.gol.expand_field_stage")
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

        @Config.LangKey("config.lootgames.gol.on_fail_zombies")
        @Config.Comment({"Enable or disable struct filling with zombies on max failed attempts.",
                "Default: true"
        })
        public boolean onFailZombies = true;

        @Config.LangKey("config.lootgames.gol.on_fail_lava")
        @Config.Comment({"Enable or disable struct filling with lava on max failed attempts.",
                "Default: true"
        })
        public boolean onFailLava = true;

        @Config.LangKey("config.lootgames.gol.timeout")
        @Config.Comment({"How long does it take to timeout a game? Value is in seconds. How long does it take to timeout a game? Value is in seconds. If player has been inactive for given time, the game will go to sleep. The next player can start the game from the beginning.",
                "Default: 60"
        })
        public int timeout = 60;

        private void initStages() {
            for (int i = 1; i <= 4; i++) {
                getStageByIndex(i).parseLootTable();
                getStageByIndex(i).parseDimConfigs();
            }
        }

        /**
         * @param index - 1-4 (inclusive)
         *              Can return true, if index will be out of bounds.
         */
        public Stage getStageByIndex(int index) {
            return index == 1 ? stage1 : index == 2 ? stage2 : index == 3 ? stage3 : index == 4 ? stage4 : null;
        }

        public static class Stage {
            @Config.LangKey("config.lootgames.gol.stage.min_rounds_required_to_pass")
            @Config.Comment({"Minimum correct rounds required to complete this stage and unlock the chest. This can be adjusted per-Dimension in S:DimensionalConfig.",
                    "Default: Stage 1 -> {5}, Stage 2 -> {10}, Stage 3 -> {15}, Stage 4 -> {20}"
            })
            @Config.RangeInt(min = 1, max = 256)
            public int minRoundsRequiredToPass;

            @Config.LangKey("config.lootgames.gol.stage.randomize")
            @Config.Comment({"If true, the pattern will randomize on each round in this stage.",
                    "Default: Stage 1 -> false, Stage 2 -> false, Stage 3 -> false, Stage 4 -> true"
            })
            public boolean randomizeSequence;

            @Config.LangKey("config.lootgames.gol.stage.display_time")
            @Config.Comment({"The amount of time (in ticks; 20 ticks = 1s) the symbol will be displayed.",
                    "Default: Stage 1 -> {24}, Stage 2 -> {16}, Stage 3 -> {12}, Stage 4 -> {10}"
            })
            @Config.RangeInt(min = 2, max = 40)
            public int displayTime;

            @Config.LangKey("config.lootgames.gol.stage.loot_table")
            @Config.Comment({"Name of the loottable, items from which will be generated in the chest of this stage. This can be adjusted per-Dimension in S:DimensionalConfig.",
                    "Default: Stage 1 -> minecraft:chests/simple_dungeon, Stage 2 -> minecraft:chests/abandoned_mineshaft, Stage 3 -> minecraft:chests/jungle_temple, Stage 4 -> minecraft:chests/stronghold_corridor"
            })
            public String lootTable;
            @Config.LangKey("config.lootgames.gol.stage.dimconfig")
            @Config.Comment({"Here you can add different loottables to each dimension. If dimension isn't in this list, then game will take default loottable for this stage.",
                    "Syntax: <dimension_id>; <loottable_name>; <min_rounds_required_to_pass>",
                    "<loottable_name> - The loottable name for the chest in this stage. Can be skipped. In this case you just need to write semicolon, example: \"{0; ; 10}\"",
                    "<min_rounds_required_to_pass> - Minimum correct rounds required to complete this stage and unlock the chest. Can be skipped. Example with skipping: \"{0; minecraft:chests/simple_dungeon; }\"",
                    "General Example: { 0; minecraft:chests/simple_dungeon; 10 }",
                    "Default: {}"})
            public String[] perDimensionConfigs = new String[]{};

            @Config.LangKey("config.lootgames.gol.stage.min_items")
            @Config.Comment({"Minimum amount of items to be generated in chest. Won't be applied, if count of items in bound loot table are less than it. If min and max are set to -1, the limits will be disabled.",
                    "Default: Stage 1 -> {2}, Stage 2 -> {4}, Stage 3 -> {6}, Stage 4 -> {8}"
            })
            @Config.RangeInt(min = -1, max = 256)
            public int minItems;

            @Config.LangKey("config.lootgames.gol.stage.max_items")
            @Config.Comment({"Maximum amount of items to be generated in chest. If this is set to -1, max limit will be disabled.",
                    "Default: Stage 1 -> {4}, Stage 2 -> {6}, Stage 3 -> {8}, Stage 4 -> {10}"
            })
            @Config.RangeInt(min = -1, max = 256)
            public int maxItems;
            @Config.Ignore
            private ResourceLocation lootTableRL;

            public Stage(int minRoundsRequiredToPass, boolean randomizeSequence, int displayTime, String lootTable, int minItems, int maxItems) {
                this.minRoundsRequiredToPass = minRoundsRequiredToPass;
                this.randomizeSequence = randomizeSequence;
                this.displayTime = displayTime;
                this.lootTable = lootTable;
                this.minItems = minItems;
                this.maxItems = maxItems;
            }

            private HashMap<Integer, DimConfig> dimensionsConfigsMap;

            private void parseLootTable() {
                lootTableRL = new ResourceLocation(lootTable);
            }

            private void parseDimConfigs() {
                dimensionsConfigsMap = new HashMap<>();

                for (String entry : perDimensionConfigs) {
                    String[] config = entry.split(";");
                    for (int i = 0; i < config.length; i++) {
                        config[i] = config[i].trim();
                    }

                    if (config.length == 3) {
                        if (IntHelper.isInt(config[0])) {
                            int dim = Integer.parseInt(config[0]);

                            if (dimensionsConfigsMap.containsKey(dim)) {
                                LootGames.logHelper.error("Invalid dimension configs entry found: {}. Dimension ID is already defined.", entry);
                                continue;
                            }

                            int minRounds;
                            if (!config[2].isEmpty() && IntHelper.isInt(config[2])) {
                                minRounds = Integer.parseInt(config[2]);
                                if (minRounds < 1 || minRounds > 256) {
                                    LootGames.logHelper.error("Invalid dimension configs entry found: {}. Min Rounds Required To Pass must be an Integer from 1 to 256 or an empty string.", entry);
                                    continue;
                                }
                            } else if (config[2].isEmpty()) {
                                minRounds = this.minRoundsRequiredToPass;
                            } else {
                                LootGames.logHelper.error("Invalid dimension configs entry found: {}. Min Rounds Required To Pass must be an Integer or an empty string.", entry);
                                continue;
                            }

                            String lootTableDim = lootTable;
                            if (!config[1].isEmpty()) {
                                lootTableDim = config[1];
                            }

                            dimensionsConfigsMap.put(dim, new DimConfig(lootTableDim, minRounds));
                        } else {
                            LootGames.logHelper.error("Invalid dimension configs entry found: {}. Dimension ID must be an Integer.", entry);
                        }
                    } else {
                        LootGames.logHelper.error("Invalid dimension configs entry found: {}. Syntax is <dimension_id>; <loottable_resourcelocation>; <min_rounds_required_to_pass> ", entry);
                    }

                }
            }

            public DimConfig getDimConfig(int dimensionID) {
                return dimensionsConfigsMap.get(dimensionID);
            }

            public int getMinRoundsRequiredToPass(int dimensionID) {
                DimConfig cfg = getDimConfig(dimensionID);

                return cfg == null ? minRoundsRequiredToPass : getDimConfig(dimensionID).minRoundRequiredToPass;
            }

            public ResourceLocation getLootTableRL(int dimensionID) {
                DimConfig cfg = getDimConfig(dimensionID);

                return cfg == null ? lootTableRL : getDimConfig(dimensionID).lootTableRL;
            }

            public static class DimConfig {
                public ResourceLocation lootTableRL;
                public int minRoundRequiredToPass;

                DimConfig(String loottable, int minRoundRequiredToPass) {
                    lootTableRL = new ResourceLocation(loottable);
                    this.minRoundRequiredToPass = minRoundRequiredToPass;
                }
            }
        }
    }
}
