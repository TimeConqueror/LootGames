package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.nbt.NBTTagCompound;
import ru.timeconqueror.lootgames.api.tileentity.TileEntityGameMaster;

public abstract class LootGame {
    protected TileEntityGameMaster masterTileEntity;

    public void setMasterTileEntity(TileEntityGameMaster masterTileEntity) {
        this.masterTileEntity = masterTileEntity;
    }

    /**
     * Writes data used only to save on server.
     * Overriding is fine.
     */
    public NBTTagCompound writeNBTForSaving() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeCommonNBT(nbt);
        return nbt;
    }

    /**
     * Reads data from server-saved NBT.
     * Overriding is fine.
     */
    public void readNBTFromSave(NBTTagCompound compound) {
        readCommonNBT(compound);
    }

    /**
     * Writes the data used only for sending to client, not to save it.
     * Overriding is fine.
     */
    public NBTTagCompound writeNBTForClient() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeCommonNBT(nbt);
        return nbt;
    }

    /**
     * Reads the data that comes to client side.
     * Overriding is fine.
     */
    public void readNBTFromClient(NBTTagCompound compound) {
        readCommonNBT(compound);
    }

    /**
     * Writes data to be used both server->client syncing and world saving
     */
    public void writeCommonNBT(NBTTagCompound compound) {

    }

    /**
     * Reads data that comes from both server->client syncing and world restoring from save
     */
    public void readCommonNBT(NBTTagCompound compound) {
    }
}
