package ru.timeconqueror.lootgames.minigame.minesweeper;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.achievement.AdvancementManager;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.util.GameUtils;
import ru.timeconqueror.lootgames.api.util.NBTUtils;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.config.LGConfigMinesweeper;
import ru.timeconqueror.lootgames.config.LootGamesConfig;
import ru.timeconqueror.lootgames.minigame.gameoflight.GameOfLight;
import ru.timeconqueror.lootgames.minigame.minesweeper.block.BlockMSActivator;
import ru.timeconqueror.lootgames.minigame.minesweeper.task.TaskMSCreateExplosion;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.world.gen.DungeonGenerator;
import ru.timeconqueror.timecore.api.auxiliary.DirectionTetra;
import ru.timeconqueror.timecore.api.auxiliary.MessageUtils;
import ru.timeconqueror.timecore.api.auxiliary.NetworkUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.timeconqueror.lootgames.minigame.minesweeper.MSBoard.MSField;

//TODO Every fail bomb strength increases
//TODO add custom Stage Class to improve readability of code (name, ticks before skipping, actions)
//TODO add win/lost achievements
//TODO add default colors to warn, fail, win, etc
public class GameMineSweeper extends LootGame {

    @SideOnly(Side.CLIENT)
    public boolean cIsGenerated;
    @SideOnly(Side.CLIENT)
    public int detonationTimeInTicks;

    private int currentLevel = 1;

    private MSBoard board;

    private Stage stage;
    private int ticks;
    private int attemptCount = 0;

    public GameMineSweeper(int boardSize, int bombCount) {
        board = new MSBoard(boardSize, bombCount);
        stage = Stage.WAITING;

        setOnWin(this::onGameWon);
        setOnLose(this::onGameLost);
    }

    public static Pos2i convertToGamePos(BlockPos masterPos, BlockPos subordinatePos) {
        BlockPos temp = subordinatePos.subtract(masterPos);
        return new Pos2i(temp.getX(), temp.getZ());
    }

    @Override
    public void onTick() {
        super.onTick();
        if (isServerSide()) {
            if (stage == Stage.DETONATING) {
                if (ticks >= LGConfigMinesweeper.detonationTimeInTicks) {
                    onDetonateTimePassed();
                }

                ticks++;
            } else if (stage == Stage.EXPLODING) {
                ticks--;

                if (ticks <= 0) {
                    NetworkUtils.sendMessageToAllNearby(getCentralGamePos(),
                            MessageUtils.color(new TextComponentTranslation("msg.lootgames.ms.new_attempt"), TextFormatting.AQUA),
                            getDefaultBroadcastDistance());
                    updateStage(Stage.WAITING);

                    board.resetBoard();

                    sendUpdatePacket("reset_flag_counter", new NBTTagCompound());

                    saveDataAndSendToClient();
                }
            }
        } else {
            if (stage == Stage.DETONATING) {
                ticks++;
            }
        }
    }

    @Override
    protected BlockPos getRoomFloorPos() {
        return getMasterPos();
    }

    public void onFieldClicked(Pos2i clickedPos, boolean sneaking) {
        if (!board.isGenerated()) {
            generateBoard(clickedPos);
        } else {
            if (sneaking) {
                if (!board.isHidden(clickedPos)) {
                    revealAllNeighbours(clickedPos, false);
                } else {
                    swapFieldMark(clickedPos);
                }
            } else {
                if (board.getMark(clickedPos) == MSField.NO_MARK) {
                    revealField(clickedPos);
                }
            }
        }
    }

    public boolean isBoardGenerated() {
        return board.isGenerated();
    }

    public int getBoardSize() {
        return board.size();
    }

    public void generateBoard(Pos2i clickedPos) {
        board.generate(clickedPos);
        sendUpdatePacket("gen_board", writeNBTForClient());

        if (board.getType(clickedPos) == MSField.EMPTY) {
            revealAllNeighbours(clickedPos, true);
        }

        saveData();
    }

