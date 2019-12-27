package ru.timeconqueror.lootgames.minigame.minesweeper.task;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.task.ITask;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.minigame.minesweeper.MSBoard;
import ru.timeconqueror.lootgames.minigame.minesweeper.tileentity.TileEntityMSMaster;

public class TaskMSCreateExplosion implements ITask {
    private Pos2i relativePos;
    private BlockPos masterPos;
    private float strength;
    private boolean damagesTerrain;

    @ApiStatus.Internal
    public TaskMSCreateExplosion() {
    }

    public TaskMSCreateExplosion(BlockPos masterPos, Pos2i relativePos, float strength, boolean damagesTerrain) {
        this.relativePos = relativePos;
        this.masterPos = masterPos;
        this.strength = strength;
        this.damagesTerrain = damagesTerrain;
    }

    @Override
    public void run(World world) {
        TileEntity te = world.getTileEntity(masterPos);
        if (te instanceof TileEntityMSMaster) {
            GameMineSweeper ms = ((TileEntityMSMaster) te).getGame();
            BlockPos relPos = ms.convertToBlockPos(relativePos);

            world.createExplosion(null, relPos.getX(), relPos.getY(), relPos.getZ(), strength, damagesTerrain);

            NBTTagCompound c = new NBTTagCompound();
            c.setInteger("x", relativePos.getX());
            c.setInteger("y", relativePos.getY());
            c.setInteger("type", MSBoard.MSField.EMPTY);
            c.setBoolean("hidden", false);
            c.setInteger("mark", MSBoard.MSField.NO_MARK);
            ms.sendUpdatePacket("field_changed", c);//for bomb vanishing on client
        } else {
            LootGames.logHelper.error("Can't find MineSweeper Master at %s while running TaskMSCreateExplosion...", masterPos);
            world.createExplosion(null, masterPos.getX() + relativePos.getX(), masterPos.getY(), masterPos.getZ() + relativePos.getY(), strength, damagesTerrain);
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound t = new NBTTagCompound();
        t.setDouble("x", masterPos.getX());
        t.setDouble("y", masterPos.getY());
        t.setDouble("z", masterPos.getZ());

        t.setInteger("rel_x", relativePos.getX());
        t.setInteger("rel_y", relativePos.getY());

        t.setFloat("strength", strength);
        t.setBoolean("damagesTerrain", damagesTerrain);
        return t;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        relativePos = new Pos2i(nbt.getInteger("rel_x"), nbt.getInteger("rel_y"));
        masterPos = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
        strength = nbt.getFloat("strength");
        damagesTerrain = nbt.getBoolean("damagesTerrain");
    }
}
