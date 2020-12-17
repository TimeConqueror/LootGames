package ru.timeconqueror.lootgames.common.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.registry.LGBlocks;

public class DungeonGenerator {
    private static final BlockState DUNGEON_FLOOR = LGBlocks.DUNGEON_FLOOR.defaultBlockState();

    public static void resetUnbreakablePlayField(World world, BlockPos floorPos) {
        if (!resetUnbreakableFieldsStartingFrom(world, floorPos)) {
            resetUnbreakableFieldsStartingFrom(world, floorPos.north());
            resetUnbreakableFieldsStartingFrom(world, floorPos.east());
            resetUnbreakableFieldsStartingFrom(world, floorPos.south());
            resetUnbreakableFieldsStartingFrom(world, floorPos.west());
        }
    }

    private static boolean resetUnbreakableFieldsStartingFrom(World world, BlockPos blockPos) {
        BlockState state = world.getBlockState(blockPos);
        if (state.getBlock() == LGBlocks.SHIELDED_DUNGEON_FLOOR) {
            world.setBlockAndUpdate(blockPos, DUNGEON_FLOOR);

            resetUnbreakableFieldsStartingFrom(world, blockPos.north());
            resetUnbreakableFieldsStartingFrom(world, blockPos.east());
            resetUnbreakableFieldsStartingFrom(world, blockPos.south());
            resetUnbreakableFieldsStartingFrom(world, blockPos.west());

            return true;
        }

        return false;
    }
}
