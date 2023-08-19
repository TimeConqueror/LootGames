package ru.timeconqueror.lootgames.api.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.exception.NotExistsException;

import java.util.ArrayList;
import java.util.Iterator;

public class TaskScheduler implements INBTSerializable<ListTag> {
    private final ArrayList<DelayedTask> tasks = new ArrayList<>();

    private final Level level;

    public TaskScheduler(Level level) {
        this.level = level;
    }

    public void addTask(Task task, int delayTicks) {
        tasks.add(new DelayedTask(delayTicks, task));
    }

    /**
     * Must be called to update scheduler.
     */
    public void onTick() {
        Iterator<DelayedTask> iterator = tasks.iterator();

        while (iterator.hasNext()) {
            DelayedTask task = iterator.next();

            if (task.timeBeforeStart <= 0) {
                task.run(level);
                iterator.remove();
            } else {
                task.decreaseTimer();
            }
        }
    }

    @Override
    public ListTag serializeNBT() {
        ListTag out = new ListTag();

        for (DelayedTask wrapper : tasks) {
            CompoundTag element = new CompoundTag();

            element.put("task", wrapper.task.serializeNBT());
            element.putInt("time", wrapper.timeBeforeStart);
            element.putString("name", wrapper.task.getClass().getName());

            out.add(element);
        }

        return out;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserializeNBT(ListTag nbt) {
        for (Tag in : nbt) {
            CompoundTag c = (CompoundTag) in;
            int time = c.getInt("time");

            Class<?> clazz = null;
            try {
                clazz = Class.forName(c.getString("name"));
                Class<? extends Task> taskClass = (Class<? extends Task>) clazz;

                Task task = TaskRegistry.createTask(taskClass);

                task.deserializeNBT(c.getCompound("task"));

                tasks.add(new DelayedTask(time, task));
            } catch (ClassNotFoundException e) {
                LootGames.LOGGER.error("Can't found class for name: {}. Skipping...", c.getString("name"));
            } catch (ClassCastException e) {
                LootGames.LOGGER.error("Restored class name {} doesn't inherit {}. Skipping...", clazz, Task.class);
                e.printStackTrace();
            } catch (NotExistsException e) {
                LootGames.LOGGER.error("Mod author didn't register factory for task class {} in TaskRegistry. Skipping...", clazz);
                e.printStackTrace();
            }
        }
    }

    private static class DelayedTask {
        private int timeBeforeStart;
        private final Task task;

        private DelayedTask(int timeBeforeStart, Task task) {
            this.timeBeforeStart = timeBeforeStart;
            this.task = task;
        }

        private void decreaseTimer() {
            timeBeforeStart--;
        }

        private void run(Level level) {
            task.run(level);
        }
    }
}
