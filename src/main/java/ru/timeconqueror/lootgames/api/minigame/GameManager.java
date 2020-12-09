package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.LootGamesAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Can be accessed via {@link LootGamesAPI#getGameManager()}.
 */
public class GameManager {
    private static final List<ILootGameFactory> GAME_GEN_LIST = new ArrayList<>();

    /**
     * Register game and its factory.
     */
    public void registerGameGenerator(ILootGameFactory generator) {
        GAME_GEN_LIST.add(generator);
    }

    public static boolean isGameListEmpty() {
        return GAME_GEN_LIST.isEmpty();
    }

    /**
     * Generates random game.
     *
     * @param world           The world for the Generator
     * @param puzzleMasterPos The position of Puzzle Master Block
     * @param bottomPos       The position with the lowest coordinates of the structure
     * @param topPos          The position with the highest coordinates of the structure
     */
    public GenResult generateRandomGame(World world, BlockPos puzzleMasterPos, BlockPos bottomPos, BlockPos topPos) {
        if (!isGameListEmpty()) {
            GAME_GEN_LIST.get(world.random.nextInt(GAME_GEN_LIST.size())).genOnPuzzleMasterClick(world, puzzleMasterPos, bottomPos, topPos);
        } else {
            String error = "Can't generate any game on pos" + puzzleMasterPos + ", because game list is empty.";
            return new GenResult(false, error);
        }

        return new GenResult(true, "");
    }

    public static class GenResult {
        private final boolean wasGenerated;
        private final String error;

        public GenResult(boolean wasGenerated, String error) {
            this.wasGenerated = wasGenerated;
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public boolean wasGenerated() {
            return wasGenerated;
        }
    }
}
