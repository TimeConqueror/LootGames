package ru.timeconqueror.lootgames.packets;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import ru.timeconqueror.lootgames.LootGames;

public class NetworkHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LootGames.MODID);

    public static void registerPackets() {
        int idx = 0;
        INSTANCE.registerMessage(CMessageGOLFeedback.Handler.class, CMessageGOLFeedback.class, idx++, Side.SERVER);
        INSTANCE.registerMessage(SMessageGOLParticle.Handler.class, SMessageGOLParticle.class, idx++, Side.CLIENT);
        INSTANCE.registerMessage(SMessageGOLDrawStuff.Handler.class, SMessageGOLDrawStuff.class, idx++, Side.CLIENT);
    }
}
