
package eu.usrv.lootgames.auxiliary.cheatEvents;


import eu.usrv.lootgames.LootGames;
import eu.usrv.lootgames.auxiliary.TeleportHelper;
import eu.usrv.lootgames.auxiliary.TeleportHelper.TeleportPoint;
import net.minecraft.entity.player.EntityPlayer;


public class CE_TeleportNether implements ICheatEvent {

    @Override
    public void doEvent(EntityPlayer pPlayer) {
        TeleportPoint tDest = new TeleportPoint();
        tDest.dimID = -1;
        tDest.pitch = pPlayer.rotationPitch;
        tDest.yaw = pPlayer.cameraYaw;
        tDest.x = LootGames.Rnd.nextInt(1000) - 500;
        tDest.y = LootGames.Rnd.nextInt(246) + 10;
        tDest.z = LootGames.Rnd.nextInt(1000) - 500;

        TeleportHelper.teleportEntity(pPlayer, tDest);
    }
}