    public void revealField(Pos2i pos) {
        if (stage == Stage.WAITING) {
            if (board.isHidden(pos)) {
                board.reveal(pos);

                int type = board.getType(pos);

                NBTTagCompound c = new NBTTagCompound();
                c.setInteger("x", pos.getX());
                c.setInteger("y", pos.getY());
                c.setInteger("type", type);
                c.setBoolean("hidden", false);
                c.setInteger("mark", MSField.NO_MARK);
                sendUpdatePacket("field_changed", c);

                if (type == MSField.EMPTY) {
                    revealAllNeighbours(pos, true);
                } else if (type == MSField.BOMB) {
                    onBombTriggered();
                }

                saveData();

                if (board.checkWin()) {
                    onLevelSuccessfullyFinished();
                }
            }
        }
    }

    public void swapFieldMark(Pos2i pos) {
        if (stage == Stage.WAITING) {
            if (board.isHidden(pos)) {
                board.swapMark(pos);

                NBTTagCompound c = new NBTTagCompound();
                c.setInteger("x", pos.getX());
                c.setInteger("y", pos.getY());
                c.setInteger("type", MSField.EMPTY);
                c.setBoolean("hidden", true);
                c.setInteger("mark", board.getMark(pos));
                sendUpdatePacket("field_changed", c);

                saveData();
            }

            if (board.checkWin()) {
                onLevelSuccessfullyFinished();
            }
        }
    }

    private void revealAllNeighbours(Pos2i mainPos, boolean revealMarked) {
        if (!revealMarked) {
            if (board.isHidden(mainPos)) {
                NetworkUtils.sendMessageToAllNearby(getCentralGamePos(), MessageUtils.color(new TextComponentTranslation("msg.lootgames.ms.reveal_on_hidden"), TextFormatting.YELLOW), getDefaultBroadcastDistance());
                return;
            }

            int bombsAround = board.getType(mainPos);
            if (bombsAround < 1) throw new IllegalStateException();

            int marked = 0;
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    Pos2i pos = mainPos.add(x, y);
                    if (board.hasFieldOn(pos) && board.getMark(pos) == MSField.FLAG) {
                        marked++;
                    }
                }
            }

