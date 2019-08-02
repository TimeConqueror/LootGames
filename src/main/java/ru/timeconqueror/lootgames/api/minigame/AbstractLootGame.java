package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class AbstractLootGame implements INBTSerializable<NBTTagCompound> {

    /**
     * Here you can create an {@link NBTTagCompound}, and then save game data to this {@link NBTTagCompound}, which will be subsequently used for sending packets and world saving.
     */
    @Override
    public abstract NBTTagCompound serializeNBT();

    /**
     * Here you can read game data from nbt, which will be received from packets or during world loading.
     */
    @Override
    public abstract void deserializeNBT(NBTTagCompound nbt);
}
