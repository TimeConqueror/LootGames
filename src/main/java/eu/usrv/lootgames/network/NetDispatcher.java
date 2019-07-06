package eu.usrv.lootgames.network;


import eu.usrv.lootgames.LootGames;
import eu.usrv.lootgames.network.msg.SpawnParticleFXMessage;
import eu.usrv.yamcore.network.PacketDispatcher;


public class NetDispatcher extends PacketDispatcher {
    public NetDispatcher() {
        super(LootGames.MODID);
    }

    @Override
    public void registerPackets() {
        registerMessage(SpawnParticleFXMessage.SpawnParticleFXMessageHandler.class, SpawnParticleFXMessage.class);
    }
}
