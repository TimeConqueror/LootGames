package ru.timeconqueror.lootgames.minigame;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ILootGame {
    /**
     * Executed upon structure-generation.
     *
     * @param world           The world for the Generator
     * @param puzzleMasterPos The position of Puzzle Master Block
     * @param bottomPos       The position with the lowest coordinates of the structure
     * @param topPos          The position with the highest coordinates of the structure
     * @return TRUE, if you have set any block, FALSE, if you have not.
     */
    boolean generate(World world, BlockPos puzzleMasterPos, BlockPos bottomPos, BlockPos topPos);
}