            if (marked != bombsAround) {
                NetworkUtils.sendMessageToAllNearby(getCentralGamePos(), MessageUtils.color(new TextComponentTranslation("msg.lootgames.ms.reveal_invalid_mark_count"), TextFormatting.YELLOW), getDefaultBroadcastDistance());
                return;
            }
        }

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }

                Pos2i pos = mainPos.add(x, y);
                if (board.hasFieldOn(pos)) {
                    if (board.isHidden(pos)) {
                        if (revealMarked || board.getMark(pos) != MSField.FLAG) {
                            revealField(pos);
                        }
                    }
                }
            }
        }
    }

    private void onBombTriggered() {
        updateStage(Stage.DETONATING);

        board.forEach((x, y) -> {
            if (board.isBomb(x, y)) {
                board.reveal(x, y);
            }
        });

        NetworkUtils.sendMessageToAllNearby(getCentralGamePos(),
                MessageUtils.color(new TextComponentTranslation("msg.lootgames.ms.bomb_touched"), TextFormatting.DARK_PURPLE),
                getDefaultBroadcastDistance());

        saveDataAndSendToClient();

        attemptCount++;
    }

    private void onDetonateTimePassed() {
        if (attemptCount < LGConfigMinesweeper.attemptCount) {
            int longestDetTime = detonateBoard(4, false);//fixme balance strength
            updateStage(Stage.EXPLODING);
            ticks = longestDetTime + 2 * 20;//number - some pause after detonating
        } else {
            if (currentLevel > 1) {
                winGame();
            } else {
                loseGame();
            }
        }
    }

    private void onLevelSuccessfullyFinished() {
        if (currentLevel < 4) {

            sendUpdatePacket("spawn_particles_level_beat", null);

            NetworkUtils.sendMessageToAllNearby(getCentralGamePos(), MessageUtils.color(new TextComponentTranslation("msg.lootgames.stage_complete"), TextFormatting.GREEN), DungeonGenerator.PUZZLEROOM_CENTER_TO_BORDER * 4);
            getWorld().playSound(null, getCentralGamePos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.75F, 1.0F);

            masterTileEntity.destroyGameBlocks();
            BlockMSActivator.generateGameStructure(getWorld(), getCentralGamePos(), currentLevel + 1);

            LGConfigMinesweeper.Stage stageConfig = LGConfigMinesweeper.getStage(currentLevel + 1);
            BlockPos startPos = getCentralGamePos().add(-stageConfig.boardSize / 2, 0, -stageConfig.boardSize / 2);

            masterTileEntity.validate();
            getWorld().setTileEntity(startPos, masterTileEntity);

            board.resetBoard(stageConfig.boardSize, stageConfig.bombCount);
            currentLevel++;

            saveDataAndSendToClient();
        } else {
            currentLevel++;
            winGame();
        }
    }

    private void onGameWon() {
        List<EntityPlayerMP> players = NetworkUtils.getPlayersNearby(getCentralGamePos(), getDefaultBroadcastDistance());

        for (EntityPlayerMP player : players) {
            player.sendMessage(MessageUtils.color(new TextComponentTranslation("msg.lootgames.win"), TextFormatting.GREEN));
        }

        genLootChests(players);
    }

    private void genLootChests(List<EntityPlayerMP> players) {//fixme change advancements
        if (currentLevel < 2) {
            LootGames.logHelper.error("GenLootChests method was called in an appropriate time!");
            return;
        }

        spawnLootChest(DirectionTetra.NORTH, 1);

        if (currentLevel > 2) {

            spawnLootChest(DirectionTetra.EAST, 2);
        }

        if (currentLevel > 3) {
            for (EntityPlayerMP entityPlayerMP : players) {
                AdvancementManager.WIN_GAME.trigger((entityPlayerMP), "ms_level3");
            }

            spawnLootChest(DirectionTetra.SOUTH, 3);
        }

        if (currentLevel > 4) {
            for (EntityPlayerMP entityPlayerMP : players) {
                AdvancementManager.WIN_GAME.trigger((entityPlayerMP), "ms_level4");
            }
            spawnLootChest(DirectionTetra.WEST, 4);
        }
    }

    private void spawnLootChest(DirectionTetra direction, int gameLevel) {
        LootGamesConfig.GOL.Stage stage = LootGamesConfig.gameOfLight.getStageByIndex(gameLevel);
        GameUtils.SpawnChestInfo chestInfo = new GameUtils.SpawnChestInfo(GameOfLight.class, stage.getLootTableRL(getWorld().provider.getDimension()), stage.minItems, stage.maxItems);
        GameUtils.spawnLootChest(getWorld(), getCentralGamePos(), direction, chestInfo);
    }

    private void onGameLost() {
        BlockPos expPos = getCentralGamePos();
        getWorld().createExplosion(null, expPos.getX(), expPos.getY() + 1.5, expPos.getZ(), 10, true);//fixme balance strength

        NetworkUtils.sendMessageToAllNearby(getCentralGamePos(),
                MessageUtils.color(new TextComponentTranslation("msg.lootgames.lose"), TextFormatting.DARK_PURPLE),
                getDefaultBroadcastDistance());
    }

    /**
     * Adds detonating tasks to scheduler.
     * Returns the longest detonating time, after which all bombs will explode
     */
    private int detonateBoard(int strength, boolean damageTerrain) {
        AtomicInteger longestDetTime = new AtomicInteger();

        board.forEach(pos2i -> {
            if (board.getType(pos2i) == MSField.BOMB) {

                int detTime = LootGames.RAND.nextInt(3 * 20);

                if (longestDetTime.get() < detTime) {
                    longestDetTime.set(detTime);
                }

                serverTaskPostponer.addTask(new TaskMSCreateExplosion(getMasterPos(), pos2i, strength, damageTerrain), detTime);
            }
        });

        return longestDetTime.get();
    }

    private void updateStage(Stage stageTo) {
        stage = stageTo;
        ticks = 0;

        if (isServerSide()) {
            NBTTagCompound c = new NBTTagCompound();
            c.setInteger("stage", stage.ordinal());

            if (stageTo == Stage.DETONATING) {
                c.setInteger("detonation_time", LGConfigMinesweeper.detonationTimeInTicks);
            }

            sendUpdatePacket("stageUpdate", c);
            saveData();
        }
    }

    @Override
    public void onUpdatePacket(String key, NBTTagCompound compoundIn) {
        switch (key) {
            case "stageUpdate":
                Stage stage = Stage.values()[compoundIn.getInteger("stage")];
                updateStage(stage);
                if (stage == Stage.DETONATING) {
                    detonationTimeInTicks = compoundIn.getInteger("detonation_time");
                }

                break;
            case "gen_board":
                readNBTFromClient(compoundIn);
                break;
            case "field_changed":
                Pos2i pos = new Pos2i(compoundIn.getInteger("x"), compoundIn.getInteger("y"));
                board.setField(pos, compoundIn.getInteger("type"), compoundIn.getBoolean("hidden"), compoundIn.getInteger("mark"));
                break;
            case "spawn_particles_level_beat":
                for (int x = 0; x < getBoardSize(); x++) {
                    for (int z = 0; z < getBoardSize(); z++) {
                        getWorld().spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, getMasterPos().getX() + x,
                                getCentralGamePos().getY() + 1,
                                getMasterPos().getZ() + z, 0.0, 0.2, 0.0);
                    }
                }
                break;
            case "reset_flag_counter":
                board.cFlaggedFields = 0;
                break;
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

    public Stage getStage() {
        return stage;
    }

    @Override
    public NBTTagCompound writeNBTForSaving() {
        NBTTagCompound gameCompound = super.writeNBTForSaving();

        if (isBoardGenerated()) {
            NBTTagCompound boardTag = board.writeNBTForSaving();
            gameCompound.setTag("board", boardTag);
        }

        gameCompound.setInteger("attempt_count", attemptCount);

        return gameCompound;
    }

    @Override
    public void readNBTFromSave(NBTTagCompound compound) {
        super.readNBTFromSave(compound);

        if (compound.hasKey("board")) {
            NBTTagCompound boardTag = compound.getCompoundTag("board");
            MSField[][] boardArr = NBTUtils.readTwoDimArrFromNBT(boardTag, MSField.class, () -> new MSField(MSField.EMPTY, true, MSField.NO_MARK));
            board.setBoard(boardArr);
        }

        attemptCount = compound.getInteger("attempt_count");
    }

    @Override
    public NBTTagCompound writeNBTForClient() {
        NBTTagCompound compound = super.writeNBTForClient();

        compound.setBoolean("is_generated", isBoardGenerated());
        if (isBoardGenerated()) {
            NBTTagCompound boardTag = board.writeNBTForClient();
            compound.setTag("board", boardTag);
        }
        return compound;
    }

    @Override
    public void readNBTFromClient(NBTTagCompound compound) {
        cIsGenerated = compound.getBoolean("is_generated");

        super.readNBTFromClient(compound);

        if (compound.hasKey("board")) {
            NBTTagCompound boardTag = compound.getCompoundTag("board");
            MSField[][] boardArr = NBTUtils.readTwoDimArrFromNBT(boardTag, MSField.class, compoundIn ->
                    new MSField(compoundIn.hasKey("type") ? compoundIn.getInteger("type") : MSField.EMPTY, compoundIn.getBoolean("hidden"), compoundIn.getInteger("mark")));
            board.setBoard(boardArr);
        }
    }

    @Override
    public void writeCommonNBT(NBTTagCompound compound) {
        super.writeCommonNBT(compound);
        compound.setInteger("bomb_count", board.getBombCount());
        compound.setInteger("board_size", board.size());
        compound.setInteger("stage", stage.ordinal());
        compound.setInteger("ticks", ticks);

        compound.setInteger("current_level", currentLevel);
    }

    @Override
    public void readCommonNBT(NBTTagCompound compound) {
        super.readCommonNBT(compound);

        board.setBombCount(compound.getInteger("bomb_count"));
        board.setSize(compound.getInteger("board_size"));
        stage = Stage.values()[compound.getInteger("stage")];
        ticks = compound.getInteger("ticks");
        currentLevel = compound.getInteger("current_level");
    }

    public enum Stage {
        WAITING,
        DETONATING,
        EXPLODING
    }

    public static class Factory implements ILootGameFactory {
        @Override
        public void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos, BlockPos bottomPos, BlockPos topPos) {
            BlockPos floorCenterPos = puzzleMasterPos.add(0, -DungeonGenerator.PUZZLEROOM_MASTER_TE_OFFSET + 1, 0);
            world.setBlockState(floorCenterPos, ModBlocks.MS_ACTIVATOR.getDefaultState());
        }
    }
}

