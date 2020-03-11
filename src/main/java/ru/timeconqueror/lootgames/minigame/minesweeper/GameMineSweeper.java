package ru.timeconqueror.lootgames.minigame.minesweeper;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.task.TaskCreateExplosion;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.common.packet.game.*;
import ru.timeconqueror.lootgames.common.world.gen.DungeonGenerator;
import ru.timeconqueror.lootgames.minigame.minesweeper.MSBoard.Mark;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.timecore.api.util.RandHelper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.timeconqueror.lootgames.minigame.minesweeper.MSBoard.MSField;
import static ru.timeconqueror.timecore.api.util.NetworkUtils.*;

//TODO add default colors to warn, fail, win, etc
public class GameMineSweeper extends LootGame<GameMineSweeper> {
    public boolean cIsGenerated;

    private int currentLevel = 1;

    private MSBoard board;

    //    private StageLegacy stage;
    private int ticks;
    private int attemptCount = 0;

    private boolean playRevealNeighboursSound = true;

    //fixme remove
    public GameMineSweeper() {
    }

    public GameMineSweeper(int boardSize, int bombCount) {
        board = new MSBoard(boardSize, bombCount);
        switchStage(new StageWaiting());
    }

    public static Pos2i convertToGamePos(BlockPos masterPos, BlockPos subordinatePos) {
        BlockPos temp = subordinatePos.subtract(masterPos);
        return new Pos2i(temp.getX(), temp.getZ());
    }

    public static void generateGameStructure(World world, BlockPos centerPos, int level) {
//        int size = Objects.requireNonNull(LGConfigMinesweeper.getStage(level)).boardSize;
//        BlockPos startPos = centerPos.add(-size / 2, 0, -size / 2);
//
//        for (int x = 0; x < size; x++) {
//            for (int z = 0; z < size; z++) {
//                BlockPos pos = startPos.add(x, 0, z);
//                if (x == 0 && z == 0) {
//                    world.setBlockState(pos, LGBlocks.MS_MASTER.getDefaultState());
//                } else {
//                    world.setBlockState(pos, LGBlocks.SMART_SUBORDINATE.getDefaultState());
//                }
//            }
//        }
    }

    @Override
    protected BlockPos getRoomFloorPos() {
        return getMasterPos();
    }

    @Override
    protected BlockPos getCentralRoomPos() {
        return getCentralGamePos();
    }

    public void clickField(ServerPlayerEntity player, Pos2i clickedPos) {
        if (stage instanceof StageWaiting) {
            ((StageWaiting) stage).onFieldClicked(player, clickedPos);
        }
    }

    public boolean isBoardGenerated() {
        return board.isGenerated();
    }

    public int getBoardSize() {
        return board.size();
    }

    private void onBombTriggered() {
//        switchStage(new StageDetonating());

        board.forEach((x, y) -> {
            if (board.isBomb(x, y)) {
                board.reveal(x, y);
            }
        });

        sendMessageToAllNearby(getCentralGamePos(),
                format(new TranslationTextComponent("msg.lootgames.ms.bomb_touched"), TextFormatting.DARK_PURPLE),
                getBroadcastDistance());

        saveDataAndSendToClient();

        attemptCount++;
    }

