package ru.timeconqueror.lootgames.tileentity;

import net.minecraft.nbt.NBTTagCompound;

import static ru.timeconqueror.lootgames.block.BlockGOLSubordinate.EnumPosOffset;

public class TileEntityGOLSubordinate extends TileEntityEnhanced {
    private TileEntityGOLMaster master;
    private EnumPosOffset posOffset;

    public TileEntityGOLSubordinate() {
    }

    void setMaster(TileEntityGOLMaster master) {
        this.master = master;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
//        int posIndex = compound.getInteger("pos_index");
//        posOffset = EnumPosOffset.byIndex(posIndex);
//        BlockPos masterPos = new BlockPos( compound.getInteger("master_x"), compound.getInteger("master_y"), compound.getInteger("master_z"));
//        master = (TileEntityGOLMaster) world.getTileEntity(masterPos);

        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
//        compound.setInteger("pos_index", posOffset.getIndex());
//        BlockPos masterPos = master.getPos();
//        compound.setInteger("master_x", masterPos.getX());
//        compound.setInteger("master_y", masterPos.getY());
//        compound.setInteger("master_z", masterPos.getZ());

        return super.writeToNBT(compound);
    }

    public EnumPosOffset getPosOffset() {
        return posOffset;
    }
}
