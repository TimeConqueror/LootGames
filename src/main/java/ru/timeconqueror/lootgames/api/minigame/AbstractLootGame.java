package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.nbt.NBTTagCompound;
import ru.timeconqueror.lootgames.api.tileentity.TileEntityGameMaster;

public abstract class AbstractLootGame {
    protected TileEntityGameMaster masterTileEntity;

    public void setMasterTileEntity(TileEntityGameMaster masterTileEntity) {
        this.masterTileEntity = masterTileEntity;
    }

    /**
     * Here you can create an {@link NBTTagCompound}, and then save game data to this {@link NBTTagCompound}, which will be subsequently used for world saving.
     * It will also be used across network, if you returns true from {@link #isDataSyncsEntirely()};
     *
     * @param isForSendingToClient If true, it will be sent on client side.
     *                             If false, it will be saved on server.
     */
    public NBTTagCompound writeToNBT(boolean isForSendingToClient) {
        return writeClientSyncedNBT(isForSendingToClient);
    }

    /**
     * Here you can read game data from compound, which will be received during world loading.
     * It will also be used across network, if you returns true from {@link #isDataSyncsEntirely()};
     *
     * @param isReadFromClient if true, this message is read on client side.
     */
    public void readFromNBT(NBTTagCompound compound, boolean callClientSyncedPart, boolean isReadFromClient) {
        if (callClientSyncedPart) {
            readClientSyncedNBT(compound, isReadFromClient);
        }
    }

    /**
     * Writes data that will sync across network.
     * Won't be used if you returns true from {@link #isDataSyncsEntirely()}.
     *
     * @param isForSendingToClient If true, it will be sent on client side.
     *                             If false, it will be saved on server.
     */
    public NBTTagCompound writeClientSyncedNBT(boolean isForSendingToClient) {
        return new NBTTagCompound();
    }

    /**
     * Reads data that will sync across network.
     * Won't be used if you returns true from {@link #isDataSyncsEntirely()}.
     *
     * @param isReadFromClient if true, this message is read on client side.
     */
    public void readClientSyncedNBT(NBTTagCompound compound, boolean isReadFromClient) {

    }

    /**
     * If set to false, then data that will be sent to client is regulated by {@link #writeClientSyncedNBT(boolean)} and {@link #readClientSyncedNBT(NBTTagCompound, boolean)}
     * So it won't send some confidential data, unless you set this to true.
     */
    public abstract boolean isDataSyncsEntirely();
}
