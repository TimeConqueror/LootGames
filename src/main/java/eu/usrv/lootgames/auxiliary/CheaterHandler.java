package eu.usrv.lootgames.auxiliary;


import eu.usrv.lootgames.LootGames;
import eu.usrv.lootgames.auxiliary.cheatEvents.CE_PlayerDeath;
import eu.usrv.lootgames.auxiliary.cheatEvents.CE_TeleportAway;
import eu.usrv.lootgames.auxiliary.cheatEvents.CE_TeleportNether;
import eu.usrv.lootgames.auxiliary.cheatEvents.ICheatEvent;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;


public class CheaterHandler {
    private List<ICheatEvent> mCheatEvents = new ArrayList<ICheatEvent>();

    public CheaterHandler() {
        registerCheatEvent(new CE_PlayerDeath());
        registerCheatEvent(new CE_TeleportAway());
        registerCheatEvent(new CE_TeleportNether());
    }

    public void registerCheatEvent(ICheatEvent pEvent) {
        mCheatEvents.add(pEvent);
    }

    public void doRandomCheaterEvent(EntityPlayer pPlayer) {
        int tRnd = LootGames.Rnd.nextInt(mCheatEvents.size());

        mCheatEvents.get(tRnd).doEvent(pPlayer);
    }
}
