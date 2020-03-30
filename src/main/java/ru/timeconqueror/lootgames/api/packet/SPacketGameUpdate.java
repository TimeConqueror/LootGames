package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;

import java.util.function.Supplier;
//Todo maybe also addNBT T to packet and handler?
public class SPacketGameUpdate implements ITimePacket {
    private IServerGamePacket<?> gamePacket;
    private BlockPos masterPos;

    public <T extends LootGame<T>> SPacketGameUpdate(LootGame<T> game, IServerGamePacket<T> gamePacket) {
        this.masterPos = game.getMasterPos();
        this.gamePacket = gamePacket;
    }

    private SPacketGameUpdate() {
    }

    @Override
    public LogicalSide getReceptionSide() {
        return LogicalSide.CLIENT;
    }

    public static class Handler implements ITimePacketHandler<SPacketGameUpdate> {
        @Override
        public void encode(SPacketGameUpdate packet, PacketBuffer buffer) {
            buffer.writeBlockPos(packet.masterPos);

            GamePacketRegistry.PacketInfo info = GamePacketRegistry.getInfo((Class<? extends IServerGamePacket<?>>) packet.gamePacket.getClass());
            buffer.writeString(info.getModID());
            buffer.writeInt(info.getPacketID());
            packet.gamePacket.encode(buffer);
        }

        @Override
        public SPacketGameUpdate decode(PacketBuffer buffer) {
            SPacketGameUpdate packet = new SPacketGameUpdate();
            packet.masterPos = buffer.readBlockPos();

            GamePacketRegistry.PacketInfo info = new GamePacketRegistry.PacketInfo(buffer.readString(), buffer.readInt());
            Class<? extends IServerGamePacket<?>> packetClass = GamePacketRegistry.getPacketClass(info);

            try {
                packet.gamePacket = packetClass.newInstance();
                packet.gamePacket.decode(buffer);
            } catch (InstantiationException e) {//FIXME test if it only kicks from server or halt client? and what if server will catch it?
                throw new RuntimeException("Can't decode received game packet, due to lack of public nullary constructor in " + packetClass, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return packet;
        }

        @Override
        public void onPacketReceived(SPacketGameUpdate packet, Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();//FIXME test disabled on server side? do only for client!

            if (packet.gamePacket != null) {
                ctx.enqueueWork(() -> {
                    World world = packet.getWorld(ctx);
                    TileEntity te = world.getTileEntity(packet.masterPos);
                    if (te instanceof TileEntityGameMaster<?>) {
                        TileEntityGameMaster<?> master = ((TileEntityGameMaster<?>) te);
                        master.getGame().onUpdatePacket(packet.gamePacket);

                    } else {
                        throw new RuntimeException("Something went wrong. Can't find TileEntityMaster on pos: " + packet.masterPos);
                        //FIXME test if it only kicks from server or halt client? and what if server will catch it?
                    }
                });
            }
        }
    }
}
