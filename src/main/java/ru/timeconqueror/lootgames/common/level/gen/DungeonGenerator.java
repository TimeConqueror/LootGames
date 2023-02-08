package ru.timeconqueror.lootgames.common.level.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import ru.timeconqueror.lootgames.registry.LGBlocks;
//TODO move away from DungeonGenerator
public class DungeonGenerator {
    private static final BlockState DUNGEON_FLOOR = LGBlocks.DUNGEON_FLOOR.defaultBlockState();

    //TODO make it called only when in structure
    public static void resetUnbreakablePlayField(Level world, BlockPos floorPos) {
        if (!resetUnbreakableFieldsStartingFrom(world, floorPos)) {
            resetUnbreakableFieldsStartingFrom(world, floorPos.north());
            resetUnbreakableFieldsStartingFrom(world, floorPos.east());
            resetUnbreakableFieldsStartingFrom(world, floorPos.south());
            resetUnbreakableFieldsStartingFrom(world, floorPos.west());
        }
    }

    private static boolean resetUnbreakableFieldsStartingFrom(Level world, BlockPos blockPos) {
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
