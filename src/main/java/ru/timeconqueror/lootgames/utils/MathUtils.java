package ru.timeconqueror.lootgames.utils;

//TODO move to TimeCore
public class MathUtils {
    public static boolean isBetweenIncluding(int value, int limit1, int limit2) {
        int min = Math.min(limit1, limit2);
        int max = Math.max(limit1, limit2);

        return value >= min && value <= max;
    }
}
