package ru.timeconqueror.lootgames.utils;

import java.util.function.Predicate;

public class CollectionUtils {

    public static <T> boolean allMatch(Iterable<T> iterable, Predicate<T> predicate) {
        for (T obj : iterable) {
            if (!predicate.test(obj)) return false;
        }
        return true;
    }

    public static <T> boolean anyMatch(Iterable<T> iterable, Predicate<T> predicate) {
        for (T obj : iterable) {
            if (predicate.test(obj)) return true;
        }
        return false;
    }

    public static <T> boolean noneMatch(Iterable<T> iterable, Predicate<T> predicate) {
        for (T obj : iterable) {
            if (predicate.test(obj)) return false;
        }
        return true;
    }
}
