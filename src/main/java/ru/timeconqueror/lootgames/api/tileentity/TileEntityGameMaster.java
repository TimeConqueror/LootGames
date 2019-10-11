package ru.timeconqueror.lootgames.api.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import ru.timeconqueror.lootgames.api.minigame.AbstractLootGame;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityGameMaster<T extends AbstractLootGame> extends TileEntity {
    protected T game;

    public TileEntityGameMaster(T game) {
        this.game = game;
        game.setMasterTileEntity(this);
    }

    /**
     * For saving/sending data use {@link #writeTEDataToNBT(NBTTagCompound)}
     */
    @Override
    public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
        writeTEDataToNBT(compound);
        compound.setTag("game", game.writeToNBT(false));
        return compound;
    }

    /**
     * For saving/sending data use {@link #readTEDataFromNBT(NBTTagCompound)}
     */
    @Override
    public final void readFromNBT(NBTTagCompound compound) {
        this.readTEDataFromNBT(compound);

        //If read from client side
        if (compound.hasKey("client_flag")) {
            if (game.isDataSyncsEntirely()) {
                game.readFromNBT(compound.getCompoundTag("game"), true, true);
            } else {
                game.readClientSyncedNBT(compound.getCompoundTag("game_synced"), true);
            }
        } else {
            game.readFromNBT(compound.getCompoundTag("game"), true, false);//First true? //FIXME?
        }
    }

    /**
     * Improved analog of {@link #writeToNBT(NBTTagCompound)}. Overriding is fine.
     */
    protected NBTTagCompound writeTEDataToNBT(NBTTagCompound compound) {
        return writeClientSyncedNBT(compound);
    }

    /**
     * Improved analog of {@link #readFromNBT(NBTTagCompound)}. Overriding is fine.
     */
    protected void readTEDataFromNBT(NBTTagCompound compound) {
        readClientSyncedNBT(compound);
    }

    /**
     * Reads the part of data permitted for sending to client. Overriding is fine.
     */
    protected void readClientSyncedNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    /**
     * Writes the part of data permitted for sending to client. Overriding is fine.
     */
    protected NBTTagCompound writeClientSyncedNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();

        if (isDataSyncsEntirely()) {
            writeTEDataToNBT(compound);
        } else {
            writeClientSyncedNBT(compound);
        }

        if (game.isDataSyncsEntirely()) {
            compound.setTag("game", game.writeToNBT(true));
        } else {
            compound.setTag("game_synced", game.writeClientSyncedNBT(true));
        }

        compound.setByte("client_flag", (byte) 0);
        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound compound = pkt.getNbtCompound();
        if (isDataSyncsEntirely()) {
            readTEDataFromNBT(compound);
        } else {
            readClientSyncedNBT(compound);
        }

        if (game.isDataSyncsEntirely()) {
            game.readFromNBT(compound.getCompoundTag("game"), true, true);
        } else {
            game.readClientSyncedNBT(compound.getCompoundTag("game_synced"), true);
        }
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
    }

    /**
     * If set to false, then data that will be sent to client is regulated by {@link #writeClientSyncedNBT(NBTTagCompound)} and {@link #readClientSyncedNBT(NBTTagCompound)}
     * So it won't send some confidential data, unless you set this to true.
     */
    protected abstract boolean isDataSyncsEntirely();

    /**
     * Saves the block to disk, sends packet to update it on client.
     */
    protected void setBlockToUpdateAndSave() {
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
