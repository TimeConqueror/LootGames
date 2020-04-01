package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.advancement.LGAdvancementManager;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
import ru.timeconqueror.lootgames.api.packet.SPacketGameUpdate;
import ru.timeconqueror.lootgames.api.task.TETaskScheduler;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.common.packet.game.SPacketChangeStage;
import ru.timeconqueror.lootgames.common.world.gen.DungeonGenerator;
import ru.timeconqueror.timecore.api.util.NetworkUtils;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;

import static ru.timeconqueror.lootgames.common.advancement.EndGameTrigger.TYPE_LOSE;
import static ru.timeconqueror.lootgames.common.advancement.EndGameTrigger.TYPE_WIN;

public abstract class LootGame<T extends LootGame<T>> {
    protected TileEntityGameMaster<T> masterTileEntity;
    protected TETaskScheduler taskScheduler;
    @Nullable
    protected Stage<T> stage;
    private boolean firstTickPassed;

    public void setMasterTileEntity(TileEntityGameMaster<T> masterTileEntity) {
        this.masterTileEntity = masterTileEntity;
    }

    /**
     * Method where you need to init anything, that requires world and {@link #masterTileEntity} to be not null.
     * Will be called on the first tick.
     */
    @OverridingMethodsMustInvokeSuper
    protected void init() {
        if (taskScheduler == null && isServerSide()) {
            taskScheduler = new TETaskScheduler(masterTileEntity);
        }
    }

    @OverridingMethodsMustInvokeSuper
    public void onTick() {
        if (!firstTickPassed) {
            init();
            firstTickPassed = true;
        }

        if (isServerSide()) {
            taskScheduler.onUpdate();
        }

        if (stage != null) {
            stage.onTick(this);
        }
    }

    public boolean isServerSide() {
        return !getWorld().isRemote;
    }

    @NotNull
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
        NetworkUtils.forEachPlayerNearby(getCentralRoomPos(), getBroadcastDistance(),
                player -> LGAdvancementManager.END_GAME.trigger(player, TYPE_WIN));
    }

    /**
     * You should call it, when the game is lost.
     * Overriding is fine.
     */
    @OverridingMethodsMustInvokeSuper
    protected void triggerGameLose() {
        onGameEnd();
        NetworkUtils.forEachPlayerNearby(getCentralRoomPos(), getBroadcastDistance(),
                player -> LGAdvancementManager.END_GAME.trigger(player, TYPE_LOSE));
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
    public void writeNBTForSaving(CompoundNBT nbt) {
        writeCommonNBT(nbt);
        nbt.put("task_scheduler", taskScheduler.serializeNBT());
    }

    /**
     * Reads data from server-saved NBT.
     * Overriding is fine.
     */
    @OverridingMethodsMustInvokeSuper
    public void readNBTFromSave(CompoundNBT compound) {
        readCommonNBT(compound);

        ListNBT schedulerTag = (ListNBT) compound.get("task_scheduler");

        taskScheduler = new TETaskScheduler(masterTileEntity);
        taskScheduler.deserializeNBT(Objects.requireNonNull(schedulerTag));
    }

    /**
     * Sends update packet to the client with given {@link CompoundNBT} to all players, tracking the game.
     */
    public void sendUpdatePacket(IServerGamePacket packet) {
        Chunk chunk = getWorld().getChunkAt(masterTileEntity.getPos());
        LGNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new SPacketGameUpdate(this, packet));
    }

    /**
     * Fired on client when {@link IServerGamePacket} comes from server.
     */
    public void onUpdatePacket(IServerGamePacket packet) {
        packet.runOnClient(this);
    }

    /**
     * Writes the data used only for sending to client, not to save it.
     * Overriding is fine.
     */
    @OverridingMethodsMustInvokeSuper
    public void writeNBTForClient(CompoundNBT nbt) {
        writeCommonNBT(nbt);
    }

    /**
     * Reads the data that comes to client side.
     * Overriding is fine.
     */
    @OverridingMethodsMustInvokeSuper
    public void readNBTAtClient(CompoundNBT compound) {
        readCommonNBT(compound);
    }

    /**
     * Writes data to be used both server->client syncing and world saving
     */
    @OverridingMethodsMustInvokeSuper
    public void writeCommonNBT(CompoundNBT compound) {
        if (stage != null) {
            compound.put("stage", stage.serialize());
        }
    }

    /**
     * Reads data that comes from both server->client syncing and world restoring from save
     */
    @OverridingMethodsMustInvokeSuper
    public void readCommonNBT(CompoundNBT compound) {
        stage = compound.contains("stage") ? createStageFromNBT(compound.getCompound("stage")) : null;
//        stage.onStart();//FIXME FIXME FIXME
    }

    public void switchStage(@Nullable Stage<T> stage) {
        Stage<T> old = this.stage;
        if (old != null) old.onEnd(this);

        this.stage = stage;

        onStageUpdate(old, stage);

        if (this.stage != null) this.stage.onStart(this);
    }


    //    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void onStageUpdate(Stage<T> oldStage, Stage<T> newStage) {
        if (isServerSide()) {
            saveData();
            sendUpdatePacket(new SPacketChangeStage(this));
        }
    }

    @Nullable
    public abstract Stage<T> createStageFromNBT(CompoundNBT stageNBT);

    @Nullable
    public Stage<T> getStage() {
        return stage;
    }

    public abstract static class Stage<T extends LootGame<T>> {

        protected void onStart(LootGame<T> game) {

        }

        protected void onTick(LootGame<T> game) {

        }

        protected void onEnd(LootGame<T> game) {

        }

        public CompoundNBT serialize() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("id", getID());

            return nbt;
        }

        public abstract String getID();
    }
}
