package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.entity.player.PlayerEntity;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.Markers;
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
import ru.timeconqueror.lootgames.api.packet.SPacketGameUpdate;
import ru.timeconqueror.lootgames.api.task.TETaskScheduler;
import ru.timeconqueror.lootgames.common.advancement.EndGameTrigger;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.common.packet.game.SPChangeStage;
import ru.timeconqueror.lootgames.common.world.gen.DungeonGenerator;
import ru.timeconqueror.lootgames.common.world.gen.GameDungeonStructure;
import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
import ru.timeconqueror.timecore.api.util.ChatUtils;
import ru.timeconqueror.timecore.api.util.NetworkUtils;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;

public abstract class LootGame<STAGE extends LootGame.Stage, G extends LootGame<STAGE, G>> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Marker DEBUG_MARKER = Markers.LOOTGAME.getMarker();

    protected GameMasterTile<G> masterTileEntity;
    protected TETaskScheduler taskScheduler;

    /**
     * Determines if tile entity is placed, but was never read from nbt.
     */
    private boolean justPlaced = true;
    private STAGE stage;

    public void setMasterTileEntity(GameMasterTile<G> masterTileEntity) {
        this.masterTileEntity = masterTileEntity;
    }

    /**
     * Method where you can init anything, that requires world and {@link #masterTileEntity} to be not null.
     */
    @OverridingMethodsMustInvokeSuper
    public void onLoad() {
        if (taskScheduler == null && isServerSide()) {
            taskScheduler = new TETaskScheduler(masterTileEntity);
        }

        if (justPlaced) {
            justPlaced = false;
            onPlace();
        }
    }

    /**
     * Called for both sides when tile entity is just placed in world and has never been write to and read from nbt.
     */
    public void onPlace() {
    }

    @OverridingMethodsMustInvokeSuper
    public void onTick() {
        if (isServerSide()) {
            taskScheduler.onUpdate();
        }

        if (getStage() != null) {
            getStage().onTick();
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
        NetworkUtils.forEachPlayerNearby(getGameCenter(), getBroadcastDistance(),
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
        NetworkUtils.forEachPlayerNearby(getGameCenter(), getBroadcastDistance(),
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
        masterTileEntity.onDestroy();
    }

    protected abstract BlockPos getGameCenter();

    /**
     * Returns default broadcast distance (radius) from dungeon central position.
     * You may use it, for example, for triggering advancements and sending text messages.
     */
    public int getBroadcastDistance() {
        return GameDungeonStructure.ROOM_WIDTH / 2 + 3;//3 - it is just extra block distance after passing dungeon wall. Not so much, not so little.
    }

    public void sendTo(PlayerEntity player, IFormattableTextComponent component) {
        NetworkUtils.sendMessage(player, component);
    }

    public void sendTo(PlayerEntity player, IFormattableTextComponent component, TextFormatting format) {
        sendTo(player, component.withStyle(format));
    }

    public void sendTo(PlayerEntity player, IFormattableTextComponent component, NotifyColor format) {
        sendTo(player, component, format.getColor());
    }

    public void sendToNearby(IFormattableTextComponent component) {
        NetworkUtils.sendForEachPlayerNearby(getGameCenter(), getBroadcastDistance(), component);
    }

    public void sendToNearby(IFormattableTextComponent component, TextFormatting format) {
        sendToNearby(component.withStyle(format));
    }

    public void sendToNearby(IFormattableTextComponent component, NotifyColor format) {
        sendToNearby(component, format.getColor());
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
        LOGGER.debug(DEBUG_MARKER, prepareLogMsg("serialized stage: '{}'"), getStage());
    }

    /**
     * Reads data that comes from both server->client syncing and world restoring from save
     */
    @OverridingMethodsMustInvokeSuper
    public void readCommonNBT(CompoundNBT compound) {
        justPlaced = false;
        setStage(deserializeStage(this, compound));
        LOGGER.debug(DEBUG_MARKER, prepareLogMsg("deserialized stage: '{}'"), getStage());
    }

    /**
     * Only for usage from {@link #onPlace()}.
     * Will be synced later.
     *
     * @param stage initial stage of game
     */
    public void setupInitialStage(STAGE stage) {
        setStage(stage);
        LOGGER.debug(DEBUG_MARKER, prepareLogMsg("set up initial stage '{}'"), stage);
        stage.onStart();
    }

    public void switchStage(@Nullable STAGE stage) {
        STAGE old = this.getStage();
        if (old != null) old.onEnd();

        setStage(stage);

        LOGGER.debug(DEBUG_MARKER, prepareLogMsg("switching from stage '{}' to '{}'"), old, stage);
        onStageUpdate(old, stage);

        if (this.getStage() != null) this.getStage().onStart();
    }

    protected void onStageUpdate(STAGE oldStage, STAGE newStage) {
        if (isServerSide()) {
            saveData();
            sendUpdatePacket(new SPChangeStage(this));
        }
    }

    @Nullable
    public abstract STAGE createStageFromNBT(String id, CompoundNBT stageNBT);

    @Nullable
    public STAGE getStage() {
        return stage;
    }

    private void setStage(@Nullable STAGE stage) {
        this.stage = stage;
    }

    public abstract void onDestroy();

    public abstract static class Stage {
        /**
         * Called for both logical sides when the game was switched to this stage.
         * For server: called only when you manually change stage on server side via {@link #setupInitialStage(Stage)} or {@link #switchStage(Stage)}
         * For client: called every time server sends new state, including deserializing from saved nbt.
         */
        protected void onStart() {

        }

        /**
         * Called on every tick for both logical sides.
         */
        protected void onTick() {

        }

        /**
         * Called for both logical sides when the game was switched from this stage to another one.
         */
        protected void onEnd() {

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

    public static <STAGE extends Stage> void serializeStage(LootGame<STAGE, ?> game, CompoundNBT nbt) {
        Stage stage = game.getStage();
        if (stage != null) {
            CompoundNBT stageWrapper = new CompoundNBT();
            stageWrapper.put("stage", stage.serialize());
            stageWrapper.putString("id", stage.getID());
            nbt.put("stage_wrapper", stageWrapper);
        }
    }

    @Nullable
    public static <S extends Stage, T extends LootGame<S, T>> S deserializeStage(LootGame<S, T> game, CompoundNBT nbt) {
        if (nbt.contains("stage_wrapper")) {
            CompoundNBT stageWrapper = nbt.getCompound("stage_wrapper");
            return game.createStageFromNBT(stageWrapper.getString("id"), stageWrapper.getCompound("stage"));
        } else {
            return null;
        }
    }

    private String prepareLogMsg(String str) {
        return getClass().getSimpleName() + ": " + TextFormatting.DARK_BLUE + str;
    }
}
