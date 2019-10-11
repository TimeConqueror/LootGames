package ru.timeconqueror.lootgames.minigame.minesweeper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.api.minigame.AbstractLootGame;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.api.util.NBTUtils;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.packets.NetworkHandler;
import ru.timeconqueror.lootgames.packets.SMessageMSUpdateBoard;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.world.gen.DungeonGenerator;

public class GameMineSweeper extends AbstractLootGame {
    private MSBoard board;

    @SideOnly(Side.CLIENT)
    public boolean cIsGenerated;

    public GameMineSweeper(int boardSize, int bombCount) {
        board = new MSBoard(boardSize, bombCount);
    }

    public static Pos2i convertToGamePos(BlockPos masterPos, BlockPos subordinatePos) {
        BlockPos tpos = subordinatePos.subtract(masterPos);
        return new Pos2i(tpos.getX(), tpos.getZ());
    }

    public boolean isBoardGenerated() {
        return board.isGenerated();
    }

    public int getBoardSize() {
        return board.size();
    }

    public void generateBoard(Pos2i clickedPos) {
        board.generate(clickedPos);

        if (board.getFieldTypeByPos(clickedPos) == 0) {
            revealAllNeighbours(clickedPos);
        }
    }

    public void revealField(Pos2i pos) {
        MSBoard.MSField field = board.getFieldByPos(pos);
        if (field.isHidden()) {
            field.resetMark();
            field.reveal();

            NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(masterTileEntity.getWorld().provider.getDimension(), masterTileEntity.getPos().getX(), masterTileEntity.getPos().getY(), masterTileEntity.getPos().getZ(), -1);
            NetworkHandler.INSTANCE.sendToAllTracking(new SMessageMSUpdateBoard(masterTileEntity.getPos(), pos, field.getType(), -1), point);

            if (field.getType() == 0) {
                revealAllNeighbours(pos);
            }
        }

        masterTileEntity.markDirty();
    }

    public void swapFieldMark(Pos2i pos) {
        MSBoard.MSField field = board.getFieldByPos(pos);
        if (field.isHidden()) {
            field.swapMark();

            NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(masterTileEntity.getWorld().provider.getDimension(), masterTileEntity.getPos().getX(), masterTileEntity.getPos().getY(), masterTileEntity.getPos().getZ(), -1);
            NetworkHandler.INSTANCE.sendToAllTracking(new SMessageMSUpdateBoard(masterTileEntity.getPos(), pos, 0, field.getMark()), point);

            masterTileEntity.markDirty();
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
                    MSBoard.MSField field = board.getFieldByPos(pos);
                    if (field.isHidden()) {
                        revealField(pos);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void onUpdateMessageReceived(SMessageMSUpdateBoard msg) {
        Pos2i pos = msg.getRelatedSubPos();
        board.setField(pos, msg.getType(), false, msg.getMark());
    }

    public MSBoard getBoard() {
        return board;
    }

    @Override
    public boolean isDataSyncsEntirely() {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(boolean isForSendingToClient) {
        NBTTagCompound gameCompound = super.writeToNBT(isForSendingToClient);

        if (isBoardGenerated()) {
            NBTTagCompound boardTag = NBTUtils.writeTwoDimArrToNBT(board.asArray());
            gameCompound.setTag("board", boardTag);
        }

        return gameCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound, boolean callClientSyncedPart, boolean isReadFromClient) {
//        System.out.println("readFromNBTStart:" + callClientSyncedPart + " " + isReadFromClient);
//        System.out.println(compound);
        super.readFromNBT(compound, callClientSyncedPart, isReadFromClient);

        if (compound.hasKey("board")) {
            NBTTagCompound boardTag = compound.getCompoundTag("board");
            MSBoard.MSField[][] boardArr = NBTUtils.readTwoDimArrFromNBT(boardTag, MSBoard.MSField.class, () -> new MSBoard.MSField(0, true, -1));
            board.setBoard(boardArr);
        }
//        System.out.println("readFromNBTEnd");
    }

    @Override
    public NBTTagCompound writeClientSyncedNBT(boolean isForSendingToClient) {
        NBTTagCompound compound = super.writeClientSyncedNBT(isForSendingToClient);

        compound.setInteger("bomb_count", board.getBombCount());
        compound.setInteger("board_size", board.size());

        if (isForSendingToClient) {
            compound.setBoolean("c_is_generated", isBoardGenerated());
            if (isBoardGenerated()) {
                NBTTagCompound boardTag = NBTUtils.writeTwoDimArrToNBT(board.asArray(), msField -> !msField.isHidden());
                compound.setTag("c_board", boardTag);
            }
        }
        return compound;
    }

    @Override
    public void readClientSyncedNBT(NBTTagCompound compound, boolean isReadFromClient) {
        super.readClientSyncedNBT(compound, isReadFromClient);

//        System.out.println("readClientSyncedNBT");
//        System.out.println(compound);
        board.setBombCount(compound.getInteger("bomb_count"));
        board.setSize(compound.getInteger("board_size"));

        if (isReadFromClient) {
            cIsGenerated = compound.getBoolean("c_is_generated");
            if (compound.hasKey("c_board")) {
                NBTTagCompound boardTag = compound.getCompoundTag("c_board");
                MSBoard.MSField[][] boardArr = NBTUtils.readTwoDimArrFromNBT(boardTag, MSBoard.MSField.class, () -> new MSBoard.MSField(0, true, -1));
                board.setBoard(boardArr);
            }
        }

//        System.out.println(Arrays.deepToString(board.asArray()));
    }

    public static class Factory implements ILootGameFactory {
        @Override
        public void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos, BlockPos bottomPos, BlockPos topPos) {
            BlockPos floorCenterPos = puzzleMasterPos.add(0, -DungeonGenerator.PUZZLEROOM_MASTER_TE_OFFSET + 1, 0);
            world.setBlockState(floorCenterPos, ModBlocks.MS_ACTIVATOR.getDefaultState());
        }
    }
}

