package ru.timeconqueror.lootgames.api.task;

import java.util.HashMap;

public class TaskRegistry {
    private static HashMap<String, Class<? extends ITask>> tasks = new HashMap<>();

    public static void registerTask(Class<? extends ITask> taskClass) {
        tasks.put(taskClass.getName(), taskClass);
    }

    public static Class<? extends ITask> getTaskClass(String name) {
        return tasks.get(name);
    }
}
