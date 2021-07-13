package ru.timeconqueror.timecore.api.common.config;

import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;

public abstract class ConfigSection {
    private final String key;
    @Nullable
    private final String comment;

    public ConfigSection(String key, @Nullable String comment) {
        this.key = key;
        this.comment = comment;
    }

    public String getKey() {
        return key;
    }

    public abstract void init(Configuration config);

    public String getComment() {
        return comment;
    }
}
