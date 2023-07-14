package ru.timeconqueror.lootgames.minigame.minesweeper;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.api.minigame.GameNetwork;
import ru.timeconqueror.lootgames.api.minigame.LootGameFactory;
import ru.timeconqueror.lootgames.api.minigame.NotifyColor;
import ru.timeconqueror.lootgames.api.minigame.event.GameEvents;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.task.TaskCreateExplosion;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.api.util.RewardUtils;
import ru.timeconqueror.lootgames.common.config.ConfigMS.Snapshot;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.common.packet.game.SPMSFieldChanged;
import ru.timeconqueror.lootgames.common.packet.game.SPMSGenBoard;
import ru.timeconqueror.lootgames.common.packet.game.SPMSResetFlags;
import ru.timeconqueror.lootgames.common.packet.game.SPMSSpawnLevelBeatParticles;
import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.lootgames.utils.EventBus;
import ru.timeconqueror.lootgames.utils.MouseClickType;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;
import ru.timeconqueror.timecore.api.util.RandHelper;
import ru.timeconqueror.timecore.api.util.holder.Holder;

import java.util.List;

//TODO lang keys for advancements
//TODO change generation random - depending on world is bad.
//TODO add leveling info in chat
//todo add game rules in chat
//TODO remove break particle before left click interact
//TODO if all non-bomb fields are revealed, finish the game
//TODO remove interact with opened fields

public class GameMineSweeper extends BoardLootGame {
    public static final String ADV_BEAT_LEVEL4 = "ms_level_4";

    public boolean cIsGenerated;

    private int currentLevel = 1;

    private final MSBoard board;

    private int ticks;
    private int attemptCount = 0;

    private boolean playRevealNeighboursSound = true;

    private Snapshot configSnapshot = null;

    public GameMineSweeper(ResourceLocation id, Room room) {
        super(id, room);
        board = new MSBoard(0, 0);

        EventBus events = getEventBus();
        events.addEventHandler(GameEvents.START_GAME, this::onStart);
        events.addEventHandler(GameEvents.SWITCH_STAGE, this::onStageSwitch);

        if (isClientSide()) {
            configSnapshot = Snapshot.stub(); // needs for first client ticks, because the config from readNBT is called a little bit later
        }
    }

    @Override
    protected BoardStage makeInitialStage() {
        return new StageWaiting();
    }

    private void onStart() {
        configSnapshot = LGConfigs.MINESWEEPER.snapshot();
        board.setSize(configSnapshot.getStage1().getBoardSize());
        board.setBombCount(configSnapshot.getStage1().getBombCount());
    }

    public boolean isBoardGenerated() {
        return board.isGenerated();
    }

    public int getCurrentBoardSize() {
        return board.size();
    }

    @Override
    public int getAllocatedBoardSize() {
        return configSnapshot.getStage4().getBoardSize();
    }

