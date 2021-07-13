
package eu.usrv.legacylootgames.auxiliary.cheatEvents;


import eu.usrv.legacylootgames.LootGamesLegacy;
import net.minecraft.entity.player.EntityPlayer;


public class CE_TeleportAway implements ICheatEvent {

    @Override
    public void doEvent(EntityPlayer pPlayer) {
        double newX = pPlayer.posX + (LootGamesLegacy.Rnd.nextInt(1000) - 500);
        double newZ = pPlayer.posZ + (LootGamesLegacy.Rnd.nextInt(1000) - 500);
        double newY = 200;

        pPlayer.setPositionAndUpdate(newX, newY, newZ);
    }
}
