package eu.usrv.legacylootgames.network;


import eu.usrv.legacylootgames.network.msg.SpawnParticleFXMessage;
import eu.usrv.yamcore.network.PacketDispatcher;
import ru.timeconqueror.lootgames.LootGames;


public class NetDispatcher extends PacketDispatcher {
    public static final NetDispatcher INSTANCE = new NetDispatcher();

    public NetDispatcher() {
        super(LootGames.MODID);
    }

    @Override
    public void registerPackets() {
        registerMessage(SpawnParticleFXMessage.SpawnParticleFXMessageHandler.class, SpawnParticleFXMessage.class);
    }
}