    private void onLevelSuccessfullyFinished() {
        if (currentLevel < 4) {

            sendUpdatePacket(new SPMSSpawnLevelBeatParticles());

            sendMessageToAllNearby(getCentralGamePos(), format(new TranslationTextComponent("msg.lootgames.stage_complete"), TextFormatting.GREEN), DungeonGenerator.PUZZLEROOM_CENTER_TO_BORDER * 4);
            getWorld().playSound(null, getCentralGamePos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.75F, 1.0F);

            masterTileEntity.destroyGameBlocks();
            generateGameStructure(getWorld(), getCentralGamePos(), currentLevel + 1);

//            LGConfigMinesweeper.Stage stageConfig = LGConfigMinesweeper.getStage(currentLevel + 1);
//            BlockPos startPos = getCentralGamePos().add(-stageConfig.boardSize / 2, 0, -stageConfig.boardSize / 2);

            masterTileEntity.validate();
//            getWorld().setTileEntity(startPos, masterTileEntity);

//            board.resetBoard(stageConfig.boardSize, stageConfig.bombCount);
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

        List<ServerPlayerEntity> players = getPlayersNearby(getCentralGamePos(), getBroadcastDistance());

        for (ServerPlayerEntity player : players) {
            player.sendMessage(format(new TranslationTextComponent("msg.lootgames.win"), TextFormatting.GREEN));
        }

//        genLootChests(players);
    }

//    private void genLootChests(List<ServerPlayerEntity> players) {
//        if (currentLevel < 2) {
//            LootGames.LOGGER.error("GenLootChests method was called in an appropriate time!");
//            return;
//        }
//
//        BlockPos central = getCentralGamePos();
//        BlockState state = LGBlocks.DUNGEON_LAMP.getDefaultState();
//        getWorld().setBlockState(central.add(1, 0, 1), state);
//        getWorld().setBlockState(central.add(1, 0, -1), state);
//        getWorld().setBlockState(central.add(-1, 0, 1), state);
//        getWorld().setBlockState(central.add(-1, 0, -1), state);
//
//        spawnLootChest(DirectionTetra.NORTH, 1);
//
//        if (currentLevel > 2) {
//
//            spawnLootChest(DirectionTetra.EAST, 2);
//        }
//
//        if (currentLevel > 3) {
//            spawnLootChest(DirectionTetra.SOUTH, 3);
//        }
//
//        if (currentLevel > 4) {
//            for (ServerPlayerEntity entityPlayerMP : players) {
//                LGAdvancementManager.END_GAME.trigger((entityPlayerMP), "ms_level4");
//            }
//            spawnLootChest(DirectionTetra.WEST, 4);
//        }
//    }

//    private void spawnLootChest(DirectionTetra direction, int gameLevel) {
//        LGConfigGOL.Stage stage = LGConfigGOL.getStageByIndex(gameLevel);
//        GameUtils.SpawnChestInfo chestInfo = new GameUtils.SpawnChestInfo(GameOfLight.class, stage.getLootTableRL(getWorld().provider.getDimension()), stage.minItems, stage.maxItems);
//        GameUtils.spawnLootChest(getWorld(), getCentralGamePos(), direction, chestInfo);
//    }

    @Override
    protected void triggerGameLose() {
        super.triggerGameLose();

        BlockPos expPos = getCentralGamePos();
        getWorld().createExplosion(null, expPos.getX(), expPos.getY() + 1.5, expPos.getZ(), 9, Explosion.Mode.DESTROY);

        sendMessageToAllNearby(getCentralGamePos(),
                format(new TranslationTextComponent("msg.lootgames.lose"), TextFormatting.DARK_PURPLE),
                getBroadcastDistance());
    }

    /**
     * Adds detonating tasks to scheduler.
     * Returns the longest detonating time, after which all bombs will explode
     */
    private int detonateBoard(int strength, Explosion.Mode explosionMode) {
        AtomicInteger longestDetTime = new AtomicInteger();

        board.forEach(pos2i -> {
            if (board.getType(pos2i) == MSField.BOMB) {

                int detTime = RandHelper.RAND.nextInt(45);

                if (longestDetTime.get() < detTime) {
                    longestDetTime.set(detTime);
                }

                serverTaskPostponer.addTask(new TaskCreateExplosion(getMasterPos(), strength, explosionMode), detTime);
            }
        });

        return longestDetTime.get();
    }

    @Override
    protected void onStageUpdate(Stage oldStage, Stage newStage) {
        super.onStageUpdate(oldStage, newStage);
        ticks = 0;

        if (isServerSide()) {
            sendUpdatePacket(new SPMSPacketChangeStage(this));
            saveData();
        }
    }

    public BlockPos convertToBlockPos(int x, int y) {
        return masterTileEntity.getPos().add(x, 0, y);
    }

    public BlockPos convertToBlockPos(Pos2i pos) {
        return convertToBlockPos(pos.getX(), pos.getY());
    }

    /**
     * Returns the blockpos in the center of the board.
     */
    public BlockPos getCentralGamePos() {
        return masterTileEntity.getPos().add(getBoardSize() / 2, 0, getBoardSize() / 2);
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
    }

    @Override
    public void readCommonNBT(CompoundNBT compound) {
        super.readCommonNBT(compound);

        board.setBombCount(compound.getInt("bomb_count"));
        board.setSize(compound.getInt("board_size"));
        ticks = compound.getInt("ticks");
        currentLevel = compound.getInt("current_level");
    }

    @Override
    public Stage createStageFromNBT(CompoundNBT stageNBT) {
        switch (stageNBT.getString("id")) {
            case StageWaiting.ID:
                return new StageWaiting();
            case StageDetonating.ID:
                return new StageDetonating(stageNBT.getInt("detonation_time"));
            default:
                throw new IllegalArgumentException("Unknown state with id: " + stageNBT.getString("id") + "!");
        }
    }

    public enum StageLegacy {
        WAITING,
        DETONATING,
        EXPLODING
    }

    public static class Factory implements ILootGameFactory {
        @Override
        public void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos, BlockPos bottomPos, BlockPos topPos) {
            BlockPos floorCenterPos = puzzleMasterPos.add(0, -DungeonGenerator.PUZZLEROOM_MASTER_TE_OFFSET + 1, 0);
            world.setBlockState(floorCenterPos, LGBlocks.MS_ACTIVATOR.getDefaultState());
        }
    }

    public class StageWaiting extends Stage {
        private static final String ID = "waiting";

        public StageWaiting() {
            super(SERVER);
        }

