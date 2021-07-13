package ru.timeconqueror.lootgames.common.config;

import net.minecraftforge.common.config.Configuration;
import ru.timeconqueror.timecore.api.common.config.Config;

import java.util.EnumSet;

public class ConfigGOL extends Config {
    public int startDigitAmount;
    public int attemptCount;
    public int expandFieldAtStage;
    public boolean explodeOnFail;
    public boolean zombiesOnFail;
    public boolean lavaOnFail;
    public int timeout;

    public StageConfig stage1 = new StageConfig("stage_1", "Regulates characteristics of stage 1.", new DefaultData(5, false, 24));
    public StageConfig stage2 = new StageConfig("stage_2", "Regulates characteristics of stage 2.", new DefaultData(5, false, 16));
    public StageConfig stage3 = new StageConfig("stage_3", "Regulates characteristics of stage 3.", new DefaultData(5, false, 12));
    public StageConfig stage4 = new StageConfig("stage_4", "Regulates characteristics of stage 4.", new DefaultData(5, true, 12));

    public ConfigGOL() {
        super("game_of_light");
    }

    @Override
    public void init() {
        startDigitAmount = config.getInt("start_digit_amount", getKey(), 2, 1, Integer.MAX_VALUE, "Regulates how many digits should be randomly chosen and shown at game-start.");
        attemptCount = config.getInt("attempt_count", getKey(), 3, 1, Integer.MAX_VALUE, "It represents the number of attempts the player has to beat the game successfully.");
        expandFieldAtStage = config.getInt("expand_field_at_stage", getKey(), 2, 0, 4, "At which stage should the playfield become a full 3x3 pattern?\nSet 0 to disable and keep the 4-block size; set 1 to always start with 3x3.");
        explodeOnFail = config.getBoolean("explode_on_fail", getKey(), true, "Enables or disables structure exploding on max failed attempts.");
        zombiesOnFail = config.getBoolean("zombies_on_fail", getKey(), true, "Enables or disables structure filling with zombies on max failed attempts.");
        lavaOnFail = config.getBoolean("lava_on_fail", getKey(), true, "Enables or disables structure filling with lava on max failed attempts.");
        timeout = config.getInt("timeout", getKey(), 30, 10, Integer.MAX_VALUE, "How long does it take to timeout a game? Value is in seconds.\nIf player has been inactive for given time, the game will go to sleep. The next player can start the game from the beginning.");

        stage1.init(config);
        stage2.init(config);
        stage3.init(config);
        stage4.init(config);

        config.setCategoryComment(getKey(), "Regulates \"Game of Light\" minigame.");
    }

    @Override
    public String getRelativePath() {
        return LGConfigs.resolve("games/" + getKey());
    }

    public EnumSet<Fail> getAllowedFails() {
        EnumSet<Fail> fails = EnumSet.noneOf(Fail.class);

        if (zombiesOnFail) {
            fails.add(Fail.ZOMBIES);
        }
        if (explodeOnFail) {
            fails.add(Fail.EXPLOSION);
        }
        if (lavaOnFail) {
            fails.add(Fail.LAVA);
        }

        return fails;
    }

    /**
     * @param index - 0-3 (inclusive)
     * @throws RuntimeException if stage config was not found for provided index.
     */
    public StageConfig getStageByIndex(int index) {
        if (index == 0) {
            return stage1;
        } else if (index == 1) {
            return stage2;
        } else if (index == 2) {
            return stage3;
        } else {
            return stage4;
        }
    }

    public static class StageConfig {
        public int rounds;
        public boolean randomizeSequence;
        public int displayTime;

        private final DefaultData defData;
        private final String key;
        private final String comment;

        public StageConfig(String key, String comment, DefaultData defData) {
            this.key = key;
            this.comment = comment;
            this.defData = defData;
        }

        private void init(Configuration config) {
            rounds = config.getInt("round_count", key, defData.rounds, 1, 256, "Round count required to complete this stage and unlock leveled reward.");
            randomizeSequence = config.getBoolean("randomize_sequence", key, defData.randomizeSequence, "If true, the pattern will randomize on each round in this stage.");
            displayTime = config.getInt("display_time", key, defData.displayTime, 2, 40, "Amount of time (in ticks; 20 ticks = 1s) for which the symbol will be displayed.");

            config.setCategoryComment(key, comment);
        }
    }

    private static class DefaultData {
        private final int rounds;
        private final boolean randomizeSequence;
        private final int displayTime;

        public DefaultData(int rounds, boolean randomizeSequence, int displayTime) {
            this.rounds = rounds;
            this.randomizeSequence = randomizeSequence;
            this.displayTime = displayTime;
        }
    }

    public enum Fail {
        ZOMBIES, EXPLOSION, LAVA
    }
}
