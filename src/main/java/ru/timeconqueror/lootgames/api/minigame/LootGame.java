package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.nbt.NBTTagCompound;
import ru.timeconqueror.lootgames.api.tileentity.TileEntityGameMaster;

public abstract class LootGame {
    protected TileEntityGameMaster masterTileEntity;

    public void setMasterTileEntity(TileEntityGameMaster masterTileEntity) {
        this.masterTileEntity = masterTileEntity;
    }

    /**
     * Here you can create an {@link NBTTagCompound}, and then save game data to this {@link NBTTagCompound}, which will be subsequently used for world saving.
     */
    public NBTTagCompound writeNBTForSaving() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeCommonNBT(nbt);
        return nbt;
    }

    /**
     * Here you can read game data from compound, which will be received during world loading.
     */
    public void readNBTFromSave(NBTTagCompound compound) {
        readCommonNBT(compound);
    }

    /**
     * Writes data that will sync across network.
     */
    public NBTTagCompound writeNBTForClient() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeCommonNBT(nbt);
        return nbt;
    }

    /**
     * Reads data that will sync across network.
     */
    public void readNBTFromClient(NBTTagCompound compound) {
        readCommonNBT(compound);
    }

    public void writeCommonNBT(NBTTagCompound compound) {

    }

    public void readCommonNBT(NBTTagCompound compound) {
    }
}
