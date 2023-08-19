package ru.timeconqueror.lootgames.api.task;

import ru.timeconqueror.timecore.api.exception.NotExistsException;

import java.util.HashMap;

/**
 * Used for task factory registering.
 */
public abstract class TaskRegistry {
    private static final HashMap<Class<? extends Task>, Task.TaskFactory<?>> TASK_FACTORIES = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Task> T createTask(Class<T> taskClass) throws NotExistsException {
        Task.TaskFactory<T> factory = (Task.TaskFactory<T>) TASK_FACTORIES.get(taskClass);
        if (factory == null) throw new NotExistsException("There is no factory for provided task class.");
        return factory.create();
    }

    public static <T extends Task> void registerTaskFactory(Class<T> taskClass, Task.TaskFactory<T> factory) {
        if (TASK_FACTORIES.put(taskClass, factory) != null) {
            throw new IllegalArgumentException("The factory for task class " + taskClass + " has been already registered.");
        }
    }
}
