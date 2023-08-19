package ru.timeconqueror.lootgames.api.task;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

public class TaskCreateExplosion implements Task {
    protected double x;
    private double y;
    private double z;
    private float strength;
    private Level.ExplosionInteraction explosionMode;

    @ApiStatus.Internal
    public TaskCreateExplosion() {
    }

    public TaskCreateExplosion(BlockPos pos, float strength, Level.ExplosionInteraction explosionMode) {
        this(pos.getX(), pos.getY(), pos.getZ(), strength, explosionMode);
    }

    public TaskCreateExplosion(double x, double y, double z, float strength, Level.ExplosionInteraction explosionMode) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.strength = strength;
        this.explosionMode = explosionMode;
    }

    @Override
    public void run(Level level) {
        level.explode(null, x, y, z, strength, explosionMode);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag c = new CompoundTag();

        c.putDouble("x", x);
        c.putDouble("y", y);
        c.putDouble("z", z);
        c.putFloat("strength", strength);
        c.putInt("explosion_mode", explosionMode.ordinal());

        return c;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        x = nbt.getDouble("x");
        y = nbt.getDouble("y");
        z = nbt.getDouble("z");
        strength = nbt.getFloat("strength");
        explosionMode = Level.ExplosionInteraction.values()[nbt.getInt("explosion_mode")];
    }
}
