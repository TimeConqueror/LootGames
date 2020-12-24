package ru.timeconqueror.lootgames.common.block.tile;

import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.registry.LGTiles;

public class TileEntityGOLMaster extends TileEntityGameMaster<GameOfLight> {
    public TileEntityGOLMaster() {
        super(LGTiles.GOL_MASTER, new GameOfLight());
    }
}
