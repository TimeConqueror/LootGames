package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;

public interface IGamePacket {
    void encode(FriendlyByteBuf bufferTo) throws IOException;

    void decode(FriendlyByteBuf bufferFrom) throws IOException;
}
