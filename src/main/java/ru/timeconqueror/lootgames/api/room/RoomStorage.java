package ru.timeconqueror.lootgames.api.room;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.Requirements;
import ru.timeconqueror.timecore.api.util.Vec2i;

public interface RoomStorage {
    int ROOM_SIZE = 8 * 16;
    int BLOCK_SHIFT = Util.make(Mth.log2(ROOM_SIZE), offset -> Requirements.greaterOrEquals(offset, 4));

    static BlockPos getRoomStart(int roomIndex) {
        Vec2i offset = MathUtils.OutwardSquareSpiral.offsetByIndex(roomIndex);
        return new BlockPos(offset.x() << BLOCK_SHIFT, 0, offset.y() << BLOCK_SHIFT);
    }

    static int getRoomIndex(BlockPos pos) {
        return getRoomIndex(pos.getX(), pos.getZ());
    }

    static int getRoomIndex(int blockX, int blockZ) {
        Vec2i corner = new Vec2i(blockX >> BLOCK_SHIFT, blockZ >> BLOCK_SHIFT);
        return MathUtils.OutwardSquareSpiral.indexByOffset(corner);
    }

    int reserveFreeIndex();

    Room getRoom(RoomCoords coords);
}
