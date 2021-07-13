package eu.usrv.legacylootgames.auxiliary;


import eu.usrv.legacylootgames.LootGamesLegacy;
import eu.usrv.legacylootgames.auxiliary.cheatEvents.CE_PlayerDeath;
import eu.usrv.legacylootgames.auxiliary.cheatEvents.CE_TeleportAway;
import eu.usrv.legacylootgames.auxiliary.cheatEvents.CE_TeleportNether;
import eu.usrv.legacylootgames.auxiliary.cheatEvents.ICheatEvent;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;


public class CheaterHandler {
    private final List<ICheatEvent> mCheatEvents = new ArrayList<ICheatEvent>();

    public CheaterHandler() {
        registerCheatEvent(new CE_PlayerDeath());
        registerCheatEvent(new CE_TeleportAway());
        registerCheatEvent(new CE_TeleportNether());
    }

    public void registerCheatEvent(ICheatEvent pEvent) {
        mCheatEvents.add(pEvent);
    }

    public void doRandomCheaterEvent(EntityPlayer pPlayer) {
        int tRnd = LootGamesLegacy.Rnd.nextInt(mCheatEvents.size());

        mCheatEvents.get(tRnd).doEvent(pPlayer);
    }
}
