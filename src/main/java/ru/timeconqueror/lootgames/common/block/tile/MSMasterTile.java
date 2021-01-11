package ru.timeconqueror.lootgames.common.block.tile;

import ru.timeconqueror.lootgames.api.block.tile.BoardGameMasterTile;
import ru.timeconqueror.lootgames.common.config.ConfigMS;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.LGTiles;

public class MSMasterTile extends BoardGameMasterTile<GameMineSweeper> {
    public MSMasterTile() {
        super(LGTiles.MS_MASTER, new GameMineSweeper());
    }

    public void init(ConfigMS.Snapshot configSnapshot) {
        game.setConfigSnapshot(configSnapshot);
    }
}
