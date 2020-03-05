package ru.timeconqueror.lootgames.api.task;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

public class TaskCreateExplosion implements ITask {
    protected double x;
    private double y;
    private double z;
    private float strength;
    private Explosion.Mode explosionMode;

    @ApiStatus.Internal
    public TaskCreateExplosion() {
    }

    public TaskCreateExplosion(BlockPos pos, float strength, Explosion.Mode explosionMode) {
        this(pos.getX(), pos.getY(), pos.getZ(), strength, explosionMode);
    }

    public TaskCreateExplosion(double x, double y, double z, float strength, Explosion.Mode explosionMode) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.strength = strength;
        this.explosionMode = explosionMode;
    }

    @Override
    public void run(World world) {
        world.createExplosion(null, x, y, z, strength, explosionMode);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT c = new CompoundNBT();

        c.putDouble("x", x);
        c.putDouble("y", y);
        c.putDouble("z", z);
        c.putFloat("strength", strength);
        c.putInt("explosion_mode", explosionMode.ordinal());

        return c;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        x = nbt.getDouble("x");
        y = nbt.getDouble("y");
        z = nbt.getDouble("z");
        strength = nbt.getFloat("strength");
        explosionMode = Explosion.Mode.values()[nbt.getInt("damagesTerrain")];
    }
}
