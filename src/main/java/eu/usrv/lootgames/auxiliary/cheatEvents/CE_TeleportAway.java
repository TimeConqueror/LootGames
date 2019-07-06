
package eu.usrv.lootgames.auxiliary.cheatEvents;


import eu.usrv.lootgames.LootGames;
import net.minecraft.entity.player.EntityPlayer;


public class CE_TeleportAway implements ICheatEvent {

    @Override
    public void doEvent(EntityPlayer pPlayer) {
        double newX = pPlayer.posX + (LootGames.Rnd.nextInt(1000) - 500);
        double newZ = pPlayer.posZ + (LootGames.Rnd.nextInt(1000) - 500);
        double newY = 200;

        pPlayer.setPositionAndUpdate(newX, newY, newZ);
    }
}
