package ru.timeconqueror.timecore.api.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class WorldUtilsX {
    @Nullable
    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> makeSimpleTicker(BlockEntityType<A> providedType, BlockEntityType<E> requiredType, BlockEntityTicker<? super E> ticker) {
        return requiredType == providedType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> makeSimpleTicker(BlockEntityType<A> providedType, Supplier<BlockEntityType<E>> requiredType, BlockEntityTicker<? super E> ticker) {
        return requiredType.get() == providedType ? (BlockEntityTicker<A>) ticker : null;
    }

    public static Explosion explode(Level level, @Nullable Entity entityIn, double xIn, double yIn, double zIn, float explosionRadius, Explosion.BlockInteraction modeIn) {
        return explode(level, entityIn, null, null, xIn, yIn, zIn, explosionRadius, false, modeIn);
    }

    public static Explosion explode(Level level, @Nullable Entity entityIn, double xIn, double yIn, double zIn, float explosionRadius, boolean causesFire, Explosion.BlockInteraction modeIn) {
        return explode(level, entityIn, null, null, xIn, yIn, zIn, explosionRadius, causesFire, modeIn);
    }

    public static Explosion explode(Level level, @Nullable Entity exploder, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float size, boolean causesFire, Explosion.BlockInteraction mode) {
        Explosion explosion = new Explosion(level, exploder, damageSource, damageCalculator, x, y, z, size, causesFire, mode);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(level, explosion)) return explosion;
        explosion.explode();
        explosion.finalizeExplosion(true);
        return explosion;
    }
}
