package ru.timeconqueror.lootgames.api.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * While implementing ITask instance, you should also register {@link TaskFactory} in {@link TaskRegistry}
 */
public interface Task extends INBTSerializable<CompoundTag> {
    void run(Level level);

    interface TaskFactory<T extends Task> {
        T create();
    }
}
