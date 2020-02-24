package ru.timeconqueror.lootgames;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LootGames.MODID)
public class LootGames {
    public static final String MODID = "lootgames";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static LootGames INSTANCE = null;

    public LootGames() {
        INSTANCE = this;
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }
}
