package ru.timeconqueror.lootgames.common.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import ru.timeconqueror.lootgames.api.reward.LootTablePool;
import ru.timeconqueror.lootgames.api.reward.LootTablePoolEntry;
import ru.timeconqueror.lootgames.common.config.base.RewardConfig.Defaults;

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
                        .lootTablePool(LootTablePool.builder()
                                .entry(LootTablePoolEntry.builder().key(BuiltInLootTables.SIMPLE_DUNGEON).weight(1).build()
                                ).build())
                        .build(),
                Defaults.builder()
                        .lootTablePool(LootTablePool.builder()
                                .entry(LootTablePoolEntry.builder().key(BuiltInLootTables.DESERT_PYRAMID).weight(1).build()
                                ).build())
                        .build(),
                Defaults.builder()
                        .lootTablePool(LootTablePool.builder()
                                .entry(LootTablePoolEntry.builder().key(BuiltInLootTables.NETHER_BRIDGE).weight(1).build()
                                ).build())
                        .build(),
                Defaults.builder()
                        .lootTablePool(LootTablePool.builder()
                                .entry(LootTablePoolEntry.builder().key(BuiltInLootTables.END_CITY_TREASURE).weight(1).build()
                                ).build())
                        .build()
        );

        private final Defaults stage1;
        private final Defaults stage2;
        private final Defaults stage3;
        private final Defaults stage4;
    }
}
