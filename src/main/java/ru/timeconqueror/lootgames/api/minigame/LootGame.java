package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.packet.SMessageGameUpdate;
import ru.timeconqueror.lootgames.api.task.TEPostponeTaskScheduler;
import ru.timeconqueror.lootgames.api.tileentity.TileEntityGameMaster;
import ru.timeconqueror.lootgames.packets.NetworkHandler;
import ru.timeconqueror.lootgames.world.gen.DungeonGenerator;

public abstract class LootGame {
    protected TileEntityGameMaster<?> masterTileEntity;
    protected TEPostponeTaskScheduler serverTaskPostponer;

    private Runnable winCallback;
    private Runnable loseCallback;
    private Runnable endGameCallback;

    public void setMasterTileEntity(TileEntityGameMaster<?> masterTileEntity) {
        this.masterTileEntity = masterTileEntity;
    }

    public void init() {
        this.serverTaskPostponer = new TEPostponeTaskScheduler(masterTileEntity);
    }

    public void onTick() {
        if (isServerSide()) {
            serverTaskPostponer.onUpdate();
        }
    }

    public boolean isServerSide() {
        return !masterTileEntity.getWorld().isRemote;
    }

    public World getWorld() {
        return masterTileEntity.getWorld();
    }

    public BlockPos getMasterPos() {
        return masterTileEntity.getPos();
    }

    /**
     * You should call this, when the game is won. If you want to run something while it is called, use
     * {@link #setOnWin(Runnable)}.
     */
    protected final void winGame() {
        onGameEnded();
        if (winCallback != null) winCallback.run();
    }

    /**
     * You should call it, when the game is lost. If you want to run something while it is called, use
     * {@link #setOnLose(Runnable)}.
     */
    protected final void loseGame() {
        onGameEnded();
        if (loseCallback != null) loseCallback.run();
    }

    private void onGameEnded() {
        if (endGameCallback != null) endGameCallback.run();
        DungeonGenerator.resetUnbreakablePlayfield(getWorld(), getRoomFloorPos());
    }

    /**
     * Should return the position of block, that is a part of shielded dungeon floor or has a neighbor, that is a shielded floor block.
     * Used, for example, to recursively reset shield of unbreakability on dungeon floor.
     */
    protected abstract BlockPos getRoomFloorPos();

    /**
     * Used to set the code block to be called when the game is won.
     */
    public void setOnWin(Runnable onWin) {
        this.winCallback = onWin;
    }

    /**
     * Used to set the code block to be called when the game is lost.
     */
    public void setOnLose(Runnable onLose) {
        this.loseCallback = onLose;
    }

    /**
     * Used to set the code block to be called when the game is ended (won or lost).
     */
    public void setOnGameEnded(Runnable onEnd) {
        this.endGameCallback = onEnd;
    }

    /**
     * Saves current data to the disk and sends update to client.
     */
    public void saveDataAndSendToClient() {
        masterTileEntity.setBlockToUpdateAndSave();
    }

    /**
     * Saves current data to the disk without sending update to client.
     */
    public void saveData() {
        masterTileEntity.markDirty();
    }

    /**
     * Writes data used only to save on server.
     * Overriding is fine.
     */
    public NBTTagCompound writeNBTForSaving() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeCommonNBT(nbt);
        nbt.setTag("task_scheduler", serverTaskPostponer.serializeNBT());
        return nbt;
    }

    /**
     * Reads data from server-saved NBT.
     * Overriding is fine.
     */
    public void readNBTFromSave(NBTTagCompound compound) {
        readCommonNBT(compound);
        serverTaskPostponer.deserializeNBT((NBTTagList) compound.getTag("task_scheduler"));
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
     * Sends update packet with given {@link NBTTagCompound} to all players, tracking the game.
     *
     * @param key allows to understand what packet should we read via {@link #onUpdatePacket(String, NBTTagCompound)}
     */
    public void sendUpdatePacket(String key, NBTTagCompound compoundToSend) {
        NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), masterTileEntity.getPos().getX(), masterTileEntity.getPos().getY(), masterTileEntity.getPos().getZ(), -1);
        NetworkHandler.INSTANCE.sendToAllTracking(new SMessageGameUpdate(masterTileEntity.getPos(), key, compoundToSend), point);
    }

    /**
     * Fired on client when {@link SMessageGameUpdate} comes from server.
     *
     * @param key allows to understand what packet did we send via {@link #sendUpdatePacket(String, NBTTagCompound)}
     */
    @SideOnly(Side.CLIENT)
    public void onUpdatePacket(String key, @Nullable NBTTagCompound compoundIn) {
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
