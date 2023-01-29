package ru.timeconqueror.lootgames.api.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.exception.NotExistsException;

import java.util.ArrayList;
import java.util.Iterator;

public class TETaskScheduler implements INBTSerializable<ListTag> {
    private final ArrayList<TaskWrapper> tasks = new ArrayList<>();

    private final BlockEntity tileEntity;

    public TETaskScheduler(@NotNull BlockEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    /**
     * Thread-safely adds task to scheduler.
     */
    public void addTask(ITask task, int timeBeforeStart) {
        tasks.add(new TaskWrapper(timeBeforeStart, task));
    }

    /**
     * Must be called to update scheduler.
     */
    public void onUpdate() {
        Iterator<TaskWrapper> iterator = tasks.iterator();

        while (iterator.hasNext()) {
            TaskWrapper task = iterator.next();

            if (task.timeBeforeStart <= 0) {
                task.run(tileEntity.getLevel());
                iterator.remove();
            } else {
                task.decreaseTimer();
            }
        }
    }

    @Override
    public ListTag serializeNBT() {
        ListTag out = new ListTag();

        for (TaskWrapper wrapper : tasks) {
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
        private final ITask task;

        private TaskWrapper(int timeBeforeStart, ITask task) {
            this.timeBeforeStart = timeBeforeStart;
            this.task = task;
        }

        private void decreaseTimer() {
            timeBeforeStart--;
        }

        private void run(Level world) {
            task.run(world);
        }
    }
}
