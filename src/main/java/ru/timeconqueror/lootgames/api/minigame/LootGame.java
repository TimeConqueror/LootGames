package ru.timeconqueror.lootgames.api.minigame;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.Markers;
import ru.timeconqueror.lootgames.api.minigame.event.EndGameEvent;
import ru.timeconqueror.lootgames.api.minigame.event.GameEvents;
import ru.timeconqueror.lootgames.api.minigame.event.StartStageEvent;
import ru.timeconqueror.lootgames.api.minigame.event.SwitchStageEvent;
import ru.timeconqueror.lootgames.api.room.GameProgress;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.task.TaskScheduler;
import ru.timeconqueror.lootgames.common.advancement.EndGameTrigger;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.common.packet.game.SPChangeStage;
import ru.timeconqueror.lootgames.common.packet.room.SSyncGamePacket;
import ru.timeconqueror.lootgames.minigame.GameEventBus;
import ru.timeconqueror.lootgames.minigame.GameNetworkImpl;
import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.lootgames.room.GameSerializer;
import ru.timeconqueror.lootgames.room.ServerRoom;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;
import ru.timeconqueror.timecore.api.util.Auxiliary;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;

@Log4j2
public abstract class LootGame<STAGE extends Stage> {
    public static final Marker DEBUG_MARKER = Markers.LOOTGAME;
    @Getter
    private final ResourceLocation id;
    @Getter
    protected Room room;
    /**
     * Exists only on server-side
     */
    protected TaskScheduler taskScheduler;
    protected GameNetwork network;
    @Getter
    protected GameEventBus eventBus;
    @Getter
    private STAGE stage;
    @Getter
    private int age;

    public LootGame(ResourceLocation id, Room room) {
        this.id = id;

        this.room = room;
        if (isServerSide()) {
            taskScheduler = new TaskScheduler(room.getLevel());
        }
        network = new GameNetworkImpl(room, this);
        eventBus = new GameEventBus(this);
    }

    protected abstract STAGE makeInitialStage();

    public final void start() {
        setupInitialStage();
        Stage startStage = getStage();
        eventBus.post(GameEvents.START_GAME);
        room.forEachInRoom(player -> LGNetwork.sendToPlayer((ServerPlayer) player, new SSyncGamePacket(room.getProgress(), this, true)));
        startStage.postInit();
        onStageStart(false);
    }

    @OverridingMethodsMustInvokeSuper
    public void onTick() {
        if (isServerSide()) {
            taskScheduler.onTick();
        }

        getStage().onTick();
        age++;
    }

    /**
     * You should call it, when the game is won.
     * Server-side-only method.
     */
    protected void triggerGameWin() {
        if (!isServerSide()) return;

        room.forEachInRoom(player -> LGAdvancementTriggers.END_GAME.trigger((ServerPlayer) player, EndGameTrigger.TYPE_WIN));

        EndGameEvent event = new EndGameEvent();
        eventBus.post(GameEvents.WIN_GAME_PRE, event);
        if (!event.doNotUseDefaultBehaviour()) {
            room.forEachInRoom(player -> network.sendTo(player, Component.translatable("msg.lootgames.win"), NotifyColor.SUCCESS));

            getLevel().playSound(null, getGameCenter(), LGSounds.GAME_WIN, SoundSource.MASTER, 0.75F, 1.0F);
        }

        eventBus.post(GameEvents.WIN_GAME_POST);
        endGame();
    }

    /**
     * You should call it, when the game is lost.
     * Server-side-only method.
     */
    public void triggerGameLose() {
        if (!isServerSide()) return;

        room.forEachInRoom(player -> LGAdvancementTriggers.END_GAME.trigger((ServerPlayer) player, EndGameTrigger.TYPE_LOSE));

        EndGameEvent event = new EndGameEvent();
        eventBus.post(GameEvents.LOSE_GAME_PRE, event);
        if (!event.doNotUseDefaultBehaviour()) {
            room.forEachInRoom(player -> network.sendTo(player, Component.translatable("msg.lootgames.lose"), NotifyColor.FAIL));
            getLevel().playSound(null, getGameCenter(), LGSounds.GAME_LOSE, SoundSource.MASTER, 0.75F, 1.0F);
        }
        eventBus.post(GameEvents.LOSE_GAME_POST);
        endGame();
    }

