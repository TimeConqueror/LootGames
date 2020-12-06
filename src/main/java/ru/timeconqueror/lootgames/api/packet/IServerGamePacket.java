package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.network.PacketBuffer;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

import java.io.IOException;

public interface IServerGamePacket {
    void encode(PacketBuffer bufferTo) throws IOException;

    void decode(PacketBuffer bufferFrom) throws IOException;

    <T extends LootGame<T>> void runOnClient(LootGame<T> game);
}
