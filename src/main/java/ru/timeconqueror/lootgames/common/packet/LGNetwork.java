package ru.timeconqueror.lootgames.common.packet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.packet.room.SLoadRoomPacket;
import ru.timeconqueror.lootgames.common.packet.room.SSyncGamePacket;
import ru.timeconqueror.timecore.api.registry.PacketRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class LGNetwork {
    @AutoRegistrable
    private static final PacketRegister REGISTER = new PacketRegister(LootGames.MODID);

    private static final String PROTOCOL_VERSION = "1";//TODO check between versions
    public static final SimpleChannel INSTANCE = REGISTER.createChannel("main", () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals)
            .regPacket(SPacketGameUpdate.class, SPacketGameUpdate.makeHandler(), NetworkDirection.PLAY_TO_CLIENT)
            .regPacket(CPacketGameUpdate.class, CPacketGameUpdate.makeHandler(), NetworkDirection.PLAY_TO_SERVER)
            .regPacket(SLoadRoomPacket.class)
            .regPacket(SSyncGamePacket.class)
            .asChannel();

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        LGNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
