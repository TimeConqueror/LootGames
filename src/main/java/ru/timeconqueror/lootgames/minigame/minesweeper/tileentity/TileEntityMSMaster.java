package ru.timeconqueror.lootgames.minigame.minesweeper.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import ru.timeconqueror.lootgames.api.tileentity.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.config.LootGamesConfig;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;

public class TileEntityMSMaster extends TileEntityGameMaster<GameMineSweeper> {
    public TileEntityMSMaster() {
        super(new GameMineSweeper(LootGamesConfig.minesweeper.boardSize, LootGamesConfig.minesweeper.bombAmount));
    }

    @Override
    public void onSubordinateBlockClicked(BlockPos subordinatePos, EntityPlayer player) {
        Pos2i pos = GameMineSweeper.convertToGamePos(getPos(), subordinatePos);

        if (!isBoardGenerated()) {
            game.generateBoard(pos);
            setBlockToUpdateAndSave();
        } else {
            if (player.isSneaking()) {
                game.swapFieldMark(pos);
            } else {
                game.revealField(pos);
            }
        }
    }

    @Override
    protected NBTTagCompound writeTEDataToNBT(NBTTagCompound compound) {
        return super.writeTEDataToNBT(compound);
    }

    @Override
    protected void readTEDataFromNBT(NBTTagCompound compound) {
        super.readTEDataFromNBT(compound);
    }

    public boolean isBoardGenerated() {
        return game.isBoardGenerated();
    }

    @Override
    protected boolean isDataSyncsEntirely() {
        return false;
    }

    @Override
    public NBTTagCompound writeClientSyncedNBT(NBTTagCompound compound) {
        return super.writeClientSyncedNBT(compound);
    }

    @Override
    public void readClientSyncedNBT(NBTTagCompound compound) {
        super.readClientSyncedNBT(compound);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }
}
