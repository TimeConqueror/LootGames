package ru.timeconqueror.lootgames.common.config;

import eu.usrv.yamcore.auxiliary.IntHelper;
import net.minecraftforge.common.config.Configuration;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.common.config.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigGeneral extends Config {

    public boolean disableMinigames;
    public WorldGenCategory worldGen = new WorldGenCategory();

    public ConfigGeneral() {
        super("general");
    }

    @Override
    public void init() {
        disableMinigames = config.getBoolean("disable_minigames", "main", false, "If this is set to true, then puzzle master won't start any new game.");
        worldGen.init(config);
    }

    @Override
    public String getRelativePath() {
        return LGConfigs.resolve(getKey());
    }

    public static class WorldGenCategory {
        public boolean disableDungeonGen;
        public boolean retroGenDungeons;
        private Map<Integer, Integer> dimRhombs;
        public String dungeonLogLevel;

        private void init(Configuration config) {
            disableDungeonGen = config.getBoolean("disable_dungeon_gen", "worldgen", false, "Enable or disable dungeon generation");
//            retroGenDungeons = config.getBoolean("retrogen_dungeons", "worldgen", false, "Enable or disable RetroGen"); is disabled
            String[] dimConfigs = config.getStringList("per_dimension_configs", "worldgen", new String[]{"0; 20"}, "Whitelisted dimensions' ids that were allowed for dungeon generation and rhomb size.\nRhomb size means the size of rhombs, which will imaginary cover the world. Dungeon will be generated in each rhomb. \nSo the larger the size, the less chance of generation. \nRhomb size must be between 5 and 100. \nExample of array element: 0; 20 - this means that dungeons will be generated in rhombs with size equal to 20 in the overworld (ID = 0).");
            parseDimensionConfig(dimConfigs);
            dungeonLogLevel = config.getString("dungeon_log_level", "worldgen", Level.INFO.toString(), "Log level for the separate DungeonGenerator Logger. Valid options: INFO, DEBUG, TRACE", new String[]{"INFO", "DEBUG", "TRACE"});

            config.setCategoryComment("worldgen", "Regulates dungeon appearing in world.");
        }

        private void parseDimensionConfig(String[] dimList) {
            dimRhombs = new HashMap<>();
            for (String entry : dimList) {
                // Skip Zero-Length entries
                if (entry.length() == 0)
                    return;

                String[] tArray = entry.split(";");
                if (tArray.length == 2) {
                    if (IntHelper.tryParse(tArray[0]) && IntHelper.tryParse(tArray[1])) {
                        int dimId = Integer.parseInt(tArray[0]);
                        int rhombSize = Integer.parseInt(tArray[1]);

                        if (rhombSize < 5 || rhombSize > 100)
                            LootGames.LOGGER.error(String.format("Invalid DimensionWhitelist entry found: [%s;]; RhombusSize must be between 5 and 100", entry));
                        else {
                            if (!dimRhombs.containsKey(dimId)) {
                                dimRhombs.put(dimId, rhombSize);
                                LootGames.LOGGER.info(String.format("Worldgen enabled in DimensionID %d with Rhombus Size %d", dimId, rhombSize));
                            } else
                                LootGames.LOGGER.error(String.format("Invalid DimensionWhitelist entry found: [%s;]; DimensionID is already defined", entry));
                        }
                    } else
                        LootGames.LOGGER.error(String.format("Invalid DimensionWhitelist entry found: [%s;]; DimensionID or Rhombus Size is not an Integer", entry));
                } else
                    LootGames.LOGGER.error(String.format("Invalid DimensionWhitelist entry found: [%s;]; Syntax is <DimensionID>;<Rhombus Size>", entry));
            }
        }

        public boolean isDimensionEnabledForWG(int dimensionId) {
            return dimRhombs.containsKey(dimensionId);
        }

        public int getWorldGenRhombusSize(int dimensionId) {
            if (!isDimensionEnabledForWG(dimensionId))
                return -1;
            else
                return dimRhombs.get(dimensionId);
        }
    }
}
