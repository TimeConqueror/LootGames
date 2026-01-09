package ru.timeconqueror.lootgames.api.minigame;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomLocalPos;
import ru.timeconqueror.lootgames.utils.MathUtils;

@Getter
public class Leadings2D {
    /**
     * x+ -> x+
     * z+ -> y+
     */
    public static final Leadings2D HORIZONTAL_DEFAULT = new Leadings2D(Direction.EAST, Direction.SOUTH);
    /**
     * x+ -> x+
     * y- -> y+
     */
    public static final Leadings2D VERTICAL_UP_TO_DOWN = new Leadings2D(Direction.EAST, Direction.DOWN);

    private final Direction x;
    private final Direction y;
    private final Vec3i step;

    public Leadings2D(Direction x, Direction y) {
        Direction.Axis xAxis = x.getAxis();
        Direction.Axis yAxis = y.getAxis();
        if (xAxis == yAxis) {
            throw new IllegalArgumentException("Leading direction should have different axis");
        }
        this.x = x;
        this.y = y;

        this.step = new Vec3i(x.getStepX() + y.getStepX(), x.getStepY() + y.getStepY(), x.getStepZ() + y.getStepZ());
    }

    /**
     * The corner, which will have the least flat coords.
     */
    public BlockPos origin(BlockPos center, int size) {
        return center.offset(-size / 2 * step.getX(), -size / 2 * step.getY(), -size / 2 * step.getZ());
    }

    private static int toFlatCoord(Direction direction, Vec3i vec) {
        int mult = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1;
        return vec.get(direction.getAxis()) * mult;
    }

    public Vector2ic toFlatPos(RoomLocalPos origin, RoomLocalPos pos) {
        return toFlatPos_(origin, pos);
    }

    public Vector2ic toFlatPos(BlockPos origin, BlockPos pos) {
        return toFlatPos_(origin, pos);
    }

    public Vector2ic toFlatPos_(Vec3i origin, Vec3i pos) {
        Vec3i offset = pos.subtract(origin);
        return new Vector2i(toFlatCoord(x, offset), toFlatCoord(y, offset));
    }

    public BlockPos fromFlatPos(BlockPos origin, Vector2ic flatPos) {
        MutableBlockPos pos = new MutableBlockPos();
        setByAxis(x, pos, flatPos.x());
        setByAxis(y, pos, flatPos.y());

        return origin.offset(pos);
    }

    private static void setByAxis(Direction direction, MutableBlockPos pos, int val) {
        int mult = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1;
        val = val * mult;

        switch (direction.getAxis()) {
            case X -> pos.setX(val);
            case Y -> pos.setY(val);
            case Z -> pos.setZ(val);
        }
    }

    public boolean isPosOnRange(RoomLocalPos origin, int size, RoomLocalPos pos) {
        return isPosOnRange_(origin, size, pos);
    }

    public boolean isPosOnRange(BlockPos origin, int size, BlockPos pos) {
        return isPosOnRange_(origin, size, pos);
    }

    private boolean isPosOnRange_(Vec3i origin, int size, Vec3i pos) {
        Vec3i lastPos = step.multiply(size).offset(origin);

        return MathUtils.isBetweenIncluding(pos.getX(), origin.getX(), lastPos.getX())
                && MathUtils.isBetweenIncluding(pos.getY(), origin.getY(), lastPos.getY())
                && MathUtils.isBetweenIncluding(pos.getZ(), origin.getZ(), lastPos.getZ());
    }
}