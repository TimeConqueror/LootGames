package ru.timeconqueror.lootgames.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ru.timeconqueror.lootgames.minigame.EventType;

import java.util.function.Consumer;

public class EventBus {
    private final Multimap<EventType<?>, Handler<?>> handlerMap = ArrayListMultimap.create();

    public <T> void addEventHandler(EventType<? extends T> type, Consumer<? super T> handler) {
        handlerMap.put(type, (Handler<T>) handler::accept);
    }

    public <T> void addEventHandler(EventType<? extends T> type, Runnable handler) {
        handlerMap.put(type, data -> handler.run());
    }

    public void post(EventType<Void> event) {
        post(event, null);
    }

    public <T> void post(EventType<T> event, T data) {
        handlerMap.get(event)
                .forEach(consumer -> ((Handler<T>) consumer).onEvent(data));
    }

    public interface Handler<T> {
        void onEvent(T data);
    }
}
