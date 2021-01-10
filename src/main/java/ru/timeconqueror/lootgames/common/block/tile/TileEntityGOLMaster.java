package ru.timeconqueror.lootgames.common.block.tile;

import ru.timeconqueror.lootgames.api.block.tile.TileBoardGameMaster;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.registry.LGTiles;

public class TileEntityGOLMaster extends TileBoardGameMaster<GameOfLight> {
    public TileEntityGOLMaster() {
        super(LGTiles.GOL_MASTER, new GameOfLight());
    }
}
