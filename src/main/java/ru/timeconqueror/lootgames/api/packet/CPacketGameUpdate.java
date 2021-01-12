package ru.timeconqueror.lootgames.api.packet;

import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

public class CPacketGameUpdate extends PacketGameUpdate<IClientGamePacket> {
    public <G extends LootGame<?, G>> CPacketGameUpdate(LootGame<?, G> game, IClientGamePacket gamePacket) {
        super(game, gamePacket);
    }

    public CPacketGameUpdate() {
    }

    @Override
    public GamePacketRegistry.Storage<IClientGamePacket> getStorage() {
        return GamePacketRegistry.clientStorage();
    }

    @Override
    public @NotNull LogicalSide getReceptionSide() {
        return LogicalSide.SERVER;
    }

    public static Handler<IClientGamePacket, CPacketGameUpdate> makeHandler() {
        return new Handler<>(CPacketGameUpdate::new, (context, lootGame, packet) -> lootGame.onFeedbackPacket(context.getSender(), packet));
    }
}
