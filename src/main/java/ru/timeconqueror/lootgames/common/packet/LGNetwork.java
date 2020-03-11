package ru.timeconqueror.lootgames.common.packet;

import net.minecraftforge.fml.network.simple.SimpleChannel;
import ru.timeconqueror.lootgames.api.packet.SPacketGameUpdate;
import ru.timeconqueror.timecore.api.registry.PacketTimeRegistry;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable
public class LGNetwork extends PacketTimeRegistry {
    private static final String PROTOCOL_VERSION = "1";//TODO check between versions
    public static final SimpleChannel INSTANCE = PacketTimeRegistry.newChannel("main",
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    @Override
    protected void register() {
        regPacket(INSTANCE, SPacketGameUpdate.class, new SPacketGameUpdate.Handler());
    }
}
