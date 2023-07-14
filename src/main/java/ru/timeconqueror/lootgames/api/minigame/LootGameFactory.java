package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface LootGameFactory {

    /**
     * Executed upon structure-generation.
     *
     * @param world           The world for the Generator
     * @param puzzleMasterPos The position of Puzzle Master Block
     */
    void genOnPuzzleMasterClick(Level world, BlockPos puzzleMasterPos);
}
