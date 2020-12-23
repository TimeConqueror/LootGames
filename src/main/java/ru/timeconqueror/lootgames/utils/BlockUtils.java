package ru.timeconqueror.lootgames.utils;

import net.minecraft.util.math.BlockPos;

import java.util.Iterator;

public class BlockUtils {

    public static Iterable<BlockPos> iterateArea(BlockPos startPos, int xSize, int ySize, int zSize) {
        return () -> new Iterator<BlockPos>() {

            long xyz;
            final BlockPos.Mutable pos = startPos.mutable();
            final long xyzSize = (long) xSize * ySize * zSize;

            @Override
            public boolean hasNext() {
                return xyz < xyzSize;
            }

            @Override
            public BlockPos next() {
                long yz = xyz / xSize;
                int x = (int) (xyz % xSize);
                int y = (int) (yz % ySize);
                int z = (int) (yz / ySize);
                xyz++;

                return pos.setWithOffset(startPos, x, y, z);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("You can not modify block iterator");
            }
        };
    }
}
