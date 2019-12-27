package ru.timeconqueror.lootgames.minigame.minesweeper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.util.NBTUtils;
import ru.timeconqueror.lootgames.api.util.NetworkUtils;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.block.BlockDungeonBricks;
import ru.timeconqueror.lootgames.block.BlockDungeonLamp;
import ru.timeconqueror.lootgames.minigame.minesweeper.task.TaskMSCreateExplosion;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.world.gen.DungeonGenerator;

import java.util.concurrent.atomic.AtomicInteger;

import static ru.timeconqueror.lootgames.minigame.minesweeper.MSBoard.MSField;

//TODO Every fail bomb strength increases
//TODO add custom Stage Class to improve readability of code (name, ticks before skipping, actions)
//FIXME add bomb counter
public class GameMineSweeper extends LootGame {
    public static final int TICKS_DETONATION_TIME = 3 * 20;//TODO config
    private static final int ATTEMPTS_ALLOWED = 3;//TODO config

    @SideOnly(Side.CLIENT)
    public boolean cIsGenerated;

    private MSBoard board;//FIXME check working after changed board size config

    private Stage stage;
    private int ticks;
    private int attemptCount = 0;

    public GameMineSweeper(int boardSize, int bombCount) {
        board = new MSBoard(boardSize, bombCount);
        stage = Stage.WAITING;
    }

    public static Pos2i convertToGamePos(BlockPos masterPos, BlockPos subordinatePos) {
        BlockPos tpos = subordinatePos.subtract(masterPos);
        return new Pos2i(tpos.getX(), tpos.getZ());
    }

    @Override
    public void onTick() {
        super.onTick();
        if (isServerSide()) {
            if (stage == Stage.DETONATING) {
                if (ticks >= TICKS_DETONATION_TIME) {
                    onDetonateTimePassed();
                }

                ticks++;
            } else if (stage == Stage.EXPLODING) {
                ticks--;

                if (ticks <= 0) {
                    NetworkUtils.sendMessageToAllNearby(getCentralGamePos(),
                            NetworkUtils.color(new TextComponentTranslation("msg.lootgames.ms.new_attempt"), TextFormatting.AQUA),
                            getBoardSize() / 2 + 7);
                    updateStage(Stage.WAITING);

                    board.resetBoard();
                    saveDataAndSendToClient();
                }
            }
        } else {
            if (stage == Stage.DETONATING) {
                ticks++;
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
            revealAllNeighbours(clickedPos);
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
                    revealAllNeighbours(pos);
                } else if (type == MSField.BOMB) {
                    onBombTriggered();
                }

                saveData();

                if (board.checkWin()) {
                    onGameWon();
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
                onGameWon();
            }
        }
    }

    private void revealAllNeighbours(Pos2i mainPos) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }

                Pos2i pos = mainPos.add(x, y);
                if (board.hasFieldOn(pos)) {
                    if (board.isHidden(pos)) {
                        revealField(pos);
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
                NetworkUtils.color(new TextComponentTranslation("msg.lootgames.ms.bomb_touched"), TextFormatting.DARK_PURPLE),
                getBoardSize() / 2 + 7);

        saveDataAndSendToClient();

        attemptCount++;
    }

    private void onDetonateTimePassed() {
        if (attemptCount < ATTEMPTS_ALLOWED) {
            int longestDetTime = detonateBoard(4, false);//fixme balance strength
            updateStage(Stage.EXPLODING);
            ticks = longestDetTime + 2 * 20;//number - some pause after detonating
        } else {
            onGameLost();
        }
    }

    private void onGameWon() {
        NetworkUtils.sendMessageToAllNearby(getCentralGamePos(), new TextComponentString("You won"), getBoardSize() / 2 + 7);
        destroyStructure();
    }

    private void onGameLost() {
        destroyStructure();

        BlockPos expPos = getCentralGamePos();
        getWorld().createExplosion(null, expPos.getX(), expPos.getY() + 1.5, expPos.getZ(), 10, true);//fixme balance strength

        NetworkUtils.sendMessageToAllNearby(getCentralGamePos(),
                NetworkUtils.color(new TextComponentTranslation("msg.lootgames.lose"), TextFormatting.DARK_PURPLE),
                getBoardSize() / 2 + 7);
    }

    private void destroyStructure() {
        for (int x = 0; x < getBoardSize(); x++) {
            for (int z = 0; z < getBoardSize(); z++) {
                if (x == 0 || z == 0 || x == getBoardSize() - 1 || z == getBoardSize() - 1) {
                    getWorld().setBlockState(getMasterPos().add(x, 0, z), ModBlocks.DUNGEON_LAMP.getDefaultState().withProperty(BlockDungeonLamp.BROKEN, false));
                } else {
                    getWorld().setBlockState(getMasterPos().add(x, 0, z), ModBlocks.DUNGEON_BRICKS.getDefaultState().withProperty(BlockDungeonBricks.VARIANT, BlockDungeonBricks.EnumType.DUNGEON_FLOOR));
                }
            }
        }

        DungeonGenerator.resetUnbreakablePlayfield(getWorld(), getCentralGamePos());//TODO duplicate from GameOfLight, do sth with it
    }

    /**
     * Adds detonating tasks to scheduler.
     * Returns the longest detonating time, after which all bombs will explode
     */
    private int detonateBoard(int strength, boolean damageTerrain) {
        AtomicInteger longestDetTime = new AtomicInteger();

        board.forEach(pos2i -> {
            if (board.getType(pos2i) == MSField.BOMB) {
                System.out.println("BoMb: " + pos2i.getX() + " " + pos2i.getY());

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
            sendUpdatePacket("stageUpdate", c);
        }

        saveData();
    }

    @Override
    public void onUpdatePacket(String key, NBTTagCompound compoundIn) {
        switch (key) {
            case "stageUpdate":
                updateStage(Stage.values()[compoundIn.getInteger("stage")]);
                break;
            case "gen_board":
                readNBTFromClient(compoundIn);
                break;
            case "field_changed":
                Pos2i pos = new Pos2i(compoundIn.getInteger("x"), compoundIn.getInteger("y"));
                board.setField(pos, compoundIn.getInteger("type"), compoundIn.getBoolean("hidden"), compoundIn.getInteger("mark"));
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
        return masterTileEntity.getPos().add(getBoardSize() / 2 + 1, 0, getBoardSize() / 2 + 1);
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
        super.readNBTFromClient(compound);

        cIsGenerated = compound.getBoolean("is_generated");
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
    }

    @Override
    public void readCommonNBT(NBTTagCompound compound) {
        super.readCommonNBT(compound);

        board.setBombCount(compound.getInteger("bomb_count"));
        board.setSize(compound.getInteger("board_size"));
        stage = Stage.values()[compound.getInteger("stage")];
        ticks = compound.getInteger("ticks");
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

