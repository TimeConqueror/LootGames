package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.network.PacketBuffer;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

import java.io.IOException;

public interface IServerGamePacket {
    void encode(PacketBuffer bufferTo) throws IOException;

    void decode(PacketBuffer bufferFrom) throws IOException;

    <S extends LootGame.Stage, T extends LootGame<S, T>> void runOnClient(LootGame<S, T> game);
}
