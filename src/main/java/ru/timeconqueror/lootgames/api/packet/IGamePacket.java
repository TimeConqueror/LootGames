package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.network.PacketBuffer;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

public interface IGamePacket<T extends LootGame> {
    void encode(PacketBuffer bufferTo);

    void decode(PacketBuffer bufferFrom);

    void runOnReceptionSide(T game);
}
