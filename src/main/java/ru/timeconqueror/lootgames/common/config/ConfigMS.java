package ru.timeconqueror.lootgames.common.config;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.common.config.Config;
import ru.timeconqueror.timecore.api.common.config.ConfigSection;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;
import ru.timeconqueror.timecore.util.ParseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigMS extends Config {
    public ForgeConfigSpec.IntValue DETONATION_TIME;
    public ForgeConfigSpec.IntValue ATTEMPT_COUNT;

    public StageConfig STAGE_1;
    public StageConfig STAGE_2;
    public StageConfig STAGE_3;
    public StageConfig STAGE_4;

    public ConfigMS(ModConfig.@NotNull Type type, @NotNull String key, @Nullable String comment) {
        super(type, key, comment);
    }

    @Override
    public void setup(ImprovedConfigBuilder builder) {
        DETONATION_TIME = builder.comment("The time until bombs start to explode. Represented in ticks.")
                .defineInRange("detonation_time", 3 * 20, 0, 600);
        ATTEMPT_COUNT = builder.comment("It represents the number of attempts the player has to beat the game successfully.")
                .defineInRange("attempt_count", 3, 1, Integer.MAX_VALUE);

        String sorryMsg = "\nIgnore stage prefix, it is here because of new forge config system knows nothing about property ordering. Waiting for fix... :c";

        STAGE_1 = new StageConfig("y_stage_1", new StageConfig.DefaultData(6, 20, "minecraft:chests/simple_dungeon", 15, 15));
        builder.addAndSetupSection(STAGE_1, "stage", "Regulates characteristics of stage 1." + sorryMsg);
        STAGE_2 = new StageConfig("y_stage_2", new StageConfig.DefaultData(7, 30, "minecraft:chests/desert_pyramid", -1, -1));
        builder.addAndSetupSection(STAGE_2, "stage", "Regulates characteristics of stage 2." + sorryMsg);
        STAGE_3 = new StageConfig("z_stage_3", new StageConfig.DefaultData(8, 42, "minecraft:chests/nether_bridge", -1, -1));
        builder.addAndSetupSection(STAGE_3, "stage", "Regulates characteristics of stage 3." + sorryMsg);
        STAGE_4 = new StageConfig("q_stage_4", new StageConfig.DefaultData(9, 68, "minecraft:chests/end_city_treasure", -1, -1));
        builder.addAndSetupSection(STAGE_4, "stage", "Regulates characteristics of stage 4." + sorryMsg);
    }

    /**
     * @param index - 1-4 (inclusive)
     *              Can return true, if index will be out of bounds.
     * @throws RuntimeException if stage config was not found for provided index.
     */
    public StageConfig getStageByIndex(int index) {
        switch (index) {
            case 1:
                return STAGE_1;
            case 2:
                return STAGE_2;
            case 3:
                return STAGE_3;
            case 4:
                return STAGE_4;
            default:
                throw new RuntimeException("Provided unknown stage config index " + index + ", please contact with mod author.");
        }
    }

    @Override
    public @NotNull String getRelativePath() {
        return LGConfigs.resolve("games/minesweeper.toml");
    }

    public static class StageConfig extends ConfigSection {
        public ForgeConfigSpec.IntValue BOMB_COUNT;
        public ForgeConfigSpec.IntValue MIN_ITEMS;
        public ForgeConfigSpec.IntValue MAX_ITEMS;
        private ForgeConfigSpec.IntValue BOARD_RADIUS_CFG;
        private ForgeConfigSpec.ConfigValue<String> DEF_LOOT_TABLE_CFG;
        private ForgeConfigSpec.ConfigValue<List<? extends String>> PER_DIM_CFG;

        private final DefaultData defData;
        private ResourceLocation defaultLootTable;
        private HashMap<Integer, ResourceLocation> dimensionsConfigsMap;

        public StageConfig(String key, DefaultData defData) {
            super(key, null);
            this.defData = defData;
        }

        @Override
        public void setup(ImprovedConfigBuilder builder) {
            BOARD_RADIUS_CFG = builder.comment("The radius of Minesweeper board.")
                    .defineInRange("board_radius", defData.boardRadius, 2, 9);
            BOMB_COUNT = builder.comment("The amount of bombs on the board.")
                    .defineInRange("bomb_count", defData.bombCount, 1, Integer.MAX_VALUE);
            DEF_LOOT_TABLE_CFG = builder.comment("Name of the loottable, items from which will be generated in the chest of this stage. This can be adjusted per-Dimension in S:DimensionalConfig.")
                    .define("loot_table", defData.lootTable);
            MIN_ITEMS = builder.comment("Minimum amount of item stacks to be generated in chest. Won't be applied, if count of items in bound loot table are less than it. If min and max are set to -1, the limits will be disabled.")
                    .defineInRange("min_items", defData.minItems, -1, 256);
            MAX_ITEMS = builder.comment("Maximum amount of item stacks to be generated in chest. If this is set to -1, max limit will be disabled.")
                    .defineInRange("max_items", defData.maxItems, -1, 256);

            PER_DIM_CFG = builder.comment("Here you can add different loottables to each dimension. If dimension isn't in this list, then game will take default loottable for this stage.",
                    "Syntax: <dimension_id>; <loottable_name>",
                    "<loottable_name> - The loottable name for the chest in this stage.",
                    "General Example: [ \"0; minecraft:chests/simple_dungeon\" ]")
                    .defineList("per_dim_configs", new ArrayList<>(), o -> true);//TODO someday nightconfig will fix validators...
        }

        @Override
        public void onEveryLoad(ModConfig.ModConfigEvent configEvent) {
            super.onEveryLoad(configEvent);

            int bombCount = BOMB_COUNT.get();
            int boardSize = getBoardSize();

            if (bombCount > boardSize * boardSize - 1) {
                LootGames.LOGGER.warn("Bomb count must be strictly less than amount of game fields. " +
                        "Current values: bomb count = {}, field count: {} (board size = {}, board radius = {})\n " +
                        "Bomb count was switched to {}.", bombCount, boardSize * boardSize, boardSize, boardSize * boardSize - 2, BOARD_RADIUS_CFG.get());
                BOMB_COUNT.set(boardSize * boardSize - 2); // at least 1 field with no bomb
            }

            defaultLootTable = new ResourceLocation(DEF_LOOT_TABLE_CFG.get());

            parseDimConfigs();
        }

        public int getBoardSize() {
            return BOARD_RADIUS_CFG.get() * 2 + 1;
        }

        private void parseDimConfigs() {
            dimensionsConfigsMap = new HashMap<>();

            for (String entry : PER_DIM_CFG.get()) {
                String[] config = entry.split(";");
                for (int i = 0; i < config.length; i++) {
                    config[i] = config[i].trim();
                }

                if (config.length == 2) {
                    if (ParseUtils.isInt(config[0])) {
                        int dim = Integer.parseInt(config[0]);

                        if (dimensionsConfigsMap.containsKey(dim)) {
                            LootGames.LOGGER.error("Invalid dimension configs entry found: {}. Dimension ID is already defined. Skipping entry...", entry);
                            continue;
                        }

                        String lootTableDim;
                        if (!config[1].isEmpty()) {
                            lootTableDim = config[1];
                        } else {
                            LootGames.LOGGER.error("Invalid dimension configs entry found: {}. LootTable ResourceLocation must not be an empty string. Skipping entry...", entry);
                            continue;
                        }

                        dimensionsConfigsMap.put(dim, new ResourceLocation(lootTableDim));
                    } else {
                        LootGames.LOGGER.error("Invalid dimension configs entry found: {}. Dimension ID must be an Integer.  Skipping entry...", entry);
                    }
                } else {
                    LootGames.LOGGER.error("Invalid dimension configs entry found: {}. Syntax is <dimension_id>; <loottable_resourcelocation>.  Skipping entry...", entry);
                }
            }
        }

        public ResourceLocation getLootTable(ResourceLocation dimensionId) {
            ResourceLocation lootTableDim = dimensionsConfigsMap.get(dimensionId);

            return lootTableDim == null ? defaultLootTable : lootTableDim;
        }

        private static class DefaultData {
            private final int boardRadius;
            private final int bombCount;
            private final String lootTable;
            private final int minItems;
            private final int maxItems;

            public DefaultData(int boardRadius, int bombCount, String lootTable, int minItems, int maxItems) {
                this.boardRadius = boardRadius;
                this.bombCount = bombCount;
                this.lootTable = lootTable;
                this.minItems = minItems;
                this.maxItems = maxItems;
            }
        }
    }
}
