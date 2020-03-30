package ru.timeconqueror.lootgames.common.config;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.common.config.ConfigManager;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable
public class LGConfigManager extends ConfigManager {
    private static ConfigGeneral GENERAL_CONFIG = new ConfigGeneral();
    private static ConfigMS MS_CONFIG = new ConfigMS();

    static String resolve(String path) {
        return LootGames.MODID + "/" + path;
    }

    @Override
    protected void register() {
        registerConfig(GENERAL_CONFIG);
        registerConfig(MS_CONFIG);
    }
}
