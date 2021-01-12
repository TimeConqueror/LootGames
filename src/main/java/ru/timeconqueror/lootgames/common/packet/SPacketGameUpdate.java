package ru.timeconqueror.lootgames.common.packet;

import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;

public class SPacketGameUpdate extends PacketGameUpdate<IServerGamePacket> {
    public <G extends LootGame<?, G>> SPacketGameUpdate(LootGame<?, G> game, IServerGamePacket gamePacket) {
        super(game, gamePacket);
    }

    public SPacketGameUpdate() {
    }

    @Override
    public GamePacketRegistry.Storage<IServerGamePacket> getStorage() {
        return GamePacketRegistry.serverStorage();
    }

    @Override
    public @NotNull LogicalSide getReceptionSide() {
        return LogicalSide.CLIENT;
    }

    public static Handler<IServerGamePacket, SPacketGameUpdate> makeHandler() {
        return new Handler<>(SPacketGameUpdate::new, (context, lootGame, packet) -> lootGame.onUpdatePacket(packet));
    }
}
