package ru.timeconqueror.lootgames;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.timecore.api.TimeMod;

@Mod(LootGames.MODID)
public class LootGames extends TimeMod {
    public static final String MODID = "lootgames";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static LootGames INSTANCE = null;

    public LootGames() {
        INSTANCE = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(LootGamesAPI::init);
    }
}
