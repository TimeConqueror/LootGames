package ru.timeconqueror.lootgames.api.util;
//TODO Move to TimeCore

/**
 * For using in lambdas, where,for example, integers cannot be changed.
 */
public class Wrapper<T> {
    T value;

    public Wrapper(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
