package ru.timeconqueror.lootgames.api;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Markers {
    private static final List<Marker> LOOKUP = new ArrayList<>();

    public static final Marker LOOTGAME = register(MarkerManager.getMarker("LOOTGAME"));
    public static final Marker ROOM = register(MarkerManager.getMarker("ROOM"));

    private static Marker register(Marker marker) {
        LOOKUP.add(marker);
        return marker;
    }

    public static List<Marker> lookup() {
        return Collections.unmodifiableList(LOOKUP);
    }
}