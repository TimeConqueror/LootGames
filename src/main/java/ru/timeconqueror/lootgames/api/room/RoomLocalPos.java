package ru.timeconqueror.lootgames.api.room;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import ru.timeconqueror.timecore.api.util.Requirements;

import javax.annotation.concurrent.Immutable;

import static ru.timeconqueror.lootgames.api.room.RoomCoords.ROOM_SIZE;

@Immutable
public class RoomLocalPos extends Vec3i {
    public RoomLocalPos(int x, int y, int z) {
        super(x, y, z);
        validate();
    }

    public static RoomLocalPos of(BlockPos pos) {
        return RoomCoords.of(pos).toLocalPos(pos);
    }

    public static RoomLocalPos of(RoomCoords roomCoords, BlockPos pos) {
        return roomCoords.toLocalPos(pos);
    }

    public static RoomLocalPos center(int y) {
        return new RoomLocalPos(
                ROOM_SIZE / 2,
                y,
                ROOM_SIZE / 2
        );
    }

    public BlockPos toAbsolutePos(RoomCoords coords) {
        return coords.toAbsolutePos(this);
    }

    private void validate() {
        Requirements.inRangeInclusive(getX(), 0, ROOM_SIZE - 1);
        Requirements.inRangeInclusive(getZ(), 0, ROOM_SIZE - 1);
    }
}
