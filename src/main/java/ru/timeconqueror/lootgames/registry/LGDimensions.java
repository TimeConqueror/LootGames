package ru.timeconqueror.lootgames.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import ru.timeconqueror.lootgames.LootGames;

public class LGDimensions {
    public static final ResourceKey<Level> TEST_SITE_DIM = ResourceKey.create(Registries.DIMENSION, LootGames.rl("test_site"));
    public static final ResourceKey<DimensionType> TEST_SITE_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, LootGames.rl("test_site"));
    public static final ResourceKey<Biome> TEST_SITE_BIOME = ResourceKey.create(Registries.BIOME, LootGames.rl("test_site"));
}
