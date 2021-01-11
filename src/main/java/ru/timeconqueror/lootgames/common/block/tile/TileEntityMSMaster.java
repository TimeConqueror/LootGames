package ru.timeconqueror.lootgames.common.block.tile;

import ru.timeconqueror.lootgames.api.block.tile.TileBoardGameMaster;
import ru.timeconqueror.lootgames.common.config.ConfigMS;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.LGTiles;

public class TileEntityMSMaster extends TileBoardGameMaster<GameMineSweeper> {
    public TileEntityMSMaster() {
        super(LGTiles.MS_MASTER, new GameMineSweeper());
    }

    public void init(ConfigMS.Snapshot configSnapshot) {
        game.setConfigSnapshot(configSnapshot);
    }
}