        public void onFieldClicked(ServerPlayerEntity player, Pos2i clickedPos) {
            getWorld().playSound(null, convertToBlockPos(clickedPos), SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.MASTER, 0.6F, 0.8F);

            if (!board.isGenerated()) {
                generateBoard(player, clickedPos);
            } else {
                playRevealNeighboursSound = true;

                if (player.isSneaking()) {
                    if (board.isHidden(clickedPos)) {
                        swapFieldMark(clickedPos);
                    } else {
                        revealAllNeighbours(player, clickedPos, false);
                    }
                } else {
                    if (board.getMark(clickedPos) == Mark.NO_MARK) {
                        revealField(player, clickedPos);
                    }
                }
            }
        }

        public void generateBoard(ServerPlayerEntity player, Pos2i clickedPos) {
            board.generate(clickedPos);
            sendUpdatePacket(new SPMSGenBoard(GameMineSweeper.this));

            if (board.getType(clickedPos) == MSField.EMPTY) {
                revealAllNeighbours(player, clickedPos, true);
            }

            saveData();
        }

        public void revealField(ServerPlayerEntity player, Pos2i pos) {
            if (board.isHidden(pos)) {
                board.reveal(pos);

                int type = board.getType(pos);

                sendUpdatePacket(new SPMSFieldChanged(pos, type, Mark.NO_MARK, false));

                if (type == MSField.EMPTY) {
                    if (playRevealNeighboursSound) {
                        getWorld().playSound(null, convertToBlockPos(pos), LGSounds.MS_ON_EMPTY_REVEAL_NEIGHBOURS, SoundCategory.MASTER, 0.6F, 1.0F);
                        playRevealNeighboursSound = false;
                    }

                    revealAllNeighbours(player, pos, true);
                } else if (type == MSField.BOMB) {
                    getWorld().playSound(null, convertToBlockPos(pos), LGSounds.MS_BOMB_ACTIVATED, SoundCategory.MASTER, 0.6F, 1.0F);
                    onBombTriggered();
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
                    player.sendMessage(format(new TranslationTextComponent("msg.lootgames.ms.reveal_on_hidden"), TextFormatting.YELLOW));
                    return;
                }

                int bombsAround = board.getType(mainPos);
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
                    player.sendMessage(format(new TranslationTextComponent("msg.lootgames.ms.reveal_invalid_mark_count"), TextFormatting.YELLOW));
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

                sendUpdatePacket(new SPMSFieldChanged(pos, MSField.EMPTY, board.getMark(pos), true));
                saveData();
            }

            if (board.checkWin()) {
                onLevelSuccessfullyFinished();
            }

        }

        @Override
        public String getID() {
            return ID;
        }
    }

    public class StageDetonating extends Stage {
        private static final String ID = "detonating";
        private final int detonationTimeInTicks;

//        public StageDetonating() {
//            this(LGConfigMinesweeper.detonationTimeInTicks);
//        }

        public StageDetonating(int detonationTimeInTicks) {
            super(BOTH);
            this.detonationTimeInTicks = detonationTimeInTicks;
        }

        @Override
        public void onTick() {
            if (isServerSide()) {
                if (ticks >= detonationTimeInTicks) {
                    onDetonateTimePassed();
                }
            }

            ticks++;
        }

        private void onDetonateTimePassed() {
//            if (attemptCount < LGConfigMinesweeper.attemptCount) {
//                int longestDetTime = detonateBoard(currentLevel + 3, Explosion.Mode.BREAK);
//                switchStage(new StageExploding());
//                ticks = longestDetTime + 2 * 20;//number - some pause after detonating
//            } else {
//                if (currentLevel > 1) {
//                    triggerGameWin();
//                } else {
//                    triggerGameLose();
//                }
//            }
        }

        @Override
        public String getID() {
            return ID;
        }

        @Override
        public CompoundNBT serialize() {
            CompoundNBT nbt = super.serialize();
            nbt.putInt("detonation_time", detonationTimeInTicks);
            return nbt;
        }
    }

    public class StageExploding extends Stage {
        private static final String ID = "exploding";

        public StageExploding() {
            super(SERVER);
        }

        @Override
        public void onTick() {
            ticks--;

            if (ticks <= 0) {
                sendMessageToAllNearby(getCentralGamePos(),
                        format(new TranslationTextComponent("msg.lootgames.ms.new_attempt"), TextFormatting.AQUA),
                        getBroadcastDistance());
                //sendMessageToAllNearby(getCentralGamePos(), format(new TranslationTextComponent("msg.lootgames.attempt_count", LGConfigMinesweeper.attemptCount - attemptCount), TextFormatting.RED), getBroadcastDistance());

                switchStage(new StageWaiting());

                board.resetBoard();

                sendUpdatePacket(new SPMSResetFlags());

                saveDataAndSendToClient();
            }
        }

        @Override
        public String getID() {
            return ID;
        }
    }
}

