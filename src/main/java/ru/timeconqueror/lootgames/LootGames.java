package ru.timeconqueror.lootgames;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import ru.timeconqueror.lootgames.api.Markers;
import ru.timeconqueror.timecore.api.TimeCoreAPI;

import java.util.ArrayList;
import java.util.List;

//TODO add neon particles on board borders when player is on top
//TODO replace coords in ms overlay by raytracing on client if player looks at board within N blocks
@Mod(LootGames.MODID)
public class LootGames {
    public static final String MODID = "lootgames";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static LootGames INSTANCE = null;

    private static final String MARKER_PROPERTY = "lootgames.logging.markers";

    public LootGames() {
        INSTANCE = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::handleMarkers);
        TimeCoreAPI.setup(this);
    }

    private void handleMarkers(FMLConstructModEvent event) {
        String markerProperty = System.getProperty(MARKER_PROPERTY);
        String[] markers = markerProperty != null ? markerProperty.split(",") : new String[0];

        List<String> enabledMarkers = new ArrayList<>();
        String[] disabledMarkers = Markers.lookup().stream()
                .map(Marker::getName)
                .filter(name -> {
                    for (String enabled : markers) {
                        if (name.equals(enabled)) {
                            enabledMarkers.add(name);
                            return false;
                        }
                    }

                    return true;
                }).toArray(String[]::new);

        //TODO uncomment?
//        LOGGER.info("Enabled logger markers: " + enabledMarkers);
//        event.enqueueWork(() -> EnvironmentUtils.disableLogMarkers(disabledMarkers));
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }
}
