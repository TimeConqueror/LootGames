package ru.timeconqueror.lootgames.minigame.minesweeper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.task.TaskCreateExplosion;
import ru.timeconqueror.lootgames.api.util.NBTUtils;
import ru.timeconqueror.lootgames.api.util.NetworkUtils;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.world.gen.DungeonGenerator;

import static ru.timeconqueror.lootgames.minigame.minesweeper.MSBoard.MSField;

public class GameMineSweeper extends LootGame {
    public static final int TICKS_DETONATION_TIME = 3 * 20;//TODO config
    private static final int ATTEMPTS_ALLOWED = 3;//TODO config

    @SideOnly(Side.CLIENT)
    public boolean cIsGenerated;

    private MSBoard board;

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

        if (board.getFieldTypeByPos(clickedPos) == MSField.EMPTY) {
            revealAllNeighbours(clickedPos);
        }

        saveData();
    }

    public void revealField(Pos2i pos) {
        if (stage == Stage.WAITING) {
            MSField field = board.getFieldByPos(pos);
            if (field.isHidden()) {
                field.resetMark();
                field.reveal();

                NBTTagCompound c = new NBTTagCompound();
                c.setInteger("x", pos.getX());
                c.setInteger("y", pos.getY());
                c.setInteger("type", field.getType());
                c.setBoolean("hidden", false);
                c.setInteger("mark", MSField.NO_MARK);
                sendUpdatePacket("field_changed", c);

                if (field.getType() == MSField.EMPTY) {
                    revealAllNeighbours(pos);
                } else if (field.getType() == MSField.BOMB) {
                    onBombTriggered(pos);
                }

                saveData();
            }
        }
    }

    public void swapFieldMark(Pos2i pos) {
        if (stage == Stage.WAITING) {
            MSField field = board.getFieldByPos(pos);
            if (field.isHidden()) {
                field.swapMark();

                NBTTagCompound c = new NBTTagCompound();
                c.setInteger("x", pos.getX());
                c.setInteger("y", pos.getY());
                c.setInteger("type", MSField.EMPTY);
                c.setBoolean("hidden", true);
                c.setInteger("mark", field.getMark());
                sendUpdatePacket("field_changed", c);

                saveData();
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
                    MSField field = board.getFieldByPos(pos);
                    if (field.isHidden()) {
                        revealField(pos);
                    }
                }
            }
        }
    }

    private void onBombTriggered(Pos2i pos) {
        updateStage(Stage.DETONATING);

        for (MSField[] msFields : board.asArray()) {
            for (MSField msField : msFields) {
                if (msField.getType() == MSField.BOMB) {
                    msField.reveal();
                }
            }
        }

        NetworkUtils.sendColoredMessageToAllNearby(getCentralGamePos(),
                new TextComponentTranslation("msg.lootgames.ms.bomb_touched"),
                TextFormatting.DARK_PURPLE, getBoardSize() / 2 + 7);

        saveDataAndSendToClient();

        attemptCount++;
    }

    private void onDetonateTimePassed() {
        if (attemptCount < ATTEMPTS_ALLOWED) {
            detonateBoard();
            updateStage(Stage.DEBUG);
        } else {

        }
    }

    private void detonateBoard() {
        MSField[][] asArray = board.asArray();
        for (int x = 0; x < asArray.length; x++) {
            MSField[] msFields = asArray[x];
            for (int y = 0; y < msFields.length; y++) {
                MSField msField = msFields[y];
                if (msField.getType() == MSField.BOMB) {
                    BlockPos pos = convertToBlockPos(x, y);

                    serverTaskPostponer.addTask(new TaskCreateExplosion(pos, 4, false), LootGames.RAND.nextInt(10 * 20));//FIXME change strength
                }
            }
        }
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

    private BlockPos convertToBlockPos(int x, int y) {
        return masterTileEntity.getPos().add(x, 0, y);
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
            NBTTagCompound boardTag = NBTUtils.writeTwoDimArrToNBT(board.asArray());
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
            NBTTagCompound boardTag = NBTUtils.<MSField>writeTwoDimArrToNBT(board.asArray(), field -> {
                NBTTagCompound c = new NBTTagCompound();

                c.setBoolean("hidden", field.isHidden());
                c.setInteger("mark", field.getMark());

                if (!field.isHidden()) {
                    c.setInteger("type", field.getType());
                }

                return c;
            });
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
        DETONATING, DEBUG
    }

    public static class Factory implements ILootGameFactory {
        @Override
        public void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos, BlockPos bottomPos, BlockPos topPos) {
            BlockPos floorCenterPos = puzzleMasterPos.add(0, -DungeonGenerator.PUZZLEROOM_MASTER_TE_OFFSET + 1, 0);
            world.setBlockState(floorCenterPos, ModBlocks.MS_ACTIVATOR.getDefaultState());
        }
    }
}

