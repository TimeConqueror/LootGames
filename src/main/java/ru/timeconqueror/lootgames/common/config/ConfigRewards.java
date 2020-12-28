package ru.timeconqueror.lootgames.common.config;

import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.common.config.base.StagedRewardConfig;
import ru.timeconqueror.lootgames.common.config.base.StagedRewardConfig.FourStagedRewardConfig;
import ru.timeconqueror.timecore.api.common.config.Config;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;

public class ConfigRewards extends Config {
    public static final ConfigRewards INSTANCE = new ConfigRewards();

    public FourStagedRewardConfig minesweeper = StagedRewardConfig.fourStaged(LGConfigs.MINESWEEPER.getKey(), "Minesweeper Rewards", StagedRewards.fourStagedDefaults());
    public FourStagedRewardConfig gol = StagedRewardConfig.fourStaged(LGConfigs.GOL.getKey(), "Game of Light Rewards", StagedRewards.fourStagedDefaults());

    private ConfigRewards() {
        super(ModConfig.Type.COMMON, "rewards", "Regulates game rewards");
    }

    @Override
    public void setup(ImprovedConfigBuilder builder) {
        builder.addAndSetupSection(minesweeper);
        builder.addAndSetupSection(gol);
    }

    @Override
    public @NotNull String getRelativePath() {
        return LGConfigs.resolve(getKey() + ".toml");
    }
}
