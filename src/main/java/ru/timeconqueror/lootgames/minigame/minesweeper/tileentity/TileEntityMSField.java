//package ru.timeconqueror.lootgames.minigame.minesweeper.tileentity;
//
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.nbt.NBTTagCompound;
//import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
//import ru.timeconqueror.lootgames.api.util.Pos2iZ;
//
//public class TileEntityMSField extends TileEntityGameMaster {
//    private int type = 0;
//    private boolean hidden = true;
//    private boolean isMaster = false;
//    private Pos2iZ masterOffset;
//
//    private GameMineSweeper mineSweeper;
//
//    public TileEntityMSField(){
//        masterOffset = new Pos2iZ(0,0);
//    }
//
//    public void onBlockClickedByPlayer(EntityPlayer player) {
//        if (hidden) {
//            reveal();
//        }
//    }
//
//    private void reveal() {
//        hidden = false;
//        setBlockToUpdateAndSave();
//    }
//
//    public void initMSField(Pos2iZ offset){
//        masterOffset = offset;
//        if(offset.getX() == 0 && offset.getZ() == 0){
//            isMaster = true;
//        }
//
//        if(isMaster){
//            mineSweeper = new GameMineSweeper();
//        }
//    }
//
//    @Override
//    public void readNBTFromSave(NBTTagCompound compound) {
//        type = compound.getInteger("type");
//        isMaster = compound.getBoolean("is_master");
//
//        masterOffset = new Pos2iZ(compound.getInteger("master_x"), compound.getInteger("master_z"));
//        this.readFromNBTNonSecretData(compound);
//    }
//
//    @Override
//    public NBTTagCompound writeNBTForSaving(NBTTagCompound compound) {
//        compound.setBoolean("is_master", isMaster);
//        compound.setInteger("type", type);
//
//        compound.setInteger("master_x", masterOffset.getX());
//        compound.setInteger("master_z", masterOffset.getZ());
//        return this.writeToNBTNonSecretData(compound);
//    }
//
//    /**
//     * Reads the data, that can be sent to client.
//     */
//    public void readFromNBTNonSecretData(NBTTagCompound compound) {
//        hidden = compound.getBoolean("hidden");
//        super.readNBTFromSave(compound);
//    }
//
//    /**
//     * Writes the data, that can be sent to client.
//     */
//    public NBTTagCompound writeToNBTNonSecretData(NBTTagCompound compound) {
//        compound.setBoolean("hidden", hidden);
//        return super.writeNBTForSaving(compound);
//    }
//
//    @Override
//    protected boolean isDataSyncsEntirely() {
//        return true;
//    }
//}
