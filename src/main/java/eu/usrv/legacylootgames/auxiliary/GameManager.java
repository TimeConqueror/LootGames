package eu.usrv.legacylootgames.auxiliary;


import eu.usrv.legacylootgames.ILootGame;
import eu.usrv.legacylootgames.LootGamesLegacy;
import eu.usrv.legacylootgames.chess.ChessGame;
import eu.usrv.legacylootgames.gol.GameOfLightGame;
import eu.usrv.yamcore.YAMCore;

import java.util.ArrayList;
import java.util.List;


public class GameManager {
    private final List<ILootGame> _mLootGames;

    public GameManager() {
        _mLootGames = new ArrayList<ILootGame>();
    }

    public void initGames() {
        _mLootGames.add(new GameOfLightGame());

        if (YAMCore.isDebug()) {
            _mLootGames.add(new ChessGame());
        }

        for (ILootGame lg : _mLootGames)
            lg.init();
    }

    public ILootGame selectRandomGame() {
        return _mLootGames.get(LootGamesLegacy.Rnd.nextInt(_mLootGames.size()));
    }
}
