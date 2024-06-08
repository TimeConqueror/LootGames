package ru.timeconqueror.lootgames.common.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import ru.timeconqueror.lootgames.common.config.base.RewardConfig.Defaults;
import ru.timeconqueror.lootgames.registry.LGLootTables;

public class StagedRewards {
    public static FourStagedDefaults fourStagedDefaults() {
        return FourStagedDefaults.DEFAULT;
    }

    public static FourStagedDefaults fourStagedDefaults(Defaults stage1, Defaults stage2, Defaults stage3, Defaults stage4) {
        return new FourStagedDefaults(stage1, stage2, stage3, stage4);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FourStagedDefaults {
        private static final FourStagedDefaults DEFAULT = new FourStagedDefaults(
                Defaults.builder()
                        .lootTable(LGLootTables.REWARD_CHEST_1)
                        .build(),
                Defaults.builder()
                        .lootTable(LGLootTables.REWARD_CHEST_2)
                        .build(),
                Defaults.builder()
                        .lootTable(LGLootTables.REWARD_CHEST_3)
                        .build(),
                Defaults.builder()
                        .lootTable(LGLootTables.REWARD_CHEST_4)
                        .build()
        );

        private final Defaults stage1;
        private final Defaults stage2;
        private final Defaults stage3;
        private final Defaults stage4;
    }
}
