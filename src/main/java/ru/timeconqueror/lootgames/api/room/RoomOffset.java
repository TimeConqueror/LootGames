package ru.timeconqueror.lootgames.api.room;

import net.minecraft.core.Vec3i;
import ru.timeconqueror.timecore.api.util.Requirements;

import javax.annotation.concurrent.Immutable;

@Immutable
public class RoomOffset extends Vec3i {
    public RoomOffset(int x, int y, int z) {
        super(x, y, z);
        validate();
    }

    private void validate() {
        Requirements.inRangeInclusive(getX(), 0, RoomCoords.ROOM_SIZE - 1);
        Requirements.inRangeInclusive(getZ(), 0, RoomCoords.ROOM_SIZE - 1);
    }
}
