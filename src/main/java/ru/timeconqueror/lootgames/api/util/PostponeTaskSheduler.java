package ru.timeconqueror.lootgames.api.util;

import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;

@Mod.EventBusSubscriber
public class PostponeTaskSheduler {
    private final ArrayList<TaskWrapper> tasks = new ArrayList<>();

    /**
     * Thread-safely adds task to sheduler.
     */
    public void addTask(Runnable task, int timeBeforeStart) {
        tasks.add(new TaskWrapper(timeBeforeStart, task));
    }

    /**
     * Must be called to update sheduler.
     */
    public void onUpdate() {
        synchronized (tasks) {
            Iterator<TaskWrapper> iterator = tasks.iterator();

            while (iterator.hasNext()) {
                TaskWrapper task = iterator.next();

                if (task.getTimeBeforeStart() <= 0) {
                    task.run();
                    iterator.remove();
                } else {
                    task.decreaseTimer();
                }
            }
        }
    }

    private static class TaskWrapper {
        private int timeBeforeStart;
        private Runnable task;

        private TaskWrapper(int timeBeforeStart, Runnable task) {
            this.timeBeforeStart = timeBeforeStart;
            this.task = task;
        }

        private void decreaseTimer() {
            timeBeforeStart--;
        }

        private void run() {
            task.run();
        }

        private int getTimeBeforeStart() {
            return timeBeforeStart;
        }
    }
}
