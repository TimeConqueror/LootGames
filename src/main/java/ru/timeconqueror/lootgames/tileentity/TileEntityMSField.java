package ru.timeconqueror.lootgames.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import ru.timeconqueror.lootgames.api.tileentity.TileEntityGameBlock;

public class TileEntityMSField extends TileEntityGameBlock {
    private int type = 0;
    private boolean hidden = true;

    public void onBlockClickedByPlayer(EntityPlayer player) {
        if (hidden) {
            reveal();
        }
    }

    private void reveal() {
        hidden = false;
        setBlockToUpdate();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        type = compound.getInteger("type");
        this.readFromNBTNonSecretData(compound);
    }

    /**
     * Reads the data, that can be sent to client.
     */
    public void readFromNBTNonSecretData(NBTTagCompound compound) {
        hidden = compound.getBoolean("hidden");
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("type", type);
        return this.writeToNBTNonSecretData(compound);
    }

    /**
     * Writes the data, that can be sent to client.
     */
    public NBTTagCompound writeToNBTNonSecretData(NBTTagCompound compound) {
        compound.setBoolean("hidden", hidden);
        return super.writeToNBT(compound);
    }

    @Override
    protected boolean sendOnlyPermittedDataToClient() {
        return true;
    }
}
