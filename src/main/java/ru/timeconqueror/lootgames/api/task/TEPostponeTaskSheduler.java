package ru.timeconqueror.lootgames.api.task;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import ru.timeconqueror.lootgames.LootGames;

import java.util.ArrayList;
import java.util.Iterator;

public class TEPostponeTaskSheduler implements INBTSerializable<NBTTagList> {
    private final ArrayList<TaskWrapper> tasks = new ArrayList<>();

    private final TileEntity tileEntity;

    public TEPostponeTaskSheduler(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    /**
     * Thread-safely adds task to sheduler.
     */
    public void addTask(ITask task, int timeBeforeStart) {
        synchronized (tasks) {
            tasks.add(new TaskWrapper(timeBeforeStart, task));
        }
    }

    /**
     * Must be called to update sheduler.
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
    public NBTTagList serializeNBT() {
        NBTTagList out = new NBTTagList();

        synchronized (tasks) {
            for (TaskWrapper wrapper : tasks) {
                NBTTagCompound element = new NBTTagCompound();

                element.setTag("task", wrapper.task.serializeNBT());
                element.setInteger("time", wrapper.timeBeforeStart);
                element.setString("name", wrapper.task.getClass().getName());

                out.appendTag(element);
            }
        }

        return out;
    }

    @Override
    public void deserializeNBT(NBTTagList nbt) {
        for (NBTBase in : nbt) {
            NBTTagCompound c = (NBTTagCompound) in;
            int time = c.getInteger("time");

            Class<? extends ITask> taskClass = TaskRegistry.getTaskClass(c.getString("name"));
            if (taskClass == null) {
                LootGames.logHelper.error("You didn't register task class {} in TaskRegistry. It will be skipped.", c.getString("name"));
                return;
            }

            ITask task;
            try {
                task = taskClass.newInstance();
                task.deserializeNBT(c.getCompoundTag("task"));
            } catch (InstantiationException | IllegalAccessException e) {
                LootGames.logHelper.error("Can't create task {} while restoring world from save. It will be skipped.", taskClass);
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
