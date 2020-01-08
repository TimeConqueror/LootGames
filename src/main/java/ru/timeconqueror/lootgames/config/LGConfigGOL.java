package ru.timeconqueror.lootgames.config;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.auxiliary.IntHelper;

import java.util.HashMap;

@Mod.EventBusSubscriber
@Config.LangKey("config.lootgames.category.gol")
@Config(modid = LootGames.MOD_ID, name = "lootgames/games/game_of_light")
public class LGConfigGOL {
    @Config.LangKey("config.lootgames.common.stage.1")
    @Config.Comment("Regulates characteristics of stage 1.")
    public static Stage stage1 = new Stage(5, false, 24, "minecraft:chests/simple_dungeon", 15, 15);

    @Config.LangKey("config.lootgames.common.stage.2")
    @Config.Comment("Regulates characteristics of stage 2.")
    public static Stage stage2 = new Stage(10, false, 16, "minecraft:chests/desert_pyramid", -1, -1);

    @Config.LangKey("config.lootgames.common.stage.3")
    @Config.Comment("Regulates characteristics of stage 3.")
    public static Stage stage3 = new Stage(15, false, 12, "minecraft:chests/nether_bridge", -1, -1);

    @Config.LangKey("config.lootgames.common.stage.4")
    @Config.Comment("Regulates characteristics of stage 4.")
    public static Stage stage4 = new Stage(16, true, 10, "minecraft:chests/end_city_treasure", -1, -1);

    @Config.LangKey("config.lootgames.gol.start_digits")
    @Config.Comment({"How many digits should be randomly chosen and shown at game-start?",
            "Default: 2."
    })
    public static int startDigitAmount = 2;

    @Config.LangKey("config.lootgames.gol.max_attempts")
    @Config.Comment({"How many attempts does a player have? The structure will fail after max attempt count is passed, or the player will win if he beat at least 1 stage.",
            "Default: 3."
    })//TODO why attempt count is 4???
    public static int maxAttempts = 3;

    @Config.LangKey("config.lootgames.gol.expand_field_stage")
    @Config.Comment({"At which stage should the playfield become a full 3x3 pattern? Set 0 to disable and keep the 4-block size; set 1 to always start with 3x3.",
            "Default: 2."
    })
    @Config.RangeInt(min = 0, max = 4)
    public static int expandFieldAtStage = 2;

    @Config.LangKey("config.lootgames.gol.on_fail_explode")
    @Config.Comment({"Enable or disable struct exploding on max failed attempts.",
            "Default: true"
    })
    public static boolean onFailExplode = true;

    @Config.LangKey("config.lootgames.gol.on_fail_zombies")
    @Config.Comment({"Enable or disable struct filling with zombies on max failed attempts.",
            "Default: true"
    })
    public static boolean onFailZombies = true;

    @Config.LangKey("config.lootgames.gol.on_fail_lava")
    @Config.Comment({"Enable or disable struct filling with lava on max failed attempts.",
            "Default: true"
    })
    public static boolean onFailLava = true;

    @Config.LangKey("config.lootgames.gol.timeout")
    @Config.Comment({"How long does it take to timeout a game? Value is in seconds. How long does it take to timeout a game? Value is in seconds. If player has been inactive for given time, the game will go to sleep. The next player can start the game from the beginning.",
            "Default: 60"
    })
    public static int timeout = 60;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(LootGames.MOD_ID)) {
            ConfigManager.sync(LootGames.MOD_ID, Config.Type.INSTANCE);
            init();
        }
    }

    private static void init() {
        for (int i = 1; i <= 4; i++) {
            getStageByIndex(i).parseLootTable();
            getStageByIndex(i).parseDimConfigs();
        }
    }

    /**
     * @param index - 1-4 (inclusive)
     *              Can return true, if index will be out of bounds.
     */
    public static Stage getStageByIndex(int index) {
        switch (index) {
            case 1:
                return stage1;
            case 2:
                return stage2;
            case 3:
                return stage3;
            case 4:
                return stage4;
            default:
                throw new RuntimeException("Provided unknown stage index " + index + ", please contact with mod author.");
        }
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

        @Config.LangKey("config.lootgames.common.stage.loot_table")
        @Config.Comment({"Name of the loottable, items from which will be generated in the chest of this stage. This can be adjusted per-Dimension in S:DimensionalConfig.",
                "Default: Stage 1 -> minecraft:chests/simple_dungeon, Stage 2 -> minecraft:chests/desert_pyramid, Stage 3 -> minecraft:chests/nether_bridge, Stage 4 -> minecraft:chests/end_city_treasure"
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

        @Config.LangKey("config.lootgamescommon..stage.min_items")
        @Config.Comment({"Minimum amount of items to be generated in chest. Won't be applied, if count of items in bound loot table are less than it. If min and max are set to -1, the limits will be disabled.",
                "Default: Stage 1 -> {15}, Stage 2 -> {-1}, Stage 3 -> {-1}, Stage 4 -> {-1}"
        })
        @Config.RangeInt(min = -1, max = 256)
        public int minItems;

        @Config.LangKey("config.lootgames.common.stage.max_items")
        @Config.Comment({"Maximum amount of items to be generated in chest. If this is set to -1, max limit will be disabled.",
                "Default: Stage 1 -> {15}, Stage 2 -> {-1}, Stage 3 -> {-1}, Stage 4 -> {-1}"
        })
        @Config.RangeInt(min = -1, max = 256)
        public int maxItems;

        private ResourceLocation lootTableRL;
        private HashMap<Integer, DimConfig> dimensionsConfigsMap;

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

        private DimConfig getDimConfig(int dimensionID) {
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

        private static class DimConfig {
            private ResourceLocation lootTableRL;
            private int minRoundRequiredToPass;

            DimConfig(String loottable, int minRoundRequiredToPass) {
                lootTableRL = new ResourceLocation(loottable);
                this.minRoundRequiredToPass = minRoundRequiredToPass;
            }
        }
    }
}
