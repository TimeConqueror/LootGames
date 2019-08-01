package ru.timeconqueror.lootgames.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityWithSecretData extends TileEntity {
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.readClientPermittedDataFromNBT(compound);
    }

    /**
     * Reads the data, that is permitted for sending to client.
     */
    public void readClientPermittedDataFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return this.writeClientPermittedDataToNBT(compound);
    }

    /**
     * Writes the data, that is permitted for sending to client.
     */
    public NBTTagCompound writeClientPermittedDataToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();

        if (sendOnlyPermittedDataToClient()) {
            writeClientPermittedDataToNBT(compound);
        } else {
            writeToNBT(compound);
        }

        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        if (sendOnlyPermittedDataToClient()) {
            readClientPermittedDataFromNBT(pkt.getNbtCompound());
        } else {
            readFromNBT(pkt.getNbtCompound());
        }
    }

    /**
     * If true, sending to client will call only {@link #writeClientPermittedDataToNBT(NBTTagCompound)}
     * and {@link #readClientPermittedDataFromNBT(NBTTagCompound)} to prevent client from seeing server-only secret data.
     * <p>If false, packets will use standard writeTo/readFrom NBT methods.
     */
    protected abstract boolean sendOnlyPermittedDataToClient();
}
