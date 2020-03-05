package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ILootGameFactory {

    /**
     * Executed upon structure-generation.
     *
     * @param world           The world for the Generator
     * @param puzzleMasterPos The position of Puzzle Master Block
     * @param bottomPos       The position with the lowest coordinates of the structure
     * @param topPos          The position with the highest coordinates of the structure
     */
    void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos, BlockPos bottomPos, BlockPos topPos);
}
