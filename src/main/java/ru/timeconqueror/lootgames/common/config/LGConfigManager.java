package ru.timeconqueror.lootgames.common.config;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.common.config.ConfigManager;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable
public class LGConfigManager extends ConfigManager {
    static String resolve(String path) {
        return LootGames.MODID + "/" + path;
    }

    @Override
    protected void register() {
        registerConfig(ConfigGeneral.INSTANCE);
        registerConfig(ConfigMS.INSTANCE);
    }
}
