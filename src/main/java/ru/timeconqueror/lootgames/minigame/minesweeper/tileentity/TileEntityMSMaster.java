package ru.timeconqueror.lootgames.minigame.minesweeper.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
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
        } else {
            if (player.isSneaking()) {
                game.swapFieldMark(pos);
            } else {
                game.revealField(pos);
            }
        }
    }

    @Override
    public void destroyGameBlocks() {
        for (int x = 0; x < game.getBoardSize(); x++) {
            for (int z = 0; z < game.getBoardSize(); z++) {
                getWorld().setBlockState(getPos().add(x, 0, z), Blocks.AIR.getDefaultState(), 2);
            }
        }
    }

    @Override
    protected NBTTagCompound writeNBTForSaving(NBTTagCompound compound) {
        return super.writeNBTForSaving(compound);
    }

    @Override
    protected void readNBTFromSave(NBTTagCompound compound) {
        super.readNBTFromSave(compound);
    }

    public boolean isBoardGenerated() {
        return game.isBoardGenerated();
    }

    @Override
    public NBTTagCompound writeNBTForClient(NBTTagCompound compound) {
        return super.writeNBTForClient(compound);
    }

    @Override
    public void readNBTFromClient(NBTTagCompound compound) {
        super.readNBTFromClient(compound);
    }

    @NotNull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }
}
