package ru.timeconqueror.lootgames.common.config.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import ru.timeconqueror.timecore.api.common.config.ConfigSection;
import ru.timeconqueror.timecore.api.common.config.ImprovedConfigBuilder;

public class RewardConfig extends ConfigSection {
    private ForgeConfigSpec.ConfigValue<String> lootTableVal;
    @Getter
    private ResourceLocation lootTableId;

    private final Defaults defaults;

    public RewardConfig(String key, Defaults defaults) {
        super(key, null);
        this.defaults = defaults;
    }

    @Override
    public void setup(ImprovedConfigBuilder builder) {
        lootTableVal = builder.comment("loot table, items from which will appear as a stage reward.")
                .define("loot_table", defaults.lootTable.toString(), o -> o instanceof String serialized && ResourceLocation.isValidResourceLocation(serialized));
    }

    @Override
    public void onEveryLoad(ModConfigEvent configEvent) {
        super.onEveryLoad(configEvent);

        lootTableId = new ResourceLocation(lootTableVal.get());
    }

    @AllArgsConstructor
    @Builder
    public static class Defaults {
        private final ResourceLocation lootTable;
    }
}