    private void endGame() {
        eventBus.post(GameEvents.END_GAME);
        ServerRoom serverRoom = (ServerRoom) room;
        serverRoom.finishGame();
    }

    public void generateRewards() {

    }

    public abstract BlockPos getGameCenter();

    public boolean isServerSide() {
        return !isClientSide();
    }

    public boolean isClientSide() {
        return getLevel().isClientSide();
    }

    @NotNull
    public Level getLevel() {
        return Objects.requireNonNull(room.getLevel());
    }

    @OverridingMethodsMustInvokeSuper
    public void writeNBT(CompoundTag nbt, SerializationType type) {
        if (type == SerializationType.SAVE) {
            nbt.put("task_scheduler", taskScheduler.serializeNBT());
        }

        GameSerializer.serializeStage(this, nbt, type);
        log.debug(DEBUG_MARKER, formatLogMessage("stage '{}' was serialized for {}."), getStage(), type == SerializationType.SAVE ? "saving" : "syncing");
    }

    @OverridingMethodsMustInvokeSuper
    public void readNBT(CompoundTag nbt, SerializationType type) {
        if (type == SerializationType.SAVE) {
            ListTag schedulerTag = (ListTag) nbt.get("task_scheduler");

            taskScheduler = new TaskScheduler(room.getLevel());
            taskScheduler.deserializeNBT(Objects.requireNonNull(schedulerTag));
        }

        this.stage = GameSerializer.deserializeStage(this, nbt, type);
        log.debug(DEBUG_MARKER, formatLogMessage("stage '{}' was deserialized {}."), getStage(), type == SerializationType.SAVE ? "from saved file" : "on client");
        onStageStart(type == SerializationType.SYNC);
    }

    private void setupInitialStage() {
        STAGE startStage = makeInitialStage();
        log.debug(DEBUG_MARKER, formatLogMessage("initial stage is set: {}"), startStage);
        this.stage = startStage;
        eventBus.transferEventBus(stage);
        startStage.preInit();
    }

    public void switchStage(STAGE stage) {
        if (stage == this.getStage()) {
            return;
        }

        STAGE old = this.getStage();
        old.onEnd();

        log.debug(DEBUG_MARKER, formatLogMessage("switching from stage '{}' to '{}'."), old, stage);

        this.stage = stage;

        eventBus.post(GameEvents.SWITCH_STAGE, new SwitchStageEvent(old, stage));
        eventBus.transferEventBus(stage);
        if (isServerSide()) {
            stage.preInit();
            network.sendUpdatePacketToNearby(new SPChangeStage(this));
            stage.postInit();
        }

        onStageStart(isClientSide());
    }

    @Nullable
    public abstract STAGE createStageFromNBT(String id, CompoundTag stageNBT, SerializationType serializationType);

    /**
     * Called for both logical sides when the game was switched to this stage:
     * <ol>
     *     <li>by changing stage via {@link #setupInitialStage()} or {@link #switchStage(Stage)}</li>
     *     <li>by deserializing and syncing</li>
     * </ol>
     */
    private void onStageStart(boolean clientSide) {
        STAGE stage = this.stage;
        stage.onStart(clientSide);
        eventBus.post(GameEvents.START_STAGE, new StartStageEvent(stage, isClientSide()));
    }

    public Message logMessage(String message, Object... arguments) {
        return Auxiliary.makeLogMessage(formatLogMessage(message), arguments);
    }

    public String formatLogMessage(String message) {
        return ChatFormatting.DARK_BLUE + id.toString() + ": " + message;
    }

    public GameNetwork net() {
        return network;
    }

    public boolean isStarted() {
        return room.getProgress() == GameProgress.STARTED;
    }
}
