package ru.timeconqueror.lootgames.api.room;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.Requirements;
import ru.timeconqueror.timecore.api.util.Vec2i;
import ru.timeconqueror.timecore.common.capability.property.serializer.IPropertySerializer;

public final class RoomCoords {
    public static final int ROOM_SIZE = 8 * 16;
    public static final int BLOCK_SHIFT = Util.make(Mth.log2(ROOM_SIZE), offset -> Requirements.greaterOrEquals(offset, 4));
    public static final int CHUNK_SHIFT = BLOCK_SHIFT - 4;

    private final int x;
    private final int z;

    private RoomCoords(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static boolean isSameRoom(BlockPos pos, BlockPos pos2) {
        return pos.getX() >> BLOCK_SHIFT == pos2.getX() >> BLOCK_SHIFT && pos.getZ() >> BLOCK_SHIFT == pos2.getZ() >> BLOCK_SHIFT;
    }

    public static boolean isSameRoom(BlockPos pos, RoomCoords pos2) {
        return pos.getX() >> BLOCK_SHIFT == pos2.x && pos.getZ() >> BLOCK_SHIFT == pos2.z;
    }

    public static RoomCoords of(BlockPos pos) {
        return ofPos(pos.getX(), pos.getZ());
    }

    public static RoomCoords of(int index) {
        Vec2i offset = MathUtils.OutwardSquareSpiral.offsetByIndex(index);
        return new RoomCoords(offset.x(), offset.y());
    }

    public static RoomCoords of(ChunkPos chunkPos) {
        return of(chunkPos.x, chunkPos.z);
    }

    public static RoomCoords of(LevelChunk chunk) {
        return of(chunk.getPos());
    }

    public static RoomCoords of(int chunkX, int chunkZ) {
        return new RoomCoords(chunkX >> CHUNK_SHIFT, chunkZ >> CHUNK_SHIFT);
    }

    public static RoomCoords ofPos(int blockX, int blockZ) {
        return of(blockX >> 4, blockZ >> 4);
    }

    public BlockPos lowestCorner() {
        return new BlockPos(x() << BLOCK_SHIFT, 0, z() << BLOCK_SHIFT);
    }

    public int x() {
        return x;
    }

    public int z() {
        return z;
    }

    public ChunkPos containerPos() {
        return new ChunkPos(x << CHUNK_SHIFT, z << CHUNK_SHIFT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomCoords that)) return false;

        if (x != that.x) return false;
        return z == that.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "RoomCoords[" + x + ", " + z + ']';
    }

    public static class Serializer implements IPropertySerializer<RoomCoords> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public RoomCoords deserialize(@NotNull String s, @NotNull CompoundTag compoundTag) {
            int[] array = compoundTag.getIntArray(s);
            return new RoomCoords(array[0], array[1]);
        }

        @Override
        public void serialize(@NotNull String s, RoomCoords roomCoords, @NotNull CompoundTag compoundTag) {
            compoundTag.putIntArray(s, new int[]{roomCoords.x, roomCoords.z});
        }
    }
}
