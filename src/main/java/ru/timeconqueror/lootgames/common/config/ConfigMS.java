package ru.timeconqueror.lootgames.common.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.config.ConfigMS.Snapshot.StageSnapshot;
import ru.timeconqueror.timecore.api.common.config.Config;
import ru.timeconqueror.timecore.api.common.config.ConfigSection;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;
import ru.timeconqueror.timecore.api.util.CodecUtils;
import ru.timeconqueror.timecore.api.util.ParseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigMS extends Config {
    public ForgeConfigSpec.IntValue DETONATION_TIME;
    public ForgeConfigSpec.IntValue ATTEMPT_COUNT;

    public StageConfig stage1;
    public StageConfig stage2;
    public StageConfig stage3;
    public StageConfig stage4;

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

        stage1 = new StageConfig("y_stage_1", new StageConfig.DefaultData(6, 20, "minecraft:chests/simple_dungeon", 15, 15));
        builder.addAndSetupSection(stage1, "stage", "Regulates characteristics of stage 1." + sorryMsg);
        stage2 = new StageConfig("y_stage_2", new StageConfig.DefaultData(7, 30, "minecraft:chests/desert_pyramid", -1, -1));
        builder.addAndSetupSection(stage2, "stage", "Regulates characteristics of stage 2." + sorryMsg);
        stage3 = new StageConfig("z_stage_3", new StageConfig.DefaultData(8, 42, "minecraft:chests/nether_bridge", -1, -1));
        builder.addAndSetupSection(stage3, "stage", "Regulates characteristics of stage 3." + sorryMsg);
        stage4 = new StageConfig("q_stage_4", new StageConfig.DefaultData(9, 68, "minecraft:chests/end_city_treasure", -1, -1));
        builder.addAndSetupSection(stage4, "stage", "Regulates characteristics of stage 4." + sorryMsg);
    }

    /**
     * @param index - 1-4 (inclusive)
     *              Can return true, if index will be out of bounds.
     * @throws RuntimeException if stage config was not found for provided index.
     */
    public StageConfig getStageByIndex(int index) {
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
                throw new RuntimeException("Provided unknown stage config index " + index + ", please contact with mod author.");
        }
    }

    @Override
    public @NotNull String getRelativePath() {
        return LGConfigs.resolve("games/minesweeper.toml");
    }

    public Snapshot snapshot() {
        return new Snapshot(stage1.snapshot(), stage2.snapshot(), stage3.snapshot(), stage4.snapshot());
    }

    public static class StageConfig extends ConfigSection {
        public ForgeConfigSpec.IntValue minItems;
        public ForgeConfigSpec.IntValue maxItems;
        private ForgeConfigSpec.IntValue bombCount;
        private ForgeConfigSpec.IntValue boardRadius;
        private ForgeConfigSpec.ConfigValue<String> defLootTableCfg;
        private ForgeConfigSpec.ConfigValue<List<? extends String>> perDimCfg;

        private final DefaultData defData;
        private ResourceLocation defaultLootTable;
        private HashMap<Integer, ResourceLocation> dimensionsConfigsMap;

        public StageConfig(String key, DefaultData defData) {
            super(key, null);
            this.defData = defData;
        }

        public StageSnapshot snapshot() {
            return new StageSnapshot(bombCount.get(), getBoardSize());
        }

        @Override
        public void setup(ImprovedConfigBuilder builder) {
            boardRadius = builder.comment("The radius of Minesweeper board.")
                    .defineInRange("board_radius", defData.boardRadius, 2, 9);
            bombCount = builder.comment("The amount of bombs on the board.")
                    .defineInRange("bomb_count", defData.bombCount, 1, Integer.MAX_VALUE);
            defLootTableCfg = builder.comment("Name of the loottable, items from which will be generated in the chest of this stage. This can be adjusted per-Dimension in S:DimensionalConfig.")
                    .define("loot_table", defData.lootTable);
            minItems = builder.comment("Minimum amount of item stacks to be generated in chest. Won't be applied, if count of items in bound loot table are less than it. If min and max are set to -1, the limits will be disabled.")
                    .defineInRange("min_items", defData.minItems, -1, 256);
            maxItems = builder.comment("Maximum amount of item stacks to be generated in chest. If this is set to -1, max limit will be disabled.")
                    .defineInRange("max_items", defData.maxItems, -1, 256);

            perDimCfg = builder.comment("Here you can add different loottables to each dimension. If dimension isn't in this list, then game will take default loottable for this stage.",
                    "Syntax: <dimension_id>; <loottable_name>",
                    "<loottable_name> - The loottable name for the chest in this stage.",
                    "General Example: [ \"0; minecraft:chests/simple_dungeon\" ]")
                    .defineList("per_dim_configs", new ArrayList<>(), o -> true);//TODO someday nightconfig will fix validators...
        }

        @Override
        public void onEveryLoad(ModConfig.ModConfigEvent configEvent) {
            super.onEveryLoad(configEvent);

            int bombCount = this.bombCount.get();
            int boardSize = getBoardSize();

            if (bombCount > boardSize * boardSize - 1) {
                LootGames.LOGGER.warn("Bomb count must be strictly less than amount of game fields. " +
                        "Current values: bomb count = {}, field count: {} (board size = {}, board radius = {})\n " +
                        "Bomb count was switched to {}.", bombCount, boardSize * boardSize, boardSize, boardSize * boardSize - 2, boardRadius.get());
                this.bombCount.set(boardSize * boardSize - 2); // at least 1 field with no bomb
            }

            defaultLootTable = new ResourceLocation(defLootTableCfg.get());

            parseDimConfigs();
        }

        public int getBoardSize() {
            return boardRadius.get() * 2 + 1;
        }

        private void parseDimConfigs() {
            dimensionsConfigsMap = new HashMap<>();

            for (String entry : perDimCfg.get()) {
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

        //TODO now i don't use it, but i must to
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

    public static class Snapshot {
        public static final Codec<Snapshot> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        StageSnapshot.CODEC.fieldOf("stage_1").forGetter(o -> o.stage1),
                        StageSnapshot.CODEC.fieldOf("stage_2").forGetter(o -> o.stage2),
                        StageSnapshot.CODEC.fieldOf("stage_3").forGetter(o -> o.stage3),
                        StageSnapshot.CODEC.fieldOf("stage_4").forGetter(o -> o.stage4)
                ).apply(instance, Snapshot::new));


        private final StageSnapshot stage1;
        private final StageSnapshot stage2;
        private final StageSnapshot stage3;
        private final StageSnapshot stage4;

        public Snapshot(StageSnapshot stage1, StageSnapshot stage2, StageSnapshot stage3, StageSnapshot stage4) {
            this.stage1 = stage1;
            this.stage2 = stage2;
            this.stage3 = stage3;
            this.stage4 = stage4;
        }

        public static INBT serialize(Snapshot snapshot) {
            return CodecUtils.encodeStrictly(CODEC, CodecUtils.NBT_OPS, snapshot);
        }

        public static Snapshot deserialize(INBT serialized) {
            return CodecUtils.decodeStrictly(CODEC, CodecUtils.NBT_OPS, serialized);
        }

        public StageSnapshot getStage1() {
            return stage1;
        }

        public StageSnapshot getStage2() {
            return stage2;
        }

        public StageSnapshot getStage3() {
            return stage3;
        }

        public StageSnapshot getStage4() {
            return stage4;
        }

        public static class StageSnapshot {
            public static final Codec<StageSnapshot> CODEC = RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.INT.fieldOf("bomb_count").forGetter(stageSnapshot -> stageSnapshot.bombCount),
                            Codec.INT.fieldOf("board_size").forGetter(stageSnapshot -> stageSnapshot.boardSize)
                    ).apply(instance, StageSnapshot::new));

            private final int bombCount;
            private final int boardSize;

            public StageSnapshot(int bombCount, int boardSize) {
                this.bombCount = bombCount;
                this.boardSize = boardSize;
            }

            public int getBoardSize() {
                return boardSize;
            }

            public int getBombCount() {
                return bombCount;
            }
        }

        /**
         * @param index - 1-4 (inclusive)
         *              Can return true, if index will be out of bounds.
         * @throws RuntimeException if stage config was not found for provided index.
         */
        public StageSnapshot getStageByIndex(int index) {
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
                    throw new RuntimeException("Provided unknown stage snapshot index " + index + ", please contact with mod author.");
            }
        }
    }
}
