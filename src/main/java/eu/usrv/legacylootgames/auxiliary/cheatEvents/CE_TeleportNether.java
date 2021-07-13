
package eu.usrv.legacylootgames.auxiliary.cheatEvents;


import eu.usrv.legacylootgames.LootGamesLegacy;
import eu.usrv.legacylootgames.auxiliary.TeleportHelper;
import eu.usrv.legacylootgames.auxiliary.TeleportHelper.TeleportPoint;
import net.minecraft.entity.player.EntityPlayer;


public class CE_TeleportNether implements ICheatEvent {

    @Override
    public void doEvent(EntityPlayer pPlayer) {
        TeleportPoint tDest = new TeleportPoint();
        tDest.dimID = -1;
        tDest.pitch = pPlayer.rotationPitch;
        tDest.yaw = pPlayer.cameraYaw;
        tDest.x = LootGamesLegacy.Rnd.nextInt(1000) - 500;
        tDest.y = LootGamesLegacy.Rnd.nextInt(246) + 10;
        tDest.z = LootGamesLegacy.Rnd.nextInt(1000) - 500;

        TeleportHelper.teleportEntity(pPlayer, tDest);
    }
}
