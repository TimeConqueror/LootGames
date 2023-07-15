package ru.timeconqueror.lootgames.api.room;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.Vec2i;
import ru.timeconqueror.timecore.common.capability.property.serializer.IPropertySerializer;
import ru.timeconqueror.timecore.common.capability.property.serializer.NullPropertySerializer;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class RoomCoords {
    public static final int ROOM_SIZE = Math.max(8 * 16, 16);
    public static final int BLOCK_SHIFT = Mth.log2(ROOM_SIZE);
    public static final int CHUNK_SHIFT = BLOCK_SHIFT - 4;

    private final int x;
    private final int z;

    public RoomCoords(int x, int z) {
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

    public BlockPos minPos(int minY) {
        return new BlockPos(x << BLOCK_SHIFT, minY, z << BLOCK_SHIFT);
    }

    public BlockPos minPos(Level level) {
        return new BlockPos(x << BLOCK_SHIFT, level.getMinBuildHeight(), z << BLOCK_SHIFT);
    }

    public BlockPos maxPos(int maxY) {
        return new BlockPos((x << BLOCK_SHIFT) + ROOM_SIZE - 1, maxY, (z << BLOCK_SHIFT) + ROOM_SIZE - 1);
    }

    public BlockPos maxPos(Level level) {
        return new BlockPos((x << BLOCK_SHIFT) + ROOM_SIZE - 1, level.getMaxBuildHeight() - 1, (z << BLOCK_SHIFT) + ROOM_SIZE - 1);
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

    public boolean contains(BlockPos pos) {
        return x == pos.getX() >> BLOCK_SHIFT && z == pos.getZ() >> BLOCK_SHIFT;
    }

    public RoomOffset toRelative(BlockPos pos) {
        if (!contains(pos)) {
            throw new IllegalArgumentException();
        }

        int x = pos.getX();
        int z = pos.getZ();
        return new RoomOffset(x - (this.x << BLOCK_SHIFT), pos.getY(), z - (this.z << BLOCK_SHIFT));
    }

    public BlockPos centerPos(int y) {
        return new BlockPos((x << BLOCK_SHIFT) + ROOM_SIZE / 2, y, (z << BLOCK_SHIFT) + ROOM_SIZE / 2);
    }

    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(x);
        byteBuf.writeInt(z);
    }

    public static RoomCoords read(ByteBuf byteBuf) {
        return new RoomCoords(byteBuf.readInt(), byteBuf.readInt());
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
        public static final IPropertySerializer<RoomCoords> INSTANCE = new Serializer();
        public static final IPropertySerializer<RoomCoords> NULLABLE_INSTANCE = new NullPropertySerializer<>(INSTANCE);

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
