package ru.timeconqueror.lootgames.minigame.minesweeper;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.NotifyColor;
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
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.lootgames.utils.MouseClickType;
import ru.timeconqueror.timecore.api.util.RandHelper;
import ru.timeconqueror.timecore.api.util.Wrapper;

import java.util.List;

import static ru.timeconqueror.timecore.api.util.NetworkUtils.getPlayersNearby;

//TODO lang keys for advancements
//TODo add check if tileentity is nearby or blocksmartsubordinate or masterplacer
//TODO change generation random - depending on world is bad.
//TODO add leveling info in chat
//todo add game rules in chat
//TODO remove break particle before left click interact
//TODO if all non-bomb fields are revealed, finish the game
public class GameMineSweeper extends BoardLootGame<GameMineSweeper> {
    public static final String ADV_TYPE_BEAT_LEVEL4 = "ms_level4";

    public boolean cIsGenerated;

    private int currentLevel = 1;

    private final MSBoard board;

    private int ticks;
    private int attemptCount = 0;

    private boolean playRevealNeighboursSound = true;

    /**
     * Server-side only
     */
    private Snapshot configSnapshot = null;

    public GameMineSweeper() {
        board = new MSBoard(0, 0);
    }

    public static Pos2i convertToGamePos(BlockPos masterPos, BlockPos subordinatePos) {
        BlockPos temp = subordinatePos.subtract(masterPos);
        return new Pos2i(temp.getX(), temp.getZ());
    }

    // server only
    public void setConfigSnapshot(Snapshot configSnapshot) {
        this.configSnapshot = configSnapshot;
    }

    @Override
    public void onPlace() {
        stage = new StageWaiting();

        if (isServerSide()) {
            configSnapshot = LGConfigs.MINESWEEPER.snapshot();
            board.setSize(configSnapshot.getStage1().getBoardSize());
            board.setBombCount(configSnapshot.getStage1().getBombCount());
        }

        super.onPlace();
    }

