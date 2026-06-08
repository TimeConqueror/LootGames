package ru.timeconqueror.lootgames.common.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.lootgames.ServerPacketHandler;
import ru.timeconqueror.timecore.api.common.packet.IPacket;

@AllArgsConstructor
public class CPacketCycleBlock implements IPacket {
    @Getter
    private boolean forward;

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(forward);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        forward = buf.readBoolean();
    }

    @Override
    public void handleOnServer(NetworkEvent.Context ctx) {
        ServerPacketHandler.onCycleBlockPacketReceived(this, ctx);
    }
}

