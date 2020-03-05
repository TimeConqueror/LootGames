package ru.timeconqueror.lootgames.api.task;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import ru.timeconqueror.lootgames.LootGames;

import java.util.ArrayList;
import java.util.Iterator;

//TODO DO ON GLOBAL IN THE WORLD
public class TEPostponeTaskScheduler implements INBTSerializable<ListNBT> {
    private final ArrayList<TaskWrapper> tasks = new ArrayList<>();

    private final TileEntity tileEntity;

    public TEPostponeTaskScheduler(TileEntity tileEntity) {
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

    @Override
    public void deserializeNBT(ListNBT nbt) {
        for (INBT in : nbt) {
            CompoundNBT c = (CompoundNBT) in;
            int time = c.getInt("time");

            Class<? extends ITask> taskClass = TaskRegistry.getTaskClass(c.getString("name"));
            if (taskClass == null) {
                LootGames.LOGGER.error("You didn't register task class {} in TaskRegistry. It will be skipped.", c.getString("name"));
                return;
            }

            ITask task;
            try {
                task = taskClass.newInstance();
                task.deserializeNBT(c.getCompound("task"));
            } catch (InstantiationException | IllegalAccessException e) {
                LootGames.LOGGER.error("Can't create task {} while restoring world from save. It will be skipped.", taskClass);
                e.printStackTrace();
                return;
            }

            tasks.add(new TaskWrapper(time, task));
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
