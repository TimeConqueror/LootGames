package ru.timeconqueror.lootgames.common.packet.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.lootgames.api.room.RoomProgress;
import ru.timeconqueror.lootgames.room.client.ClientRoom;
import ru.timeconqueror.timecore.api.common.packet.IPacket;

@Getter
@Log4j2
@AllArgsConstructor
public class SUpdateRoomPacket implements IPacket {
    private RoomProgress progress;

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(progress.ordinal());
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        progress = RoomProgress.values()[buf.readVarInt()];
    }

    @Override
    public void handleOnClient(NetworkEvent.Context ctx) {
        ClientRoom instance = ClientRoom.getInstance();
        if(instance != null) {
            instance.handleUpdateRoomPacket(this);
        } else {
            log.debug("Update packet received for an empty room!");
        }
    }
}
