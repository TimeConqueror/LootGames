package ru.timeconqueror.lootgames.common.config;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.config.base.StagedRewardConfig;

public class LGConfigs {
    public static final ConfigGeneral GENERAL = new ConfigGeneral();
    public static final ConfigMS MINESWEEPER = new ConfigMS();
    public static final ConfigGOL GOL = new ConfigGOL();
    public static final StagedRewardConfig.FourStagedRewardConfig REWARDS_MINESWEEPER = StagedRewardConfig.fourStaged(LGConfigs.MINESWEEPER.getKey(), StagedRewards.fourStagedDefaults());
    public static final StagedRewardConfig.FourStagedRewardConfig REWARDS_GOL = StagedRewardConfig.fourStaged(LGConfigs.GOL.getKey(), StagedRewards.fourStagedDefaults());

    public static String resolve(String path) {
        return LootGames.MODID + "/" + path + ".cfg";
    }

    public static void load() {
        GENERAL.load();
        MINESWEEPER.load();
        GOL.load();
        REWARDS_MINESWEEPER.load();
        REWARDS_GOL.load();
    }
}