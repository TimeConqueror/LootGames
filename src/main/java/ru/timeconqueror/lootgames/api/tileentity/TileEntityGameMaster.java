package ru.timeconqueror.lootgames.api.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import ru.timeconqueror.lootgames.api.minigame.AbstractLootGame;

/**
 * Base minigame master block. It will automatically save game object it stores.
 */
public abstract class TileEntityGameMaster<T extends AbstractLootGame> extends TileEntityWithSecretData {
    private T game;

    public TileEntityGameMaster(T game) {
        this.game = game;
    }

    /**
     * Overriding it, obligatorily call {@link TileEntityGameMaster#writeToNBT(NBTTagCompound)}!
     */
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound gameData = game.serializeNBT();
        compound.setTag("game_data", gameData);

        return super.writeToNBT(compound);
    }

    /**
     * Overriding it, <b>FIRSTLY</b> obligatorily call {@link TileEntityGameMaster#readFromNBT(NBTTagCompound)}!
     */
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        NBTTagCompound gameData = compound.getCompoundTag("game_data");
        game.deserializeNBT(gameData);
    }

    /**
     * If true, sending to client will call only {@link #writeClientPermittedDataToNBT(NBTTagCompound)}
     * and {@link #readClientPermittedDataFromNBT(NBTTagCompound)} to prevent client from seeing server-only secret data.
     * <p>If false, packets will use standard writeTo/readFrom NBT methods.
     */
    @Override
    protected boolean sendOnlyPermittedDataToClient() {
        return true;
    }

    /**
     * Called when player clicked on subordinate block.
     */
    public void onSubordinateBlockClicked(BlockPos subordinatePos, EntityPlayer player) {

    }

    public T getGame() {
        return game;
    }

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

    public void onBlockClickedByPlayer(EntityPlayer player) {

    }
}
