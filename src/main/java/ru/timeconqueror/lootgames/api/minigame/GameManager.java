package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.LootGames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Can be accessed via LootGames.gameManager.
 */
public class GameManager {
    private static final Map<Class<? extends AbstractLootGame>, ILootGameFactory> GAME_MAP = new HashMap<>();
    private static final List<ILootGameFactory> GAME_GEN_LIST = new ArrayList<>();

    /**
     * Register game and its factory.
     */
    public <T extends AbstractLootGame> void registerGame(Class<T> clazz, ILootGameFactory generator) {
        GAME_MAP.put(clazz, generator);
        GAME_GEN_LIST.add(generator);
    }

    /**
     * Generates random game.
     *
     * @param world           The world for the Generator
     * @param puzzleMasterPos The position of Puzzle Master Block
     * @param bottomPos       The position with the lowest coordinates of the structure
     * @param topPos          The position with the highest coordinates of the structure
     */
    public void generateRandomGame(World world, BlockPos puzzleMasterPos, BlockPos bottomPos, BlockPos topPos) {
        GAME_GEN_LIST.get(LootGames.rand.nextInt(GAME_GEN_LIST.size())).genOnPuzzleMasterClick(world, puzzleMasterPos, bottomPos, topPos);
    }
}
