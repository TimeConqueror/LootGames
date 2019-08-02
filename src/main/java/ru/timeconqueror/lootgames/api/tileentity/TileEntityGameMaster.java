package ru.timeconqueror.lootgames.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import ru.timeconqueror.lootgames.api.minigame.AbstractLootGame;

public abstract class TileEntityGameMaster<T extends AbstractLootGame> extends TileEntityGameBlock {
    private T game;

    public TileEntityGameMaster(T game) {
        this.game = game;
    }

    /**
     * Overriding it, obligatorily call {@link TileEntityGameMaster#writeToNBT(NBTTagCompound)}!
     */
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound gameData = game.serializeNBT();
        compound.setTag("game_data", gameData);

        return super.writeToNBT(compound);
    }

    /**
     * Overriding it, obligatorily call {@link TileEntityGameMaster#readFromNBT(NBTTagCompound)} <b>FIRSTLY</b>!
     */
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        NBTTagCompound gameData = compound.getCompoundTag("game_data");
        game.deserializeNBT(gameData);
    }

    /**
     * If true, sending to client will call only {@link #writeClientPermittedDataToNBT(NBTTagCompound)}
     * and {@link #readClientPermittedDataFromNBT(NBTTagCompound)} to prevent client from seeing server-only secret data.
     * <p>If false, packets will use standard writeTo/readFrom NBT methods.
     */
    @Override
    protected boolean sendOnlyPermittedDataToClient() {
        return true;
    }
}
