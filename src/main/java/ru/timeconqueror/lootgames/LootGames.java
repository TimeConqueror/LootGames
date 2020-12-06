package ru.timeconqueror.lootgames;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.util.EnvironmentUtils;

@Mod(LootGames.MODID)
public class LootGames implements TimeMod {
    public static final String MODID = "lootgames";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static LootGames INSTANCE = null;

    private static final String MARKER_PROPERTY = "lootgames.logging.markers";

    public LootGames() {
        INSTANCE = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConstructed);
    }

    private void onModConstructed(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            String[] markers = System.getProperty(MARKER_PROPERTY).split(",");
            EnvironmentUtils.enableLogMarkers(markers);
        });
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }
}
