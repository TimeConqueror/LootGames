package ru.timeconqueror.lootgames.common.packet;

import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;

public class SPacketGameUpdate extends PacketGameUpdate<IServerGamePacket> {
    public SPacketGameUpdate(LootGame<?> game, IServerGamePacket gamePacket) {
        super(game, gamePacket);
    }

    public SPacketGameUpdate() {
    }

    @Override
    public GamePacketRegistry.Storage<IServerGamePacket> getStorage() {
        return GamePacketRegistry.serverStorage();
    }

    public static Handler<IServerGamePacket, SPacketGameUpdate> makeHandler() {
        return new Handler<>(SPacketGameUpdate::new, (context, lootGame, packet) -> lootGame.net().onUpdatePacket(packet));
    }
}
