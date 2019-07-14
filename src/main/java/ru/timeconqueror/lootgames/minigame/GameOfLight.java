package ru.timeconqueror.lootgames.minigame;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.world.gen.DungeonGenerator;

public class GameOfLight implements ILootGame {
    @Override
    public boolean generate(World world, BlockPos puzzleMasterPos, BlockPos bottomPos, BlockPos topPos) {
        LootGames.logHelper.trace("GameOfLightGame => generate()");

        BlockPos floorCenterPos = puzzleMasterPos.add(0, -DungeonGenerator.PUZZLEROOM_MASTER_TE_OFFSET + 1, 0);

        world.setBlockState(floorCenterPos, ModBlocks.GOL_MASTER.getDefaultState());

        return true;
    }
}
