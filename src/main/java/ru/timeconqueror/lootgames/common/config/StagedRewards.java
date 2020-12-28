package ru.timeconqueror.lootgames.common.config;

import net.minecraft.loot.LootTables;
import ru.timeconqueror.lootgames.common.config.base.RewardConfig.Defaults;

public class StagedRewards {
    public static FourStagedDefaults fourStagedDefaults() {
        return FourStagedDefaults.DEFAULT;
    }

    public static FourStagedDefaults fourStagedDefaults(Defaults stage1, Defaults stage2, Defaults stage3, Defaults stage4) {
        return new FourStagedDefaults(stage1, stage2, stage3, stage4);
    }

    public static class FourStagedDefaults {
        private static final FourStagedDefaults DEFAULT = new FourStagedDefaults(new Defaults(LootTables.SIMPLE_DUNGEON, 15, 15),
                new Defaults(LootTables.DESERT_PYRAMID, -1, -1),
                new Defaults(LootTables.NETHER_BRIDGE, -1, -1),
                new Defaults(LootTables.END_CITY_TREASURE, -1, -1)
        );

        private final Defaults stage1;
        private final Defaults stage2;
        private final Defaults stage3;
        private final Defaults stage4;

        private FourStagedDefaults(Defaults stage1, Defaults stage2, Defaults stage3, Defaults stage4) {
            this.stage1 = stage1;
            this.stage2 = stage2;
            this.stage3 = stage3;
            this.stage4 = stage4;
        }

        public Defaults getStage1() {
            return stage1;
        }

        public Defaults getStage2() {
            return stage2;
        }

        public Defaults getStage3() {
            return stage3;
        }

        public Defaults getStage4() {
            return stage4;
        }
    }
}
