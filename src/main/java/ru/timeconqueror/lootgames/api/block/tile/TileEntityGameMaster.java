package ru.timeconqueror.lootgames.api.block.tile;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.timecore.util.Hacks;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class TileEntityGameMaster<T extends LootGame> extends TileEntity implements ITickableTileEntity {
    protected T game;
    private long age;

    public TileEntityGameMaster(TileEntityType<? extends TileEntityGameMaster<T>> tileEntityTypeIn, T game) {
        super(tileEntityTypeIn);
        this.game = game;
        game.setMasterTileEntity(this);
    }

    public long getAge() {
        return age;
    }

    @Override
    public void tick() {
        age++;
        game.onTick();
    }

    /**
     * Called when TileEntityGameMaster or BlockSubordinate is broken.
     * This method implies destroying of all game blocks.
     */
    public abstract void destroyGameBlocks();

    /**
     * For saving/sending data use {@link #writeNBTForSaving(CompoundNBT)}
     */
    @NotNull
    @Override
    public final CompoundNBT save(CompoundNBT compound) {
        compound = writeNBTForSaving(compound);

        CompoundNBT gameTag = new CompoundNBT();
        game.writeNBTForSaving(gameTag);

        compound.put("game", gameTag);

        compound = writeCommonNBT(compound);

        return compound;
    }

    /**
     * For saving/sending data use {@link #readNBTFromSave(BlockState, CompoundNBT)}
     */
    @Override
    public final void load(BlockState state, CompoundNBT compound) {
        //If read from client side
        if (compound.contains("client_flag")) {
            readNBTFromClient(state, compound);
            game.readNBTAtClient(compound.getCompound("game_synced"));
        } else {
            readNBTFromSave(state, compound);
            game.readNBTFromSave(compound.getCompound("game"));
        }

        readCommonNBT(compound);
    }

    /**
     * Writes data to be used both server->client syncing and world saving.
     * Overriding is fine.
     */
    protected CompoundNBT writeCommonNBT(CompoundNBT compound) {
        return compound;
    }

    /**
     * Reads data that comes from both server->client syncing and world restoring from save.
     * Overriding is fine.
     */
    protected void readCommonNBT(CompoundNBT compound) {

    }

    /**
     * Writes data used only to save on server.
     * Overriding is fine.
     */
    protected CompoundNBT writeNBTForSaving(CompoundNBT compound) {
        compound = writeCommonNBT(compound);
        return super.save(compound);
    }

    /**
     * Reads data from server-saved NBT.
     * Overriding is fine.
     */
    protected void readNBTFromSave(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
    }

    /**
     * Reads the data that comes to client side.
     * Overriding is fine.
     */
    protected void readNBTFromClient(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
    }

    /**
     * Writes the data used only for sending to client, not to save it.
     * Overriding is fine.
     */
    protected CompoundNBT writeNBTForClient(CompoundNBT compound) {
        return super.save(compound);
    }

    @Nonnull
    @Override
    public final CompoundNBT getUpdateTag() {
        CompoundNBT compound = new CompoundNBT();

        writeNBTForClient(compound);

        writeCommonNBT(compound);

        CompoundNBT gameTag = new CompoundNBT();
        game.writeNBTForClient(gameTag);
        compound.put("game_synced", gameTag);

        compound.putByte("client_flag", (byte) 0);
        return compound;
    }

    @Override
    public final void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT compound = pkt.getTag();

        BlockState state = Minecraft.getInstance().level.getBlockState(worldPosition);
        readNBTFromClient(state, compound);
        readCommonNBT(compound);

        game.readNBTAtClient(compound.getCompound("game_synced"));
    }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 3, this.getUpdateTag());
    }

    /**
     * Saves the block to disk, sends packet to update it on client.
     */
    public void setBlockToUpdateAndSave() {
        Objects.requireNonNull(level);

        setChanged();
        level.sendBlockUpdated(worldPosition, getState(), getState(), 2);
    }

    /**
     * Will be called when subordinate block is clicked by player.
     *
     * @param player         player, who clicked the subordinate block.
     * @param subordinatePos pos of subordinate block.
     */
    public void onSubordinateBlockClicked(ServerPlayerEntity player, BlockPos subordinatePos) {
    }

    /**
     * Returns the blockstate on tileentity pos.
     */
    public BlockState getState() {
        Objects.requireNonNull(level);

        return level.getBlockState(worldPosition);
    }

    public T getGame() {
        return game;
    }
}
