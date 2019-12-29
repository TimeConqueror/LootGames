package ru.timeconqueror.lootgames.api.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

import javax.annotation.Nonnull;

public abstract class TileEntityGameMaster<T extends LootGame> extends TileEntity implements ITickable {
    protected T game;

    public TileEntityGameMaster(T game) {
        this.game = game;
        game.setMasterTileEntity(this);
        game.init();
    }

    @Override
    public void update() {
        game.onTick();
    }


    /**
     * Called when TileEntityGameMaster or BlockSubordinate is broke.
     * This method implies destroying of all game blocks.
     */
    public abstract void destroyGameBlocks();

    /**
     * For saving/sending data use {@link #writeNBTForSaving(NBTTagCompound)}
     */
    @Override
    public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
        writeNBTForSaving(compound);
        compound.setTag("game", game.writeNBTForSaving());

        writeCommonNBT(compound);

        return compound;
    }

    /**
     * For saving/sending data use {@link #readNBTFromSave(NBTTagCompound)}
     */
    @Override
    public final void readFromNBT(NBTTagCompound compound) {
        //If read from client side
        if (compound.hasKey("client_flag")) {
            readNBTFromClient(compound);
            game.readNBTFromClient(compound.getCompoundTag("game_synced"));
        } else {
            readNBTFromSave(compound);
            game.readNBTFromSave(compound.getCompoundTag("game"));
        }

        readCommonNBT(compound);
    }

    /**
     * Writes data to be used both server->client syncing and world saving.
     * Overriding is fine.
     */
    protected NBTTagCompound writeCommonNBT(NBTTagCompound compound) {
        return compound;
    }

    /**
     * Reads data that comes from both server->client syncing and world restoring from save.
     * Overriding is fine.
     */
    protected void readCommonNBT(NBTTagCompound compound) {

    }

    /**
     * Writes data used only to save on server.
     * Overriding is fine.
     */
    protected NBTTagCompound writeNBTForSaving(NBTTagCompound compound) {
        writeCommonNBT(compound);
        return super.writeToNBT(compound);
    }

    /**
     * Reads data from server-saved NBT.
     * Overriding is fine.
     */
    protected void readNBTFromSave(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    /**
     * Reads the data that comes to client side.
     * Overriding is fine.
     */
    protected void readNBTFromClient(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    /**
     * Writes the data used only for sending to client, not to save it.
     * Overriding is fine.
     */
    protected NBTTagCompound writeNBTForClient(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Nonnull
    @Override
    public final NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();

        writeNBTForClient(compound);

        writeCommonNBT(compound);

        compound.setTag("game_synced", game.writeNBTForClient());

        compound.setByte("client_flag", (byte) 0);
        return compound;
    }

    @Override
    public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound compound = pkt.getNbtCompound();

        readNBTFromClient(compound);
        readCommonNBT(compound);

        game.readNBTFromClient(compound.getCompoundTag("game_synced"));
    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
    }

    /**
     * Saves the block to disk, sends packet to update it on client.
     */
    public void setBlockToUpdateAndSave() {
//        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, getState(), getState(), 3);
//        world.scheduleBlockUpdate(pos, this.getBlockType(), 0, 0);
        markDirty();
    }

    /**
     * Will be called when subordinate block is clicked by player.
     *
     * @param subordinatePos pos of subordinate block.
     * @param player         player, who clicked the subordinate block.
     */
    public void onSubordinateBlockClicked(BlockPos subordinatePos, EntityPlayer player) {
    }

    /**
     * Returns the blockstate on tileentity pos.
     */
    public IBlockState getState() {
        return world.getBlockState(pos);
    }

    public T getGame() {
        return game;
    }
}
