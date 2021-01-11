package ru.timeconqueror.lootgames.common.block.tile;

import ru.timeconqueror.lootgames.api.block.tile.BoardGameMasterTile;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.registry.LGTiles;

public class GOLMasterTile extends BoardGameMasterTile<GameOfLight> {
    public GOLMasterTile() {
        super(LGTiles.GOL_MASTER, new GameOfLight());
    }
}