    public void onClick(ServerPlayerEntity player, Pos2i clickedPos, MouseClickType type) {
        if (stage instanceof StageWaiting) {
            ((StageWaiting) stage).onClick(player, clickedPos, type);
        }
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
            sendUpdatePacket(new SPMSSpawnLevelBeatParticles());

            sendToNearby(new TranslationTextComponent("msg.lootgames.stage_complete"), NotifyColor.SUCCESS);
            getWorld().playSound(null, getGameCenter(), SoundEvents.PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.75F, 1.0F);

            Snapshot.StageSnapshot stageSnapshot = configSnapshot.getStageByIndex(currentLevel + 1);

            board.resetBoard(stageSnapshot.getBoardSize(), stageSnapshot.getBombCount());
            currentLevel++;

            saveDataAndSendToClient();
        } else {
            currentLevel++;
            triggerGameWin();
        }
    }


    @Override
    protected void triggerGameWin() {
        super.triggerGameWin();

        List<ServerPlayerEntity> players = getPlayersNearby(getGameCenter(), getBroadcastDistance());

        genLootChests(players);
    }

    private void genLootChests(List<ServerPlayerEntity> players) {
        if (currentLevel < 2) {
            throw new RuntimeException("GenLootChests method was called in an appropriate time!");
        }

        BlockPos central = getGameCenter();

        if (currentLevel > 4) { // end of the game
            players.forEach(player -> LGAdvancementTriggers.END_GAME.trigger(player, ADV_TYPE_BEAT_LEVEL4));
        }

        RewardUtils.spawnFourStagedReward(((ServerWorld) getWorld()), this, central, currentLevel - 1, LGConfigs.REWARDS.minesweeper);
    }

    @Override
    protected void triggerGameLose() {
        super.triggerGameLose();

        BlockPos expPos = getGameCenter();
        getWorld().explode(null, expPos.getX(), expPos.getY() + 1.5, expPos.getZ(), 9, Explosion.Mode.DESTROY);
    }

    @Override
    protected void onStageUpdate(Stage<GameMineSweeper> oldStage, Stage<GameMineSweeper> newStage) {
        super.onStageUpdate(oldStage, newStage);
        ticks = 0;
    }

    public MSBoard getBoard() {
        return board;
    }

    public int getTicks() {
        return ticks;
    }

    @Override
    public void writeNBTForSaving(CompoundNBT nbt) {
        super.writeNBTForSaving(nbt);

        if (isBoardGenerated()) {
            CompoundNBT boardTag = board.writeNBTForSaving();
            nbt.put("board", boardTag);
        }

        nbt.putInt("attempt_count", attemptCount);
    }

    @Override
    public void readNBTFromSave(CompoundNBT compound) {
        super.readNBTFromSave(compound);

        if (compound.contains("board")) {
            CompoundNBT boardTag = compound.getCompound("board");
            board.readNBTFromSave(boardTag);
        }

        attemptCount = compound.getInt("attempt_count");
    }

    @Override
    public void writeNBTForClient(CompoundNBT nbt) {
        super.writeNBTForClient(nbt);

        nbt.putBoolean("is_generated", isBoardGenerated());
        if (isBoardGenerated()) {
            CompoundNBT boardTag = board.writeNBTForClient();
            nbt.put("board", boardTag);
        }
    }

    @Override
    public void readNBTAtClient(CompoundNBT compound) {
        cIsGenerated = compound.getBoolean("is_generated");

        super.readNBTAtClient(compound);

        if (compound.contains("board")) {
            CompoundNBT boardTag = compound.getCompound("board");
            board.readNBTFromClient(boardTag);
        }
    }

    @Override
    public void writeCommonNBT(CompoundNBT compound) {
        super.writeCommonNBT(compound);
        compound.putInt("bomb_count", board.getBombCount());
        compound.putInt("board_size", board.size());

        compound.putInt("ticks", ticks);

        compound.putInt("current_level", currentLevel);

        compound.put("config_snapshot", Snapshot.serialize(configSnapshot));
    }

    @Override
    public void readCommonNBT(CompoundNBT compound) {
        super.readCommonNBT(compound);

        board.setBombCount(compound.getInt("bomb_count"));
        board.setSize(compound.getInt("board_size"));
        ticks = compound.getInt("ticks");
        currentLevel = compound.getInt("current_level");

        configSnapshot = Snapshot.deserialize(compound.get("config_snapshot"));
    }

    @Override
    public Stage<GameMineSweeper> createStageFromNBT(String id, CompoundNBT stageNBT) {
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

    public static class Factory implements ILootGameFactory {
        @Override
        public void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos) {
            BlockPos floorCenterPos = puzzleMasterPos.offset(0, -3/*instead of GameDungeonStructure.MASTER_BLOCK_OFFSET*/ + 1, 0);
            world.setBlockAndUpdate(floorCenterPos, LGBlocks.MS_ACTIVATOR.defaultBlockState());
        }
    }

    public class StageWaiting extends Stage<GameMineSweeper> {
        private static final String ID = "waiting";

        public StageWaiting() {
        }

        public void onClick(ServerPlayerEntity player, Pos2i clickedPos, MouseClickType type) {
            getWorld().playSound(null, convertToBlockPos(clickedPos), SoundEvents.NOTE_BLOCK_HAT, SoundCategory.MASTER, 0.6F, 0.8F);

            if (!board.isGenerated()) {
                generateBoard(player, clickedPos);
            } else {
                playRevealNeighboursSound = true;

                if (type == MouseClickType.LEFT) {
                    if (board.getMark(clickedPos) == Mark.NO_MARK) {
                        revealField(player, clickedPos);
                    }
                } else {
                    if (player.isShiftKeyDown()) {
                        revealAllNeighbours(player, clickedPos, false);
                    } else if (board.isHidden(clickedPos)) {
                        swapFieldMark(clickedPos);
                    }
                }
            }
        }

        public void generateBoard(ServerPlayerEntity player, Pos2i clickedPos) {
            board.generate(clickedPos);
            sendUpdatePacket(new SPMSGenBoard(GameMineSweeper.this));
            revealField(player, clickedPos);

            saveData();
        }

        public void revealField(ServerPlayerEntity player, Pos2i pos) {
            if (board.isHidden(pos)) {
                board.reveal(pos);

                Type type = board.getType(pos);

                sendUpdatePacket(new SPMSFieldChanged(pos, board.getField(pos)));

                if (type == Type.EMPTY) {
                    if (playRevealNeighboursSound) {
                        getWorld().playSound(null, convertToBlockPos(pos), LGSounds.MS_ON_EMPTY_REVEAL_NEIGHBOURS, SoundCategory.MASTER, 0.6F, 1.0F);
                        playRevealNeighboursSound = false;
                    }

                    revealAllNeighbours(player, pos, true);
                } else if (type == Type.BOMB) {
                    triggerBombs(pos);
                }

                saveData();

                if (board.checkWin()) {
                    onLevelSuccessfullyFinished();
                }
            }
        }

        private void revealAllNeighbours(ServerPlayerEntity player, Pos2i mainPos, boolean revealMarked) {
            if (!revealMarked) {
                if (board.isHidden(mainPos)) {
                    sendTo(player, new TranslationTextComponent("msg.lootgames.ms.reveal_on_hidden"), NotifyColor.WARN);
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
                    sendTo(player, new TranslationTextComponent("msg.lootgames.ms.reveal_invalid_mark_count"), NotifyColor.WARN);
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

                sendUpdatePacket(new SPMSFieldChanged(pos, board.getField(pos)));
                saveData();
            }

            if (board.checkWin()) {
                onLevelSuccessfullyFinished();
            }

        }

        private void triggerBombs(Pos2i pos) {
            getWorld().playSound(null, convertToBlockPos(pos), LGSounds.MS_BOMB_ACTIVATED, SoundCategory.MASTER, 0.6F, 1.0F);

            switchStage(new StageDetonating());

            board.forEach((x, y) -> {
                if (board.isBomb(x, y)) {
                    board.reveal(x, y);
                }
            });

            sendToNearby(new TranslationTextComponent("msg.lootgames.ms.bomb_touched"), NotifyColor.FAIL);

            saveDataAndSendToClient();

            attemptCount++;
        }

        @Override
        public String getID() {
            return ID;
        }
    }

    public class StageDetonating extends Stage<GameMineSweeper> {
        private static final String ID = "detonating";
        private final int detonationTicks;

        public StageDetonating() {
            this(LGConfigs.MINESWEEPER.DETONATION_TIME.get());
        }

        public StageDetonating(int detonationTicks) {
            this.detonationTicks = detonationTicks;
        }

        @Override
        public void onTick(LootGame<GameMineSweeper> game) {
            if (isServerSide()) {
                if (ticks >= detonationTicks) {
                    if (attemptCount < LGConfigs.MINESWEEPER.ATTEMPT_COUNT.get()) {
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
        public CompoundNBT serialize() {
            CompoundNBT nbt = super.serialize();
            nbt.putInt("detonation_time", detonationTicks);
            return nbt;
        }
    }

    public class StageExploding extends Stage<GameMineSweeper> {
        private static final String ID = "exploding";

        public StageExploding() {
        }

        @Override
        protected void onStart(LootGame<GameMineSweeper> game) {
            if (game.isServerSide()) {
                int longestDetTime = detonateBoard(currentLevel + 3, Explosion.Mode.BREAK);
                ticks = longestDetTime + 2 * 20; //number represents some pause after detonating
            }
        }

        @Override
        public void onTick(LootGame<GameMineSweeper> game) {
            if (game.isServerSide()) {
                ticks--;

                if (ticks <= 0) {
                    sendToNearby(new TranslationTextComponent("msg.lootgames.ms.new_attempt"), NotifyColor.NOTIFY);
                    sendToNearby(new TranslationTextComponent("msg.lootgames.attempt_left", LGConfigs.MINESWEEPER.ATTEMPT_COUNT.get() - attemptCount), NotifyColor.GRAVE_NOTIFY);

                    switchStage(new StageWaiting());

                    board.resetBoard();

                    sendUpdatePacket(new SPMSResetFlags());

                    saveDataAndSendToClient();
                }
            }
        }

        /**
         * Adds detonating tasks to scheduler.
         * Returns the longest detonating time, after which all bombs will explode
         */
        private int detonateBoard(int strength, Explosion.Mode explosionMode) {
            Wrapper<Integer> longestDetTime = new Wrapper<>(0);

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

