package ru.timeconqueror.lootgames.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2ic;
import org.joml.Vector3d;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.RandHelper;

public class Utils {
    public static int manhattanDistance(Vector2ic vec1, Vector2ic vec2) {
        return Math.abs(vec1.x() - vec2.x()) + Math.abs(vec1.y() - vec2.y());
    }

    // copypaste from Player#drop(ItemStack stack_, boolean throwRandomly_, boolean retainOwnership_)
    public static ItemEntity makeItemThrowing(Level level, Vector3d pos, ItemStack stack, Vector3f direction, float xRotDegrees, float speed /*default: 0.3*/, int pickupDelay) {
        float yRotDegrees = (float) Math.toDegrees((float) Mth.atan2(direction.z(), direction.x())) - 90;

        ItemEntity itementity = new ItemEntity(level, pos.x(), pos.y(), pos.z(), stack);
        itementity.setPickUpDelay(pickupDelay);

        float f8 = Mth.sin(MathUtils.toRadians(xRotDegrees));
        float f2 = Mth.cos(MathUtils.toRadians(xRotDegrees));
        float f3 = Mth.sin(MathUtils.toRadians(yRotDegrees));
        float f4 = Mth.cos(MathUtils.toRadians(yRotDegrees));
        float f5 = RandHelper.RAND.nextFloat() * ((float) Math.PI * 2F);
        float randomOffset = 0.02F * RandHelper.RAND.nextFloat();

        itementity.setDeltaMovement(
                (-f3 * f2 * speed) + Math.cos(f5) * randomOffset,
                -f8 * speed + 0.2F,
                (f4 * f2 * speed) + Math.sin(f5) * randomOffset
        );

        return itementity;
    }

    public static Vector3d blockPosToVec3d(BlockPos pos) {
        return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vector3f vec3iToVec3f(Vec3i vec) {
        return new Vector3f(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vector3f vec3ToVec3f(Vec3 vec) {
        return new Vector3f((float) vec.x(), (float) vec.y(), (float) vec.z());
    }

    /**
     * Calculates and returns yaw
     */
    public static float getLookAngle(Vector3f source, Vector3f target) {
        double dx = target.x() - source.x();
        double dz = target.z() - source.z();

        return (float) Mth.atan2(dx, dz);
    }

    //TODO randomize target pos?
    public static void teleportToSpawn(ServerPlayer player) {
        ServerLevel respLevel = player.server.getLevel(player.getRespawnDimension());
        if (respLevel == null) {
            respLevel = player.server.overworld();
        }
        BlockPos respawnPos = player.getRespawnPosition();
        float respawnAngle = player.getRespawnAngle();
        if (respawnPos == null) {
            respawnPos = respLevel.getSharedSpawnPos();
            respawnAngle = respLevel.getSharedSpawnAngle();
        }

        player.teleportTo(respLevel, respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), respawnAngle, 0);
    }
}
