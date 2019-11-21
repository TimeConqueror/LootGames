package ru.timeconqueror.lootgames.minigame.gameoflight;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.world.gen.DungeonGenerator;

//TODO Move content here from TE
public class GameOfLight extends LootGame {
    //FIXME check saving and sending/receiving
    public static class Factory implements ILootGameFactory {
        @Override
        public void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos, BlockPos bottomPos, BlockPos topPos) {
            BlockPos floorCenterPos = puzzleMasterPos.add(0, -DungeonGenerator.PUZZLEROOM_MASTER_TE_OFFSET + 1, 0);
            world.setBlockState(floorCenterPos, ModBlocks.GOL_MASTER.getDefaultState());
        }
    }
}
