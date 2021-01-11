package ru.timeconqueror.lootgames.common.block.tile;

import ru.timeconqueror.lootgames.api.block.tile.TileBoardGameMaster;
import ru.timeconqueror.lootgames.minigame.pipes.GamePipes;
import ru.timeconqueror.lootgames.registry.LGTiles;

public class TileEntityPipesMaster extends TileBoardGameMaster<GamePipes> {
    public TileEntityPipesMaster() {
        super(LGTiles.PIPES_MASTER, new GamePipes());
    }
}