    private void onLevelSuccessfullyFinished() {
        if (currentLevel < 4) {
            net().sendUpdatePacketToNearby(new SPMSSpawnLevelBeatParticles());

            net().sendToAllInRoom(Component.translatable("msg.lootgames.stage_complete"), NotifyColor.SUCCESS);
            getLevel().playSound(null, getGameCenter(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.75F, 1.0F);

            Snapshot.StageSnapshot stageSnapshot = configSnapshot.getStageByIndex(currentLevel + 1);

            board.resetBoard(stageSnapshot.getBoardSize(), stageSnapshot.getBombCount());
            currentLevel++;
            net().saveAndSync();
        } else {
            currentLevel++;
            triggerGameWin();
        }
    }


    @Override
    protected void triggerGameWin() {
        super.triggerGameWin();

        genLootChests(room.getPlayers()
                .stream()
                .map(player -> (ServerPlayer) player)
                .toList());
    }

    private void genLootChests(List<ServerPlayer> players) {
        if (currentLevel < 2) {
            throw new RuntimeException("GenLootChests method was called in an appropriate time!");
        }

        BlockPos central = getGameCenter();

        if (currentLevel > 4) { // end of the game
            players.forEach(player -> LGAdvancementTriggers.END_GAME.trigger(player, ADV_BEAT_LEVEL4));
        }

        RewardUtils.spawnFourStagedReward(((ServerLevel) getLevel()), this, central, currentLevel - 1, LGConfigs.REWARDS.minesweeper);
    }

    @Override
    protected void triggerGameLose() {
        super.triggerGameLose();

        BlockPos expPos = getGameCenter();
        getLevel().explode(null, expPos.getX(), expPos.getY() + 1.5, expPos.getZ(), 9, Level.ExplosionInteraction.TNT);
    }

    private void onStageSwitch() {
        ticks = 0;
    }

    public MSBoard getBoard() {
        return board;
    }

    public int getTicks() {
        return ticks;
    }

    @Override
    public void writeNBT(CompoundTag nbt, SerializationType type) {
        super.writeNBT(nbt, type);

        if (type == SerializationType.SAVE) {
            if (isBoardGenerated()) {
                CompoundTag boardTag = board.writeNBTForSaving();
                nbt.put("board", boardTag);
            }

            nbt.putInt("attempt_count", attemptCount);
        } else {
            nbt.putBoolean("is_generated", isBoardGenerated());
            if (isBoardGenerated()) {
                CompoundTag boardTag = board.writeNBTForClient();
                nbt.put("board", boardTag);
            }
        }

        nbt.putInt("bomb_count", board.getBombCount());
        nbt.putInt("board_size", board.size());
        nbt.putInt("ticks", ticks);
        nbt.putInt("current_level", currentLevel);
        nbt.put("config_snapshot", Snapshot.serialize(configSnapshot));
    }

    @Override
    public void readNBT(CompoundTag nbt, SerializationType type) {
        super.readNBT(nbt, type);

        if (type == SerializationType.SAVE) {
            if (nbt.contains("board")) {
                CompoundTag boardTag = nbt.getCompound("board");
                board.readNBTFromSave(boardTag);
            }

            attemptCount = nbt.getInt("attempt_count");
        } else {
            cIsGenerated = nbt.getBoolean("is_generated");

            if (nbt.contains("board")) {
                CompoundTag boardTag = nbt.getCompound("board");
                board.readNBTFromClient(boardTag);
            }
        }

        board.setBombCount(nbt.getInt("bomb_count"));
        board.setSize(nbt.getInt("board_size"));
        ticks = nbt.getInt("ticks");
        currentLevel = nbt.getInt("current_level");
        configSnapshot = Snapshot.deserialize(nbt.get("config_snapshot"));
    }

    @Override
    public BoardStage createStageFromNBT(String id, CompoundTag stageNBT, SerializationType serializationType) {
        switch (id) {
            case StageWaiting.ID:
                return new StageWaiting();
            case StageDetonating.ID:
                return new StageDetonating(stageNBT.getInt("detonation_time"));
            case StageExploding.ID:
                return new StageExploding();
            default:
                throw new IllegalArgumentException("Unknown state with id: " + id + "!");
        }
    }

    public static class Factory implements LootGameFactory {
        @Override
        public void genOnPuzzleMasterClick(Level world, BlockPos puzzleMasterPos) {
            BlockPos floorCenterPos = puzzleMasterPos.offset(0, -3/*instead of GameDungeonStructure.MASTER_BLOCK_OFFSET*/ + 1, 0);
//            world.setBlockAndUpdate(floorCenterPos, LGBlocks.MS_ACTIVATOR.defaultBlockState());
        }
    }

    public class StageWaiting extends BoardStage {
        private static final String ID = "waiting";

        public StageWaiting() {
        }

        @Override
        protected void onClick(Player player, Pos2i pos, MouseClickType type) {
            if (isServerSide()) {
                ServerPlayer sPlayer = (ServerPlayer) player;
                getLevel().playSound(null, convertToBlockPos(pos), SoundEvents.NOTE_BLOCK_HAT.get(), SoundSource.MASTER, 0.6F, 0.8F);

                if (!board.isGenerated()) {
                    generateBoard(sPlayer, pos);
                } else {
                    playRevealNeighboursSound = true;

                    if (type == MouseClickType.LEFT) {
                        if (board.getMark(pos) == Mark.NO_MARK) {
                            revealField(sPlayer, pos);
                        }
                    } else {
                        if (player.isShiftKeyDown()) {
                            revealAllNeighbours(sPlayer, pos, false);
                        } else if (board.isHidden(pos)) {
                            swapFieldMark(pos);
                        }
                    }
                }
            }
        }

        public void generateBoard(ServerPlayer player, Pos2i clickedPos) {
            board.generate(clickedPos);
            net().sendUpdatePacketToNearby(new SPMSGenBoard(GameMineSweeper.this));
            revealField(player, clickedPos);
            net().save();
        }

        public void revealField(ServerPlayer player, Pos2i pos) {
            if (board.isHidden(pos)) {
                board.reveal(pos);

                Type type = board.getType(pos);

                net().sendUpdatePacketToNearby(new SPMSFieldChanged(pos, board.getField(pos)));

                if (type == Type.EMPTY) {
                    if (playRevealNeighboursSound) {
                        getLevel().playSound(null, convertToBlockPos(pos), LGSounds.MS_ON_EMPTY_REVEAL_NEIGHBOURS, SoundSource.MASTER, 0.6F, 1.0F);
                        playRevealNeighboursSound = false;
                    }

                    revealAllNeighbours(player, pos, true);
                } else if (type == Type.BOMB) {
                    triggerBombs(pos);
                }

                net().save();

                if (board.checkWin()) {
                    onLevelSuccessfullyFinished();
                }
            }
        }

        private void revealAllNeighbours(ServerPlayer player, Pos2i mainPos, boolean revealMarked) {
            if (!revealMarked) {
                if (board.isHidden(mainPos)) {
                    net().sendTo(player, Component.translatable("msg.lootgames.ms.reveal_on_hidden"), NotifyColor.WARN);
                    return;
                }

                int bombsAround = board.getType(mainPos).getId();
                if (bombsAround < 1) throw new IllegalStateException();

                int marked = 0;
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        Pos2i pos = mainPos.add(x, y);
                        if (board.hasFieldOn(pos) && board.getMark(pos) == Mark.FLAG) {
                            marked++;
                        }
                    }
                }

                if (marked != bombsAround) {
                    net().sendTo(player, Component.translatable("msg.lootgames.ms.reveal_invalid_mark_count"), NotifyColor.WARN);
                    return;
                }
            }

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0) {
                        continue;
                    }

