package ru.timeconqueror.lootgames.common.config.base;

import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.common.config.StagedRewards;
import ru.timeconqueror.lootgames.common.config.base.RewardConfig.Names;
import ru.timeconqueror.timecore.api.common.config.Config;

public class StagedRewardConfig extends Config {
    private final RewardConfig[] configs;

    public static FourStagedRewardConfig fourStaged(String key, StagedRewards.FourStagedDefaults fourStagedDefaults) {
        return new FourStagedRewardConfig(key, fourStagedDefaults);
    }

    protected StagedRewardConfig(String key, RewardConfig... rewardConfigs) {
        super(key);
        this.configs = rewardConfigs;
    }

    public RewardConfig getStageByIndex(int index) {
        if (index < 0 || index > size() - 1) {
            throw new IllegalArgumentException("Provided number (=" + index + ") should be positive and less then " + (size() - 1));
        }
        return configs[index];
    }

    public int size() {
        return configs.length;
    }

    @Override
    public void init() {
        for (int i = 0; i < configs.length; i++) {
            RewardConfig rc = configs[i];
            rc.init(config);
            config.setCategoryComment(rc.getKey(), "Rewards for stage " + (i + 1));
        }
    }

    @Override
    public String getRelativePath() {
        return LGConfigs.resolve("rewards/" + getKey());
    }

    public static class FourStagedRewardConfig extends StagedRewardConfig {
        private FourStagedRewardConfig(String key, StagedRewards.FourStagedDefaults fourStagedDefaults) {
            super(key,
                    new RewardConfig(Names.CATEGORY_STAGE_1, fourStagedDefaults.getStage1()),
                    new RewardConfig(Names.CATEGORY_STAGE_2, fourStagedDefaults.getStage2()),
                    new RewardConfig(Names.CATEGORY_STAGE_3, fourStagedDefaults.getStage3()),
                    new RewardConfig(Names.CATEGORY_STAGE_4, fourStagedDefaults.getStage4()));
        }
    }
}
