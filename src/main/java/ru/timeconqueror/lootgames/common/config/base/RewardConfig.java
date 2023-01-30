package ru.timeconqueror.lootgames.common.config.base;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.common.config.ConfigSection;
import ru.timeconqueror.timecore.api.common.config.ConfigValidators;
import ru.timeconqueror.timecore.api.common.config.IQuickConfigValue;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;
import ru.timeconqueror.timecore.api.util.ParseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class RewardConfig extends ConfigSection {
    public IQuickConfigValue<Integer> minItems;
    public IQuickConfigValue<Integer> maxItems;
    public IQuickConfigValue<ResourceLocation> defaultLootTable;

    private ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionConfigsProp;
    private final HashMap<ResourceLocation, ResourceLocation> dimensionsConfigs = new HashMap<>();

    private final Defaults defaults;

    public RewardConfig(String key, Defaults defaults) {
        super(key, null);
        this.defaults = defaults;
    }

    @Override
    public void setup(ImprovedConfigBuilder builder) {
        minItems = builder.optimized(
                builder.comment("Minimum amount of item stacks to be generated in chest. Won't be applied, if count of items in bound loot table are less than it. If min and max are set to -1, the limits will be disabled.")
                        .defineInRange("min_items", defaults.minItems, -1, 256)
        );
        maxItems = builder.optimized(
                builder.comment("Maximum amount of item stacks to be generated in chest. If this is set to -1, max limit will be disabled.")
                        .defineInRange("max_items", defaults.maxItems, -1, 256)
        );
        defaultLootTable = builder.optimized(
                builder.comment("Name of the loot table, items from which will be generated in the chest of this stage. This can be adjusted per dimension in \"per_dim_configs\".")
                        .define("loot_table", defaults.lootTable.toString(), ConfigValidators.resourceLocation()),
                ResourceLocation::new, ResourceLocation::toString
        );

        dimensionConfigsProp = builder.comment("Here you can add different loot tables to each dimension. If dimension isn't in this list, then game will take default loot table for this stage.",
                "Syntax: <dimension_key>; <loottable_name>",
                "<loottable_name> - The loottable name for the chest in this stage.",
                "General Example: [ \"0; minecraft:chests/simple_dungeon\" ]")
                .defineList("per_dim_configs", new ArrayList<>(), o -> true);
    }

    public ResourceLocation getLootTable(Level world) {
        ResourceLocation dimKey = world.dimension().location();
        ResourceLocation lootTableDim = dimensionsConfigs.get(dimKey);

        return lootTableDim != null ? lootTableDim : defaultLootTable.get();
    }

    @Override
    public void onEveryLoad(ModConfigEvent configEvent) {
        super.onEveryLoad(configEvent);

        parseDimConfigs();
    }

    private void parseDimConfigs() {
        dimensionsConfigs.clear();

        for (String entry : dimensionConfigsProp.get()) {
            String[] config = entry.split(";");
            for (int i = 0; i < config.length; i++) {
                config[i] = config[i].trim();
            }

            if (config.length == 2) {
                Either<ResourceLocation, DataResult.PartialResult<ResourceLocation>> rlParseResult = ParseUtils.parseResourceLocation(config[0]).get();
                Optional<ResourceLocation> dimKey = rlParseResult.left();
                if (dimKey.isPresent()) {
                    if (dimensionsConfigs.containsKey(dimKey.get())) {
                        LootGames.LOGGER.error("Invalid dimension configs entry found: {}. Dimension ID is already defined. Skipping entry...", entry);
                        continue;
                    }

                    if (!config[1].isEmpty()) {
                        dimensionsConfigs.put(dimKey.get(), new ResourceLocation(config[1]));
                    } else {
                        LootGames.LOGGER.error("Invalid dimension configs entry found: {}. LootTable key must not be an empty string. Skipping entry...", entry);
                    }
                } else {
                    LootGames.LOGGER.error("Invalid dimension configs entry found: {}. Dimension id must be a valid Resource Location.  Skipping entry...", rlParseResult.right().get().message());
                }
            } else {
                LootGames.LOGGER.error("Invalid dimension configs entry found: {}. Syntax is <dimension_key>; <loottable_key>.  Skipping entry...", entry);
            }
        }
    }

    public static class Defaults {
        private final ResourceLocation lootTable;
        private final int minItems;
        private final int maxItems;

        public Defaults(ResourceLocation lootTable, int minItems, int maxItems) {
            this.lootTable = lootTable;
            this.minItems = minItems;
            this.maxItems = maxItems;
        }
    }
}
