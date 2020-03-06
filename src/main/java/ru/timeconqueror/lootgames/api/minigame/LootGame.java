package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.PacketDistributor;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.packet.IGamePacket;
import ru.timeconqueror.lootgames.api.packet.SPacketGameUpdate;
import ru.timeconqueror.lootgames.api.task.TEPostponeTaskScheduler;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.common.world.gen.DungeonGenerator;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;

public abstract class LootGame {
    protected TileEntityGameMaster<?> masterTileEntity;
    protected TEPostponeTaskScheduler serverTaskPostponer;

    public void setMasterTileEntity(TileEntityGameMaster<?> masterTileEntity) {
        this.masterTileEntity = masterTileEntity;
    }

    public void init() {
        this.serverTaskPostponer = new TEPostponeTaskScheduler(getWorld());
    }

    public void onTick() {
        if (isServerSide()) {
            serverTaskPostponer.onUpdate();
        }
    }

    public boolean isServerSide() {
        World world = Objects.requireNonNull(masterTileEntity.getWorld());
        return !world.isRemote;
    }

    public World getWorld() {
        return Objects.requireNonNull(masterTileEntity.getWorld());
    }

    public BlockPos getMasterPos() {
        return masterTileEntity.getPos();
    }

    /**
     * You should call it, when the game is won.
     * Overriding is fine.
     */
    @OverridingMethodsMustInvokeSuper
    protected void triggerGameWin() {
        onGameEnd();
//        NetworkUtils.forEachPlayerNearby(getCentralRoomPos(), getBroadcastDistance(), player -> AdvancementManager.WIN_GAME.trigger(player, "win"));//fixme uncomment
    }

    /**
     * You should call it, when the game is lost.
     * Overriding is fine.
     */
    @OverridingMethodsMustInvokeSuper
    protected void triggerGameLose() {
        onGameEnd();
//        NetworkUtils.forEachPlayerNearby(getCentralRoomPos(), getBroadcastDistance(), player -> AdvancementManager.WIN_GAME.trigger(player, "lose"));//fixme uncomment
    }

    /**
     * Will be called when the game ends.
     * Overriding is fine.
     */
    protected void onGameEnd() {
        DungeonGenerator.resetUnbreakablePlayField(getWorld(), getRoomFloorPos());
        masterTileEntity.destroyGameBlocks();
    }

    protected abstract BlockPos getCentralRoomPos();

    /**
     * Returns default broadcast distance from dungeon central position.
     * You may use it, for example, for triggering advancements and sending text messages.
     */
    public int getBroadcastDistance() {
        return DungeonGenerator.PUZZLEROOM_CENTER_TO_BORDER + 3;//3 - it is just extra block distance after passing dungeon wall. Not so much, not so little.
    }

    /**
     * Should return the position of block, that is a part of shielded dungeon floor or has a neighbor, that is a shielded floor block.
     * Used, for example, to recursively reset shield of unbreakability on dungeon floor.
     */
    protected abstract BlockPos getRoomFloorPos();

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
    @OverridingMethodsMustInvokeSuper
    public CompoundNBT writeNBTForSaving() {
        CompoundNBT nbt = new CompoundNBT();
        nbt = writeCommonNBT(nbt);
        nbt.put("task_scheduler", serverTaskPostponer.serializeNBT());
        return nbt;
    }

    /**
     * Reads data from server-saved NBT.
     * Overriding is fine.
     */
    @OverridingMethodsMustInvokeSuper
    public void readNBTFromSave(CompoundNBT compound) {
        readCommonNBT(compound);

        ListNBT schedulerTag = (ListNBT) compound.get("task_scheduler");

        serverTaskPostponer.deserializeNBT(Objects.requireNonNull(schedulerTag));
    }

    /**
     * Writes the data used only for sending to client, not to save it.
     * Overriding is fine.
     */
    @OverridingMethodsMustInvokeSuper
    public CompoundNBT writeNBTForClient() {
        CompoundNBT nbt = new CompoundNBT();
        nbt = writeCommonNBT(nbt);
        return nbt;
    }

    /**
     * Sends update packet to the client with given {@link CompoundNBT} to all players, tracking the game.
     */
    public void sendUpdatePacket(IGamePacket<LootGame> packet) {
        Chunk chunk = getWorld().getChunkAt(masterTileEntity.getPos());
        LGNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new SPacketGameUpdate(this, packet));
    }

    /**
     * Fired on client when {@link IGamePacket} comes from server.
     */
    public void onUpdatePacket(IGamePacket<LootGame> packet) {
        packet.runOnReceptionSide(this);
    }

    /**
     * Reads the data that comes to client side.
     * Overriding is fine.
     */
    @OverridingMethodsMustInvokeSuper
    public void readNBTFromClient(CompoundNBT compound) {
        readCommonNBT(compound);
    }

    /**
     * Writes data to be used both server->client syncing and world saving
     */
    @OverridingMethodsMustInvokeSuper
    public CompoundNBT writeCommonNBT(CompoundNBT compound) {
        return compound;
    }

    /**
     * Reads data that comes from both server->client syncing and world restoring from save
     */
    @OverridingMethodsMustInvokeSuper
    public void readCommonNBT(CompoundNBT compound) {
    }
}
