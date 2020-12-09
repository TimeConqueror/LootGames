package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
import ru.timeconqueror.lootgames.api.packet.SPacketGameUpdate;
import ru.timeconqueror.lootgames.api.task.TETaskScheduler;
import ru.timeconqueror.lootgames.common.advancement.EndGameTrigger;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.common.packet.game.SPChangeStage;
import ru.timeconqueror.lootgames.common.world.gen.DungeonGenerator;
import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
import ru.timeconqueror.timecore.util.ChatUtils;
import ru.timeconqueror.timecore.util.NetworkUtils;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;

public abstract class LootGame<T extends LootGame<T>> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Marker DEBUG_MARKER = MarkerManager.getMarker("LOOTGAME");

    protected TileEntityGameMaster<T> masterTileEntity;
    protected TETaskScheduler taskScheduler;
    @Nullable
    protected Stage<T> stage;
    private boolean firstTickPassed;
    /**
     * Determines if tile entity is placed, but was never read from nbt.
     */
    private boolean justPlaced = true;

    public void setMasterTileEntity(TileEntityGameMaster<T> masterTileEntity) {
        this.masterTileEntity = masterTileEntity;
    }

    /**
     * Method where you need to init anything, that requires world and {@link #masterTileEntity} to be not null.
     * Will be called on the first tick.
     */
    @OverridingMethodsMustInvokeSuper
    public void onLoad() {
        if (taskScheduler == null && isServerSide()) {
            taskScheduler = new TETaskScheduler(masterTileEntity);
        }

        if (justPlaced) {
            onPlace();
        }
    }

    /**
     * Called when tile entity is just placed in world and has never been read from nbt.
     */
    public void onPlace() {
    }

    @OverridingMethodsMustInvokeSuper
    public void onTick() {
        if (!firstTickPassed) {
            onLoad();
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
        return !getWorld().isClientSide();
    }

    @NotNull
    public World getWorld() {
        return Objects.requireNonNull(masterTileEntity.getLevel());
    }

    public BlockPos getMasterPos() {
        return masterTileEntity.getBlockPos();
    }

    /**
     * You should call it, when the game is won.
     * Overriding is fine.
     */
    @OverridingMethodsMustInvokeSuper
    protected void triggerGameWin() {
        onGameEnd();
        NetworkUtils.forEachPlayerNearby(getCentralRoomPos(), getBroadcastDistance(),
                player -> {
                    LGAdvancementTriggers.END_GAME.trigger(player, EndGameTrigger.TYPE_WIN);
                    player.sendMessage(ChatUtils.format(new TranslationTextComponent("msg.lootgames.win"), TextFormatting.GREEN), player.getUUID());
                });
    }

    /**
     * You should call it, when the game is lost.
     * Overriding is fine.
     */
    @OverridingMethodsMustInvokeSuper
    protected void triggerGameLose() {
        onGameEnd();
        NetworkUtils.forEachPlayerNearby(getCentralRoomPos(), getBroadcastDistance(),
                player -> {
                    LGAdvancementTriggers.END_GAME.trigger(player, EndGameTrigger.TYPE_LOSE);
                    player.sendMessage(ChatUtils.format(new TranslationTextComponent("msg.lootgames.lose"), TextFormatting.DARK_PURPLE), player.getUUID());
                });
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
        return 32 /*instead of GameDungeonStructure.ROOM_WIDTH FIXME*/ / 2 + 3;//3 - it is just extra block distance after passing dungeon wall. Not so much, not so little.
    }

    public void sendForEachNearby(IFormattableTextComponent component) {
        NetworkUtils.sendForEachPlayerNearby(getCentralRoomPos(), getBroadcastDistance(), component);
    }

    public void sendForEachNearby(IFormattableTextComponent component, TextFormatting format) {
        sendForEachNearby(component.withStyle(format));
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
        masterTileEntity.setChanged();
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
        Chunk chunk = getWorld().getChunkAt(masterTileEntity.getBlockPos());
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
        serializeStage(this, compound);
        LOGGER.debug(DEBUG_MARKER, color("Serialized stage: '{}'"), stage);
    }

    /**
     * Reads data that comes from both server->client syncing and world restoring from save
     */
    @OverridingMethodsMustInvokeSuper
    public void readCommonNBT(CompoundNBT compound) {
        justPlaced = false;
        stage = deserializeStage(this, compound);
        LOGGER.debug(DEBUG_MARKER, color("Deserialized stage: '{}'"), stage);
    }

    public void switchStage(@Nullable Stage<T> stage) {
        Stage<T> old = this.stage;
        if (old != null) old.onEnd(this);

        this.stage = stage;

        LOGGER.debug(DEBUG_MARKER, color("Switching from stage '{}' to '{}'"), old, stage);
        onStageUpdate(old, stage);

        if (this.stage != null) this.stage.onStart(this);
    }

    protected void onStageUpdate(Stage<T> oldStage, Stage<T> newStage) {
        if (isServerSide()) {
            saveData();
            sendUpdatePacket(new SPChangeStage(this));
        }
    }

    @Nullable
    public abstract Stage<T> createStageFromNBT(String id, CompoundNBT stageNBT);

    @Nullable
    public Stage<T> getStage() {
        return stage;
    }

    public abstract static class Stage<T extends LootGame<T>> {
        /**
         * Called when the game was switched to this stage.
         */
        protected void onStart(LootGame<T> game) {

        }

        protected void onTick(LootGame<T> game) {

        }

        /**
         * Called when the game was switched from this stage to another one.
         */
        protected void onEnd(LootGame<T> game) {

        }

        public CompoundNBT serialize() {
            return new CompoundNBT();
        }

        public abstract String getID();

        @Override
        public String toString() {
            return getID();
        }
    }

    public static void serializeStage(LootGame<?> game, CompoundNBT nbt) {
        Stage<?> stage = game.getStage();
        if (stage != null) {
            CompoundNBT stageWrapper = new CompoundNBT();
            stageWrapper.put("stage", stage.serialize());
            stageWrapper.putString("id", stage.getID());
            nbt.put("stage_wrapper", stageWrapper);
        }
    }

    @Nullable
    public static <T extends LootGame<T>> Stage<T> deserializeStage(LootGame<T> game, CompoundNBT nbt) {
        if (nbt.contains("stage_wrapper")) {
            CompoundNBT stageWrapper = nbt.getCompound("stage_wrapper");
            return game.createStageFromNBT(stageWrapper.getString("id"), stageWrapper.getCompound("stage"));
        } else {
            return null;
        }
    }

    private static String color(String str) {
        return TextFormatting.DARK_BLUE + str;
    }
}
