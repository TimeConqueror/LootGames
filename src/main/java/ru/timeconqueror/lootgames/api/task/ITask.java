package ru.timeconqueror.lootgames.api.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * While implementing ITask instance, you should also register {@link ITaskFactory} in {@link TaskRegistry}/
 */
public interface ITask extends INBTSerializable<CompoundTag> {
    void run(Level world);

    interface ITaskFactory<T extends ITask> {
        T create();
    }
}
