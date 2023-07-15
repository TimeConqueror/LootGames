package ru.timeconqueror.lootgames.utils;

import org.joml.Vector2ic;

public class Utils {
    public static int manhattanDistance(Vector2ic vec1, Vector2ic vec2) {
        return Math.abs(vec1.x() - vec2.x()) + Math.abs(vec1.y() - vec2.y());
    }
}
