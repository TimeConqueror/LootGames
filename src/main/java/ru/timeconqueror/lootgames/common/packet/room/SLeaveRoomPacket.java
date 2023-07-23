package ru.timeconqueror.lootgames.common.packet.room;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.lootgames.room.client.ClientRoom;
import ru.timeconqueror.timecore.api.common.packet.IPacket;

public class SLeaveRoomPacket implements IPacket {
    @Override
    public void write(FriendlyByteBuf buf) {

    }

    @Override
    public void read(FriendlyByteBuf buf) {

    }

    @Override
    public void handleOnClient(NetworkEvent.Context ctx) {
        ClientRoom.clearInstance();
    }
}
