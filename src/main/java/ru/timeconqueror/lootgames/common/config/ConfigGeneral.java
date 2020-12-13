package ru.timeconqueror.lootgames.common.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.common.config.Config;
import ru.timeconqueror.timecore.api.common.config.ConfigSection;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;

import java.util.HashMap;
import java.util.List;

public class ConfigGeneral extends Config {

    public ForgeConfigSpec.BooleanValue DISABLE_MINIGAMES;
    public ForgeConfigSpec.BooleanValue ENABLE_DEBUG;
    public ForgeConfigSpec.ConfigValue<String> DEBUG_LEVEL;

    public WorldGenCategory WORLD_GEN;

    public ConfigGeneral(ModConfig.@NotNull Type type, @NotNull String key, @Nullable String comment) {
        super(type, key, comment);
    }

    @Override
    public void setup(ImprovedConfigBuilder builder) {
        DISABLE_MINIGAMES = builder.comment("If this is set to true, then puzzle master won't start any new game.")
                .define("disable_minigames", false);
        ENABLE_DEBUG = builder.comment("If this is equal true, then it will print additional info to log files.")
                .define("enable_debug", false);
        DEBUG_LEVEL = builder.comment("Level of debug, will be applied if it is enable_debug property is enabled.",
                "Debug: additionally prints debug messages.",
                "Trace: additionally prints debug and trace messages.",
                "Available variants: debug, trace")
                .defineInList("debug_level", "debug", Lists.newArrayList("debug", "trace"));//TODO someday nightconfig will fix validators... https://github.com/TheElectronWill/night-config/issues/79

        WORLD_GEN = new WorldGenCategory();
        builder.addAndSetupSection(WORLD_GEN);
    }

    @Override
    public @NotNull String getRelativePath() {
        return LGConfigs.resolve("general.toml");
    }

    public static class WorldGenCategory extends ConfigSection {
        public ForgeConfigSpec.BooleanValue disableDungeonGen;
        private ForgeConfigSpec.ConfigValue<List<? extends String>> dimAndRhombList;
        private HashMap<Integer, Integer> dimRhombs;

        private ForgeConfigSpec.IntValue maxEntryPathLength;
        private int qMaxEntryPathLength;

        public WorldGenCategory() {
            super("worldgen", "Regulates dungeon appearing in world.");
        }

        @Override
        public void setup(ImprovedConfigBuilder builder) {
            disableDungeonGen = builder.comment("If this is equal true, then dungeon generation will be disabled.")
                    .define("disable_dungeon_gen", false);

            dimAndRhombList = builder.comment("Whitelisted dimensions' ids that were allowed for dungeon generation and rhomb size.",
                    "Rhomb size means the size of rhombs, which will imaginary cover the world. Dungeon will be generated in each rhomb.",
                    "So the larger the size, the less chance of generation.",
                    "Rhomb size must be between 5 and 100.",
                    "Example of array element: 0; 20 - this means that dungeons will be generated in rhombs with size equal to 20 in the overworld (ID = 0).")
                    .defineList("dim_and_rhomb_list", Lists.newArrayList("0; 20"), o -> true);//TODO someday nightconfig will fix validators...

            maxEntryPathLength = builder.comment("Defines the maximum length of the entry path from ground to the structure.",
                    "0 means, that there will be no path at all.",
                    Integer.MAX_VALUE + " means, that there will be no limits for path length. Beware of setting to this value, because the paths may be too long, so it may produce lags.")
                    .defineInRange("max_entry_path_length", 40, 0, Integer.MAX_VALUE);
        }

        public boolean isDimensionEnabledForWG(int worldID) {
            return dimRhombs.get(worldID) != null;
        }

        public int getRhombSizeFromDim(int dimensionID) {
            return dimRhombs.get(dimensionID);
        }

        public int getMaxEntryPathLength() {
            return qMaxEntryPathLength;
        }

        @Override
        public void onEveryLoad(ModConfig.ModConfigEvent configEvent) {
            qMaxEntryPathLength = maxEntryPathLength.get();

            parseDimAndRhombList();
        }

        private void parseDimAndRhombList() {
            dimRhombs = new HashMap<>();

            List<? extends String> dimAndRhombList = this.dimAndRhombList.get();

            for (String entry : dimAndRhombList) {
                if (entry.length() == 0)
                    return;

                String[] arr = entry.split(";");
                if (arr.length != 2) {
                    LootGames.LOGGER.error("Invalid dimension rhomb entry found: {}. Syntax is <dimensionID>;<rhomb size>. This entry will be skipped.", entry);
                }

                try {
                    int dimID = Integer.parseInt(arr[0].trim());
                    int rhombSize = Integer.parseInt(arr[1].trim());

                    if (rhombSize < 5 || rhombSize > 100) {
                        LootGames.LOGGER.error("Invalid dimension rhomb entry found: {}. Rhomb size must be between 5 and 100.", entry);
                    } else {
                        if (!dimRhombs.containsKey(dimID)) {
                            dimRhombs.put(dimID, rhombSize);
                            LootGames.LOGGER.info("Worldgen enabled in dimension {} with rhomb size {}.", dimID, rhombSize);
                        } else
                            LootGames.LOGGER.error("Invalid dimension rhomb entry found: {}. DimensionID is already defined.", entry);
                    }

                } catch (NumberFormatException e) {
                    LootGames.LOGGER.error("Invalid dimension rhomb entry found: {}. DimensionID or Rhomb size is not an Integer. This entry will be skipped.", entry);
                }
            }
        }
    }
}
