package ru.timeconqueror.lootgames.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.timeconqueror.lootgames.LootGames;

@Mod.EventBusSubscriber
@Config(modid = LootGames.MODID, type = Config.Type.INSTANCE)
public class LootGamesConfig {
    @Config.LangKey("config.lootgames.category.worldgen")
    @Config.Comment("Regulates dungeon appearing in world.")
    public static WorldGen worldGen = new WorldGen();

    //TODO add minigame disabling
    @Config.LangKey("config.lootgames.minigamesenabled")
    @Config.Comment({"If this is set to false, then dungeon generation will be enabled.", "Default: true"})
    public static boolean areMinigamesEnabled = true;

    @Config.LangKey("config.lootgames.debugmode")
    @Config.Comment({"If this is equal true, then it will print additional info to log files.", "Default: false"})
    public static boolean debugMode = false;

    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent.PostConfigChangedEvent event) {
        if (event.getModID().equals(LootGames.MODID)) {
            ConfigManager.sync(LootGames.MODID, Config.Type.INSTANCE);
        }
    }

    public static class WorldGen {
        @Config.LangKey("config.lootgames.worldgenenabled")
        @Config.Comment({"If this is equal true, then dungeon generation will be enabled.", "Default: true"})
        public boolean isDungeonWorldGenEnabled = true;

        //TODO add retrogen
        @Config.LangKey("config.lootgames.retrogenenabled")
        @Config.Comment({"If this is equal true, then minigames will appear in already generated chunks.", "Default: false"})
        public boolean isDungeonRetroGenEnabled = false;

        //TODO add retrogen
        @Config.LangKey("config.lootgames.retrogenenabled")
        @Config.Comment({"If this is equal true, then minigames will appear in already generated chunks.", "Default: false"})
        public String[] dimensionWhitelist = new String[]{"0"};
    }
}
