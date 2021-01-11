package ru.timeconqueror.lootgames.common.block.tile;

import ru.timeconqueror.lootgames.api.block.tile.BoardGameMasterTile;
import ru.timeconqueror.lootgames.minigame.pipes.GamePipes;
import ru.timeconqueror.lootgames.registry.LGTiles;

public class PipesMasterTile extends BoardGameMasterTile<GamePipes> {
    public PipesMasterTile() {
        super(LGTiles.PIPES_MASTER, new GamePipes());
    }
}
