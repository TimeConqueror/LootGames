package ru.timeconqueror.lootgames.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class LGLootTableProvider {
    public static LootTableProvider create(PackOutput output_) {
        return new LootTableProvider(output_, /*requiredTables*/ Set.of(),
                List.of(
                        new LootTableProvider.SubProviderEntry(LGBlockLootSubProvider::new, LootContextParamSets.BLOCK)));
    }
}
