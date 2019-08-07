package ru.timeconqueror.lootgames.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import ru.timeconqueror.lootgames.api.tileentity.TileEntityGameMaster;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;

public class TileEntityMSMaster extends TileEntityGameMaster<GameMineSweeper> {
    boolean isConstructed = false;

    public TileEntityMSMaster() {
        super(new GameMineSweeper());
    }

    public void onBlockClickedByPlayer(EntityPlayer player) {
        if (!isConstructed) {
        }
    }

    public void generateBlockField() {

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("is_constructed", isConstructed);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        isConstructed = compound.getBoolean("is_constructed");
    }

    @Override
    protected boolean sendOnlyPermittedDataToClient() {
        return false;
    }
}
