package ru.timeconqueror.lootgames;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.lootgames.api.Markers;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.util.EnvironmentUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        handleMarkers(event);
    }

    private void handleMarkers(FMLConstructModEvent event) {
        String markerProperty = System.getProperty(MARKER_PROPERTY);
        String[] markers = markerProperty != null ? markerProperty.split(",") : new String[0];

        List<String> enabledMarkers = new ArrayList<>();
        String[] disabledMarkers = Arrays.stream(Markers.values())
                .map(marker -> marker.getMarker().getName())
                .filter(name -> {
                    for (String enabled : markers) {
                        if (name.equals(enabled)) {
                            enabledMarkers.add(name);
                            return false;
                        }
                    }

                    return true;
                }).toArray(String[]::new);

        LOGGER.info("Enabled logger markers: " + enabledMarkers);
        event.enqueueWork(() -> EnvironmentUtils.disableLogMarkers(disabledMarkers));
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }
}
