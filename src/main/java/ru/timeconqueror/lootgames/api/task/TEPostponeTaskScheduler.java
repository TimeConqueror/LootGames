package ru.timeconqueror.lootgames.api.task;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.exception.NotExistsException;

import java.util.ArrayList;
import java.util.Iterator;

public class TEPostponeTaskScheduler implements INBTSerializable<ListNBT> {
    private final ArrayList<TaskWrapper> tasks = new ArrayList<>();

    private final TileEntity tileEntity;

    public TEPostponeTaskScheduler(@NotNull TileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    /**
     * Thread-safely adds task to scheduler.
     */
    public void addTask(ITask task, int timeBeforeStart) {
        synchronized (tasks) {
            tasks.add(new TaskWrapper(timeBeforeStart, task));
        }
    }

    /**
     * Must be called to update scheduler.
     */
    public void onUpdate() {
        synchronized (tasks) {
            Iterator<TaskWrapper> iterator = tasks.iterator();

            while (iterator.hasNext()) {
                TaskWrapper task = iterator.next();

                if (task.timeBeforeStart <= 0) {
                    task.run(tileEntity.getWorld());
                    iterator.remove();
                } else {
                    task.decreaseTimer();
                }
            }
        }
    }

    @Override
    public ListNBT serializeNBT() {
        ListNBT out = new ListNBT();

        synchronized (tasks) {
            for (TaskWrapper wrapper : tasks) {
                CompoundNBT element = new CompoundNBT();

                element.put("task", wrapper.task.serializeNBT());
                element.putInt("time", wrapper.timeBeforeStart);
                element.putString("name", wrapper.task.getClass().getName());

                out.add(element);
            }
        }

        return out;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserializeNBT(ListNBT nbt) {
        for (INBT in : nbt) {
            CompoundNBT c = (CompoundNBT) in;
            int time = c.getInt("time");

            Class<?> clazz = null;
            try {
                clazz = Class.forName(c.getString("name"));
                Class<? extends ITask> taskClass = (Class<? extends ITask>) clazz;

                ITask task = TaskRegistry.createTask(taskClass);

                task.deserializeNBT(c.getCompound("task"));

                tasks.add(new TaskWrapper(time, task));
            } catch (ClassNotFoundException e) {
                LootGames.LOGGER.error("Can't found class for name: {} . Skipping...", c.getString("name"));
            } catch (ClassCastException e) {
                LootGames.LOGGER.error("Restored class name {} doesn't inherit {}. Skipping...", clazz, ITask.class);
                e.printStackTrace();
            } catch (NotExistsException e) {
                LootGames.LOGGER.error("Mod author didn't register factory for task class {} in TaskRegistry. Skipping...", clazz);
                e.printStackTrace();
            }
        }
    }

    private static class TaskWrapper {
        private int timeBeforeStart;
        private ITask task;

        private TaskWrapper(int timeBeforeStart, ITask task) {
            this.timeBeforeStart = timeBeforeStart;
            this.task = task;
        }

        private void decreaseTimer() {
            timeBeforeStart--;
        }

        private void run(World world) {
            task.run(world);
        }
    }
}
