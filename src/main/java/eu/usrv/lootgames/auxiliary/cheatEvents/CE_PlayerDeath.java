package eu.usrv.lootgames.auxiliary.cheatEvents;

import net.minecraft.entity.player.EntityPlayer;

public class CE_PlayerDeath implements ICheatEvent {

    @Override
    public void doEvent(EntityPlayer pPlayer) {
        pPlayer.setHealth(0.0F);
        pPlayer.setDead();
    }
}
