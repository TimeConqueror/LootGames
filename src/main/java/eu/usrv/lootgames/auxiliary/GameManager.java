package eu.usrv.lootgames.auxiliary;


import eu.usrv.lootgames.ILootGame;
import eu.usrv.lootgames.LootGames;
import eu.usrv.lootgames.chess.ChessGame;
import eu.usrv.lootgames.gol.GameOfLightGame;
import eu.usrv.yamcore.YAMCore;

import java.util.ArrayList;
import java.util.List;


public class GameManager {
    private List<ILootGame> _mLootGames;

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
        return _mLootGames.get(LootGames.Rnd.nextInt(_mLootGames.size()));
    }
}
