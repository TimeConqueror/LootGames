package ru.timeconqueror.lootgames.common.packet;

import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry;
import ru.timeconqueror.lootgames.api.packet.ServerGamePacket;

public class SPacketGameUpdate extends PacketGameUpdate<ServerGamePacket> {
    public SPacketGameUpdate(LootGame<?> game, ServerGamePacket gamePacket) {
        super(game, gamePacket);
    }

    public SPacketGameUpdate() {
    }

    @Override
    public GamePacketRegistry.Storage<ServerGamePacket> getStorage() {
        return GamePacketRegistry.serverStorage();
    }

    public static Handler<ServerGamePacket, SPacketGameUpdate> makeHandler() {
        return new Handler<>(SPacketGameUpdate::new, (context, lootGame, packet) -> lootGame.net().onUpdatePacket(packet));
    }
}
