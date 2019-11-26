package ru.timeconqueror.lootgames.api.task;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @apiNote You must create a <font color="red">public empty constructor</font>
 * that will be used by schedulers to create an instance after restoring world.
 */
public interface ITask extends INBTSerializable<NBTTagCompound> {
    void run(World world);
}
