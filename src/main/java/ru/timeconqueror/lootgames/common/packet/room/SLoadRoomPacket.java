package ru.timeconqueror.lootgames.common.packet.room;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.room.GameSerializer;
import ru.timeconqueror.lootgames.room.ServerRoom;
import ru.timeconqueror.lootgames.room.client.ClientRoom;
import ru.timeconqueror.timecore.api.common.packet.IPacket;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;

public class SLoadRoomPacket implements IPacket {
    public RoomCoords roomCoords;
    public CompoundTag gameTag;

    public SLoadRoomPacket(ServerRoom room) {
        this.roomCoords = room.getCoords();
        this.gameTag = GameSerializer.serialize(room.getGame(), SerializationType.SYNC);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        roomCoords.write(buf);
        buf.writeNbt(gameTag);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        roomCoords = RoomCoords.read(buf);
        gameTag = buf.readNbt();
    }

    @Override
    public void handleOnClient(NetworkEvent.Context ctx) {
        ClientRoom.handleLoadRoomPacket(this);
    }
}
