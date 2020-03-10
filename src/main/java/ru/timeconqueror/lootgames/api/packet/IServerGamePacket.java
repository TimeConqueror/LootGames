package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.network.PacketBuffer;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

public interface IServerGamePacket<T extends LootGame<T>> {
    void encode(PacketBuffer bufferTo);

    void decode(PacketBuffer bufferFrom);

    void runOnClient(LootGame<T> game);
}
