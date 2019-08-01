package ru.timeconqueror.lootgames.api.tileentity;

import net.minecraft.block.state.IBlockState;

public abstract class TileEntityGameBlock extends TileEntityWithSecretData {
    /**
     * Saves the block to disk, sends packet to update it on client.
     */
    protected void setBlockToUpdate() {
//        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, getState(), getState(), 3);
//        world.scheduleBlockUpdate(pos, this.getBlockType(), 0, 0);
        markDirty();
    }

    /**
     * Returns the blockstate on tileentity pos.
     */
    public IBlockState getState() {
        return world.getBlockState(pos);
    }
}
