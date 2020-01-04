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
import java.util.Objects;

@Mod.EventBusSubscriber
@Config.LangKey("config.lootgames.category.ms")
@Config(modid = LootGames.MOD_ID, name = "lootgames/games/minesweeper")
public class LGConfigMinesweeper {
    @Config.LangKey("config.lootgames.ms.detonation_time")
    @Config.Comment({"The time until bombs start to explode. Represented in ticks.", "Default: 60"})
    public static int detonationTimeInTicks = 3 * 20;

    @Config.LangKey("config.lootgames.ms.attempt_count")
    @Config.Comment({"It represents the number of attempts the player has to beat the game successfully.", "Default: 3"})
    public static int attemptCount = 3;

    @Config.LangKey("config.lootgames.common.stage.1")
    @Config.Comment("Regulates characteristics of stage 1.")
    public static Stage stage1 = new Stage(13, 28, "minecraft:chests/simple_dungeon", 15, 15);
    @Config.LangKey("config.lootgames.common.stage.2")
    @Config.Comment("Regulates characteristics of stage 2.")
    public static Stage stage2 = new Stage(15, 35, "minecraft:chests/jungle_temple", -1, -1);
    @Config.LangKey("config.lootgames.common.stage.3")
    @Config.Comment("Regulates characteristics of stage 3.")
    public static Stage stage3 = new Stage(17, 40, "minecraft:chests/desert_pyramid", -1, -1);
    @Config.LangKey("config.lootgames.common.stage.4")
    @Config.Comment("Regulates characteristics of stage 4.")
    public static Stage stage4 = new Stage(19, 50, "minecraft:chests/end_city_treasure", -1, -1);

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(LootGames.MOD_ID)) {
            ConfigManager.sync(LootGames.MOD_ID, Config.Type.INSTANCE);
            init();
        }
    }

    public static void init() {
        for (int i = 1; i <= 4; i++) {
            Objects.requireNonNull(getStage(i)).init();
        }
    }

    public static Stage getStage(int index) {
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
        @Config.LangKey("config.lootgames.ms.board_size")
        @Config.Comment({"The size of Minesweeper board. Accepts only odd numbers. If you set this to even number, then it will be increased by one.",
                "Default: Stage 1 -> {5}, Stage 2 -> {10}, Stage 3 -> {15}, Stage 4 -> {20}"//fixme stage defaults
        })
        @Config.RangeInt(min = 5, max = 19)
        public int boardSize;

        @Config.LangKey("config.lootgames.ms.bomb_count")
        @Config.Comment({"The amount of bombs on the board.",
                "Default: Stage 1 -> {5}, Stage 2 -> {10}, Stage 3 -> {15}, Stage 4 -> {20}"//fixme stage defaults
        })
        @Config.RangeInt(min = 1)
        public int bombCount;

        @Config.LangKey("config.lootgames.common.stage.loot_table")
        @Config.Comment({"Name of the loottable, items from which will be generated in the chest of this stage. This can be adjusted per-Dimension in S:DimensionalConfig.",
                "Default: Stage 1 -> minecraft:chests/simple_dungeon, Stage 2 -> minecraft:chests/jungle_temple, Stage 3 -> minecraft:chests/desert_pyramid, Stage 4 -> minecraft:chests/end_city_treasure"
        })
        public String lootTable;

        @Config.LangKey("config.lootgames.common.stage.min_items")
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

        @Config.LangKey("config.lootgames.ms.stage.dimconfig")
        @Config.Comment({"Here you can add different loottables to each dimension. If dimension isn't in this list, then game will take default loottable for this stage.",
                "Syntax: <dimension_id>; <loottable_name>",
                "<loottable_name> - The loottable name for the chest in this stage.",
                "General Example: { 0; minecraft:chests/simple_dungeon }",
                "Default: {}"})
        public String[] perDimensionConfigs = new String[]{};

        @Config.Ignore
        private ResourceLocation lootTableRL;
        private HashMap<Integer, ResourceLocation> dimensionsConfigsMap;

        public Stage(int boardSize, int bombCount, String lootTable, int minItems, int maxItems) {
            this.boardSize = boardSize;
            this.bombCount = bombCount;
            this.lootTable = lootTable;
            this.minItems = minItems;
            this.maxItems = maxItems;
        }

        public void init() {
            if (boardSize % 2 == 0) {
                boardSize += 1;
            }

            if (bombCount > boardSize * boardSize - 1) {
                LootGames.logHelper.error("Bomb count must be strictly less than amount of game fields. " +
                        "Current values: bomb count = {}, field count: {} (boardSize = {})\n " +
                        "Bomb count will be switched to {}.", bombCount, boardSize * boardSize, boardSize, boardSize * boardSize - 2);
                bombCount = boardSize * boardSize - 2; // at least 1 field with no bomb
            }

            parseLootTable();
            parseDimConfigs();
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

                if (config.length == 2) {
                    if (IntHelper.isInt(config[0])) {
                        int dim = Integer.parseInt(config[0]);

                        if (dimensionsConfigsMap.containsKey(dim)) {
                            LootGames.logHelper.error("Invalid dimension configs entry found: {}. Dimension ID is already defined.", entry);
                            continue;
                        }

                        String lootTableDim;
                        if (!config[1].isEmpty()) {
                            lootTableDim = config[1];
                        } else {
                            LootGames.logHelper.error("Invalid dimension configs entry found: {}. LootTable ResourceLocation must not be an empty string. This setting will be skipped.", entry);
                            continue;
                        }

                        dimensionsConfigsMap.put(dim, new ResourceLocation(lootTableDim));
                    } else {
                        LootGames.logHelper.error("Invalid dimension configs entry found: {}. Dimension ID must be an Integer.", entry);
                    }
                } else {
                    LootGames.logHelper.error("Invalid dimension configs entry found: {}. Syntax is <dimension_id>; <loottable_resourcelocation>", entry);
                }
            }
        }

        public ResourceLocation getLootTableRL(int dimensionID) {
            ResourceLocation lootTableDim = dimensionsConfigsMap.get(dimensionID);

            return lootTableDim == null ? lootTableRL : lootTableDim;
        }
    }
}
