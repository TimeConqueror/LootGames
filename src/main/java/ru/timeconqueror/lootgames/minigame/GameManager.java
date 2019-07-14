package ru.timeconqueror.lootgames.minigame;

import ru.timeconqueror.lootgames.LootGames;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<ILootGame> lootGames = new ArrayList<>();

    public void registerGame(ILootGame game) {
        lootGames.add(game);
    }

    public ILootGame getRandomGame() {
        return lootGames.get(LootGames.rand.nextInt(lootGames.size()));
    }
}
