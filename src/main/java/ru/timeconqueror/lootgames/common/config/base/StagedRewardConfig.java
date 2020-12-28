package ru.timeconqueror.lootgames.common.config.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.common.config.StagedRewards;
import ru.timeconqueror.timecore.api.common.config.ConfigSection;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;
import ru.timeconqueror.timecore.api.util.Requirements;

public class StagedRewardConfig extends ConfigSection {
    private final RewardConfig[] configs;

    public static FourStagedRewardConfig fourStaged(@NotNull String key, @Nullable String comment, StagedRewards.FourStagedDefaults fourStagedDefaults) {
        return new FourStagedRewardConfig(key, comment, fourStagedDefaults);
    }

    protected StagedRewardConfig(@NotNull String key, @Nullable String comment, RewardConfig... rewardConfigs) {
        super(key, comment);
        this.configs = rewardConfigs;
    }

    public RewardConfig getStageByIndex(int index) {
        Requirements.inRangeInclusive(index, 0, size() - 1);
        return configs[index];
    }

    public int size() {
        return configs.length;
    }

    @Override
    public void setup(ImprovedConfigBuilder builder) {
        for (int i = 0; i < configs.length; i++) {
            RewardConfig config = configs[i];
            builder.addAndSetupSection(config, "Rewards for stage " + (i + 1));
        }
    }

    public static class FourStagedRewardConfig extends StagedRewardConfig {
        private FourStagedRewardConfig(@NotNull String key, @Nullable String comment, StagedRewards.FourStagedDefaults fourStagedDefaults) {
            super(key,
                    comment,
                    new RewardConfig("stage_1", fourStagedDefaults.getStage1()),
                    new RewardConfig("stage_2", fourStagedDefaults.getStage2()),
                    new RewardConfig("stage_3", fourStagedDefaults.getStage3()),
                    new RewardConfig("stage_4", fourStagedDefaults.getStage4()));
        }
    }
}
