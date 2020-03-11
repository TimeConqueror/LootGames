package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.PacketDistributor;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.advancement.LGAdvancementManager;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
import ru.timeconqueror.lootgames.api.packet.SPacketGameUpdate;
import ru.timeconqueror.lootgames.api.task.TEPostponeTaskScheduler;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.common.world.gen.DungeonGenerator;
import ru.timeconqueror.timecore.api.util.NetworkUtils;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;

import static ru.timeconqueror.lootgames.common.advancement.EndGameTrigger.TYPE_LOSE;
import static ru.timeconqueror.lootgames.common.advancement.EndGameTrigger.TYPE_WIN;

public abstract class LootGame<T extends LootGame<T>> {
    protected TileEntityGameMaster<T> masterTileEntity;
    protected TEPostponeTaskScheduler serverTaskPostponer;
    protected Stage stage;

    public void setMasterTileEntity(TileEntityGameMaster<T> masterTileEntity) {
        this.masterTileEntity = masterTileEntity;
    }

    public void init() {
        this.serverTaskPostponer = new TEPostponeTaskScheduler(getWorld());
    }

    @OverridingMethodsMustInvokeSuper
    public void onTick() {
        if (isServerSide()) {
            serverTaskPostponer.onUpdate();
        }

        if (stage != null) {
            if (stage.side == Stage.BOTH || (stage.side == Stage.SERVER && isServerSide() || (stage.side == Stage.CLIENT && !isServerSide()))) {
                stage.onTick();
            }
        }
    }

    public T typed() {
        return ((T) this);
    }

    public boolean isServerSide() {
        World world = Objects.requireNonNull(masterTileEntity.getWorld());
        return !world.isRemote;
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
        nbt.put("task_scheduler", serverTaskPostponer.serializeNBT());
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
     * Sends update packet to the client with given {@link CompoundNBT} to all players, tracking the game.
     */
    public void sendUpdatePacket(IServerGamePacket<T> packet) {
        Chunk chunk = getWorld().getChunkAt(masterTileEntity.getPos());
        LGNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new SPacketGameUpdate(this, packet));
    }

    /**
     * Fired on client when {@link IServerGamePacket} comes from server.
     */
    public void onUpdatePacket(IServerGamePacket<T> packet) {
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
        compound.put("stage", stage.serialize());
    }

    /**
     * Reads data that comes from both server->client syncing and world restoring from save
     */
    @OverridingMethodsMustInvokeSuper
    public void readCommonNBT(CompoundNBT compound) {
        stage = createStageFromNBT(compound.getCompound("stage"));
    }

    public void switchStage(@Nullable Stage stage) {
        Stage old = this.stage;
        if (old != null) old.onEnd();

        this.stage = stage;

        onStageUpdate(old, stage);
        if (this.stage != null) this.stage.onStart();
    }

    protected void onStageUpdate(Stage oldStage, Stage newStage) {
        saveData();
    }

    public abstract Stage createStageFromNBT(CompoundNBT stageNBT);

    public Stage getStage() {
        return stage;
    }

    public abstract static class Stage {
        public static final int CLIENT = 0;
        public static final int SERVER = 1;
        public static final int BOTH = 2;

        @MagicConstant(intValues = {CLIENT, SERVER, BOTH})
        private int side;

        public Stage(@MagicConstant(intValues = {CLIENT, SERVER, BOTH}) int side) {
            this.side = side;
        }

        public void onStart() {

        }

        public void onTick() {

        }

        public void onEnd() {

        }

        public CompoundNBT serialize() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("id", getID());

            return nbt;
        }

        public abstract String getID();
    }
}
