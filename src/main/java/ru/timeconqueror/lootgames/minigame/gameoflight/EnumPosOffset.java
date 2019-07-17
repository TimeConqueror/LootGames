package ru.timeconqueror.lootgames.minigame.gameoflight;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

public enum EnumPosOffset implements IStringSerializable {
    NORTH(0, "north", 0, -1),
    NORTH_EAST(1, "north_east", 1, -1),
    EAST(2, "east", 1, 0),
    SOUTH_EAST(3, "south_east", 1, 1),
    SOUTH(4, "south", 0, 1),
    SOUTH_WEST(5, "south_west", -1, 1),
    WEST(6, "west", -1, 0),
    NORTH_WEST(7, "north_west", -1, -1);

    private static final EnumPosOffset[] LOOKUP = new EnumPosOffset[EnumPosOffset.values().length];

    static {
        for (EnumPosOffset value : values()) {
            LOOKUP[value.index] = value;
        }
    }

    private final String name;
    private final int index;
    /**
     * Offset from master block
     */
    private final int offsetX;
    /**
     * Offset from master block
     */
    private final int offsetZ;

    EnumPosOffset(int index, String name, int offsetX, int offsetZ) {
        this.index = index;
        this.name = name;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
    }

    public static EnumPosOffset byIndex(int index) {
        return LOOKUP[index];
    }

    @Override
    public String getName() {
        return name;
    }

    public BlockPos getMasterBlockPos(BlockPos subordinatePos) {
        return subordinatePos.add(offsetX != 0 ? -offsetX : 0, 0, offsetZ != 0 ? -offsetZ : 0);
    }

    public int getIndex() {
        return index;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetZ() {
        return offsetZ;
    }

    @Override
    public String toString() {
        return name;
    }
}