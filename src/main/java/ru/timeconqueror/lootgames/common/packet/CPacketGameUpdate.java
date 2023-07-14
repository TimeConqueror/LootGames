package ru.timeconqueror.lootgames.common.packet;

import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.ClientGamePacket;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry;

public class CPacketGameUpdate extends PacketGameUpdate<ClientGamePacket> {
    public CPacketGameUpdate(LootGame<?> game, ClientGamePacket gamePacket) {
        super(game, gamePacket);
    }

    public CPacketGameUpdate() {
    }

    @Override
    public GamePacketRegistry.Storage<ClientGamePacket> getStorage() {
        return GamePacketRegistry.clientStorage();
    }

    public static Handler<ClientGamePacket, CPacketGameUpdate> makeHandler() {
        return new Handler<>(CPacketGameUpdate::new, (context, lootGame, packet) -> lootGame.net().onFeedbackPacket(context.getSender(), packet));
    }
}
