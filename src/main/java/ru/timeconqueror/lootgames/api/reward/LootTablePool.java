package ru.timeconqueror.lootgames.api.reward;

import lombok.Getter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class LootTablePool {
    private final List<LootTablePoolEntry> entries;
    private final int totalWeight;

    public LootTablePool(List<LootTablePoolEntry> entries) {
        this.entries = Collections.unmodifiableList(entries);

        this.totalWeight = entries.stream()
                .mapToInt(LootTablePoolEntry::getWeight)
                .reduce(Integer::sum)
                .orElse(0);
    }

    /**
     * Chooses random entry from pool based on its weight.
     *
     * @return key of chosen loot table. Returns {@link LootTable#EMPTY} if pool is invalid, which means its total weight or entry count is zero
     */
    public LootTable chooseRandom(Level level, RandomSource randomSource) {
        var entry = chooseRandomEntry(randomSource);
        if (entry == null) return LootTable.EMPTY;

        return Objects.requireNonNull(level.getServer())
                .getLootData()
                .getLootTable(entry.getKey());
    }

    @Nullable
    private LootTablePoolEntry chooseRandomEntry(RandomSource randomSource) {
        if (totalWeight < 1) {
            return null;
        }

        int chosenWeight = randomSource.nextInt(totalWeight);

        if (entries.size() == 1) {
            return entries.get(0);
        }

        for (LootTablePoolEntry entry : entries) {
            chosenWeight -= entry.getWeight();

            if (chosenWeight < 0) {
                return entry;
            }
        }

        throw new IllegalStateException("Should not be reachable");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<LootTablePoolEntry> entries = new ArrayList<>();

        public Builder entry(LootTablePoolEntry entry) {
            this.entries.add(entry);
            return this;
        }

        public LootTablePool build() {
            return new LootTablePool(entries);
        }
    }
}