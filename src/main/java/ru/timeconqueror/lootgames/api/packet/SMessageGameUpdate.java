package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

import java.util.function.Supplier;

public class SMessageGameUpdate implements ITimePacket {
    private IGamePacket<?> gamePacket;
    private BlockPos masterPos;

    public <T extends LootGame> SMessageGameUpdate(T game, IGamePacket<T> gamePacket) {
        this.masterPos = game.getMasterPos();
        this.gamePacket = gamePacket;
    }

    private SMessageGameUpdate() {
    }

    @SuppressWarnings("unchecked")
    public static void encode(SMessageGameUpdate packet, PacketBuffer buffer) {
        buffer.writeBlockPos(packet.masterPos);

        GamePacketRegistry.PacketInfo info = GamePacketRegistry.getInfo((Class<? extends IGamePacket<?>>) packet.gamePacket.getClass());
        buffer.writeString(info.getModID());
        buffer.writeInt(info.getPacketID());
        packet.gamePacket.encode(buffer);
    }

    public static SMessageGameUpdate decode(PacketBuffer buffer) {
        SMessageGameUpdate packet = new SMessageGameUpdate();
        packet.masterPos = buffer.readBlockPos();

        GamePacketRegistry.PacketInfo info = new GamePacketRegistry.PacketInfo(buffer.readString(), buffer.readInt());
        Class<? extends IGamePacket<?>> packetClass = GamePacketRegistry.getPacketClass(info);

        try {
            packet.gamePacket = packetClass.newInstance();
        } catch (InstantiationException e) {//FIXME test if it only kicks from server or halt client? and what if server will catch it?
            throw new RuntimeException("Can't decode received game packet, due to lack of nullary constructor in " + packetClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return packet;
    }

    public static void handle(SMessageGameUpdate packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();//FIXME disable on server side, do only for client!
        ctx.enqueueWork(() -> {
            World world = packet.getWorld(ctx);
            TileEntity te = world.getTileEntity(packet.masterPos);
            if (te instanceof TileEntityGameMaster<?>) {
                TileEntityGameMaster<?> master = ((TileEntityGameMaster<?>) te);
                master.getGame().onUpdatePacket((IGamePacket<LootGame>) packet.gamePacket);

            } else {
                throw new RuntimeException("Something went wrong. Can't find TileEntityMaster on pos: " + packet.masterPos);
                //FIXME test if it only kicks from server or halt client? and what if server will catch it?
            }
        });
    }
}
