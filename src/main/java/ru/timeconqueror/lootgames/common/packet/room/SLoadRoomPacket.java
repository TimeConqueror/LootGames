package ru.timeconqueror.lootgames.common.packet.room;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.lootgames.api.room.GameProgress;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.room.GameSerializer;
import ru.timeconqueror.lootgames.room.ServerRoom;
import ru.timeconqueror.lootgames.room.client.ClientRoom;
import ru.timeconqueror.timecore.api.common.packet.IPacket;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;

public class SLoadRoomPacket implements IPacket {
    public RoomCoords roomCoords;
    public CompoundTag gameTag;
    public GameProgress progress;

    public SLoadRoomPacket(ServerRoom room) {
        this.roomCoords = room.getCoords();
        this.gameTag = GameSerializer.serialize(room.getGame(), SerializationType.SYNC);
        this.progress = room.getProgress();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        roomCoords.write(buf);
        buf.writeVarInt(progress.ordinal());
        buf.writeNbt(gameTag);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        roomCoords = RoomCoords.read(buf);
        progress = GameProgress.VALUES[buf.readVarInt() % GameProgress.VALUES.length];
        gameTag = buf.readNbt();
    }

    @Override
    public void handleOnClient(NetworkEvent.Context ctx) {
        ClientRoom.handleLoadRoomPacket(this);
    }
}
