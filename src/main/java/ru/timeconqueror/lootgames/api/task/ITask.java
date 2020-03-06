package ru.timeconqueror.lootgames.api.task;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * While implementing ITask instance, you should also register {@link ITaskFactory} in {@link TaskRegistry}/
 */
public interface ITask extends INBTSerializable<CompoundNBT> {
    void run(World world);

    interface ITaskFactory<T extends ITask> {
        T create();
    }
}
