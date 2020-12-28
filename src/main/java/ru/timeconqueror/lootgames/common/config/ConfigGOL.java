package ru.timeconqueror.lootgames.common.config;

import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.common.config.Config;
import ru.timeconqueror.timecore.api.common.config.ConfigSection;
import ru.timeconqueror.timecore.api.common.config.IQuickConfigValue;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;

public class ConfigGOL extends Config {
    public IQuickConfigValue<Integer> detonationTime;
    public IQuickConfigValue<Integer> attemptCount;

    /**
     * @param type    <i>please, see description in ModConfig.Type</i>
     * @param key     used as a location for the config file (see {@link #getRelativePath()}). Also determines the section in config file and is used as a part of lang keys.
     * @param comment used to provide a comment that can be seen above this section in the config file.
     */
    public ConfigGOL(ModConfig.@NotNull Type type, @NotNull String key, @Nullable String comment) {
        super(type, key, comment);
    }

    @Override
    public void setup(ImprovedConfigBuilder builder) {

    }

    public static class StageConfig extends ConfigSection {
        public StageConfig(@NotNull String key) {
            super(key, null);
        }

        @Override
        public void setup(ImprovedConfigBuilder builder) {

        }
    }
}
