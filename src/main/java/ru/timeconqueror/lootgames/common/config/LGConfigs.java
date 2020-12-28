package ru.timeconqueror.lootgames.common.config;

import net.minecraftforge.fml.config.ModConfig;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.registry.ConfigRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class LGConfigs {
    public static final ConfigGeneral GENERAL = new ConfigGeneral(ModConfig.Type.COMMON, "general", "General Settings");
    public static final ConfigMS MINESWEEPER = new ConfigMS(ModConfig.Type.COMMON, "minesweeper", "Regulates \"Minesweeper\" minigame.");
    public static final ConfigGOL GOL = new ConfigGOL(ModConfig.Type.COMMON, "game_of_light", "Regulates \"Game of Light\" minigame.");
    public static final ConfigRewards REWARDS = ConfigRewards.INSTANCE;
    @AutoRegistrable
    private static final ConfigRegister REGISTER = new ConfigRegister(LootGames.MODID);

    static String resolve(String path) {
        return LootGames.MODID + "/" + path;
    }

    @AutoRegistrable.InitMethod
    private static void register() {
        REGISTER.register(GENERAL);
        REGISTER.register(MINESWEEPER);
        REGISTER.register(GOL);
        REGISTER.register(REWARDS);
    }
}