                    Pos2i pos = mainPos.add(x, y);
                    if (board.hasFieldOn(pos) && board.isHidden(pos)) {
                        if (revealMarked || board.getMark(pos) != Mark.FLAG) {
                            revealField(player, pos);
                        }
                    }
                }
            }
        }

        public void swapFieldMark(Pos2i pos) {
            if (board.isHidden(pos)) {
                board.swapMark(pos);

                net().sendUpdatePacketToNearby(new SPMSFieldChanged(pos, board.getField(pos)));
                net().save();
            }

            if (board.checkWin()) {
                onLevelSuccessfullyFinished();
            }

        }

        private void triggerBombs(Pos2i pos) {
            getLevel().playSound(null, convertToBlockPos(pos), LGSounds.MS_BOMB_ACTIVATED, SoundSource.MASTER, 0.6F, 1.0F);

            switchStage(new StageDetonating());

            board.forEach((x, y) -> {
                if (board.isBomb(x, y)) {
                    board.reveal(x, y);
                }
            });

            net().sendToAllInRoom(Component.translatable("msg.lootgames.ms.bomb_touched"), NotifyColor.FAIL);
            net().saveAndSync();
            attemptCount++;
        }

        @Override
        public String getID() {
            return ID;
        }
    }

    public class StageDetonating extends BoardStage {
        private static final String ID = "detonating";
        private final int detonationTicks;

        public StageDetonating() {
            this(LGConfigs.MINESWEEPER.detonationTime.get());
        }

        public StageDetonating(int detonationTicks) {
            this.detonationTicks = detonationTicks;
        }

        @Override
        public void onTick() {
            if (isServerSide()) {
                if (ticks >= detonationTicks) {
                    if (attemptCount < LGConfigs.MINESWEEPER.attemptCount.get()) {
                        switchStage(new StageExploding());
                    } else {
                        if (currentLevel > 1) {
                            triggerGameWin();
                        } else {
                            triggerGameLose();
                        }
                    }
                }
            }

            ticks++;
        }

        public int getDetonationTicks() {
            return detonationTicks;
        }

        @Override
        public String getID() {
            return ID;
        }

        @Override
        public CompoundTag serialize(SerializationType serializationType) {
            CompoundTag nbt = super.serialize(serializationType);
            nbt.putInt("detonation_time", detonationTicks);
            return nbt;
        }
    }

    public class StageExploding extends BoardStage {
        private static final String ID = "exploding";

        public StageExploding() {
        }

        @Override
        public void postInit() {
            int longestDetTime = detonateBoard(currentLevel + 3, Level.ExplosionInteraction.NONE/*don't break blocks*/);
            ticks = longestDetTime + 20; //number represents some pause after detonating
        }

        @Override
        public void onTick() {
            if (isServerSide()) {
                ticks--;

                if (ticks <= 0) {
                    GameNetwork net = net();
                    net.sendToAllInRoom(Component.translatable("msg.lootgames.ms.new_attempt"), NotifyColor.NOTIFY);
                    net.sendToAllInRoom(Component.translatable("msg.lootgames.attempt_left", LGConfigs.MINESWEEPER.attemptCount.get() - attemptCount), NotifyColor.GRAVE_NOTIFY);

                    switchStage(new StageWaiting());

                    board.resetBoard();

                    net.sendUpdatePacketToNearby(new SPMSResetFlags());
                    net().saveAndSync();
                }
            }
        }

        /**
         * Adds detonating tasks to scheduler.
         * Returns the longest detonating time, after which all bombs will explode
         */
        private int detonateBoard(int strength, Level.ExplosionInteraction explosionMode) {
            Holder<Integer> longestDetTime = new Holder<>(0);

            board.forEach(pos2i -> {
                if (board.getType(pos2i) == Type.BOMB) {

                    int detTime = RandHelper.RAND.nextInt(45);

                    if (longestDetTime.get() < detTime) {
                        longestDetTime.set(detTime);
                    }

                    taskScheduler.addTask(new TaskCreateExplosion(convertToBlockPos(pos2i), strength, explosionMode), detTime);
                }
            });

            return longestDetTime.get();
        }

        @Override
        public String getID() {
            return ID;
        }
    }
}

