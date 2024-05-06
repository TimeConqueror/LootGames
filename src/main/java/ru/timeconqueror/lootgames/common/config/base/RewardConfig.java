package ru.timeconqueror.lootgames.common.config.base;

import com.mojang.serialization.DataResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.reward.LootTablePool;
import ru.timeconqueror.lootgames.api.reward.LootTablePoolEntry;
import ru.timeconqueror.timecore.api.common.config.ConfigSection;
import ru.timeconqueror.timecore.api.common.config.IQuickConfigValue;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;
import ru.timeconqueror.timecore.api.util.ParseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RewardConfig extends ConfigSection {
    public IQuickConfigValue<Integer> minItems;
    public IQuickConfigValue<Integer> maxItems;

    private ForgeConfigSpec.ConfigValue<List<? extends String>> lootTablePoolVal;
    @Getter
    private LootTablePool lootTablePool;

    private final Defaults defaults;

    public RewardConfig(String key, Defaults defaults) {
        super(key, null);
        this.defaults = defaults;
    }

    @Override
    public void setup(ImprovedConfigBuilder builder) {
        minItems = builder.optimized(
                builder.comment("Minimum amount of item stacks to be generated in chest. If this is set to 0 or -1, the lower limit will be the same as upper limit (see max_items config comment). If loot table contains very few items, they won't be increased by this config, it's designed to be used with giant loot tables.")
                        .defineInRange("min_items", defaults.minItems, -1, 256)
        );
        maxItems = builder.optimized(
                builder.comment("Maximum amount of item stacks to be generated in chest. If this is set to 0 or -1, the upper limit will be the chest inventory size. If loot table contains very few items, they won't be increased by this config, it's designed to be used with giant loot tables.")
                        .defineInRange("max_items", defaults.maxItems, -1, 256)
        );
        lootTablePoolVal = builder.comment("Pool of loot tables with their weights, items from which will appear as a stage reward.",
                        "Syntax: <loot_table_key>, <weight>",
                        "General Example: [ \"minecraft:chests/simple_dungeon, 10\", \"minecraft:chests/desert_pyramid, 20\" ]")
                .defineList("loot_table_pool", defaults.getSerializedLootTablePool(), o -> o instanceof String serialized && validateLootTablePool(serialized));
    }

    @Override
    public void onEveryLoad(ModConfigEvent configEvent) {
        super.onEveryLoad(configEvent);

        parseLootTablePool();
    }

    public static boolean validateLootTablePool(String serialized) {
        return parseLootTablePoolEntry(serialized)
                .get()
                .right()
                .stream()
                .peek(result -> LootGames.LOGGER.error("Invalid loot table pool found: {}. Syntax is \"<loot_table_key>, <weight>\"", serialized))
                .findAny()
                .isEmpty();
    }

    public static DataResult<LootTablePoolEntry> parseLootTablePoolEntry(String serialized) {
        String[] entry = serialized.split(",");

        if (entry.length != 2) {
            return DataResult.error(() -> "Element length in single entry should be 2 but was " + entry.length);
        }

        for (int i = 0; i < entry.length; i++) {
            entry[i] = entry[i].trim();
        }

        var serializedLootTableKey = entry[0];

        var rlParseResult = ParseUtils.parseResourceLocation(serializedLootTableKey).get();
        Optional<ResourceLocation> lootTableKey = rlParseResult.left();
        if (lootTableKey.isEmpty()) {
            return DataResult.error(() -> "Invalid loot table key: " + rlParseResult.right()
                    .map(DataResult.PartialResult::message)
                    .orElse(""));
        }

        if (entry[1].isEmpty() || !ParseUtils.isInt(entry[1])) {
            return DataResult.error(() -> "LootTable weight must not be empty and should represent integer, was: " + entry[1]);
        }

        return DataResult.success(new LootTablePoolEntry(Integer.parseInt(entry[1]), lootTableKey.get()));
    }

    private void parseLootTablePool() {
        List<LootTablePoolEntry> entries = new ArrayList<>();

        for (String serializedEntry : lootTablePoolVal.get()) {
            parseLootTablePoolEntry(serializedEntry)
                    .get()
                    .ifLeft(entries::add)
                    .ifRight(entry -> LootGames.LOGGER.error("Invalid loot table pool entry found: {}. Skipping entry...", serializedEntry));
        }

        lootTablePool = new LootTablePool(entries);
    }

    @AllArgsConstructor
    @Builder
    public static class Defaults {
        private final LootTablePool lootTablePool;
        @Builder.Default
        private final int minItems = -1;
        @Builder.Default
        private final int maxItems = -1;

        public List<String> getSerializedLootTablePool() {
            return lootTablePool
                    .getEntries()
                    .stream()
                    .map(e -> e.getKey() + ", " + e.getWeight())
                    .toList();
        }
    }
}
