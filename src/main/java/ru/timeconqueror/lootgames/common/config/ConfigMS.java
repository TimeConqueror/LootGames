package ru.timeconqueror.lootgames.common.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.INBT;
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

        stage1 = new StageConfig("stage_1", new StageConfig.DefaultData(6, 20));
        builder.addAndSetupSection(stage1, "stage", "Regulates characteristics of stage 1." + sorryMsg);
        stage2 = new StageConfig("stage_2", new StageConfig.DefaultData(7, 30));
        builder.addAndSetupSection(stage2, "stage", "Regulates characteristics of stage 2." + sorryMsg);
        stage3 = new StageConfig("stage_3", new StageConfig.DefaultData(8, 42));
        builder.addAndSetupSection(stage3, "stage", "Regulates characteristics of stage 3." + sorryMsg);
        stage4 = new StageConfig("stage_4", new StageConfig.DefaultData(9, 68));
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
        private ForgeConfigSpec.IntValue bombCount;
        private ForgeConfigSpec.IntValue boardRadius;

        private final DefaultData defData;

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
        }

        public int getBoardSize() {
            return boardRadius.get() * 2 + 1;
        }

        private static class DefaultData {
            private final int boardRadius;
            private final int bombCount;

            public DefaultData(int boardRadius, int bombCount) {
                this.boardRadius = boardRadius;
                this.bombCount = bombCount;
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
