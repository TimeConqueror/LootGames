package ru.timeconqueror.lootgames.common.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.util.ThreeConsumer;
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry.Key;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry.Storage;
import ru.timeconqueror.lootgames.api.packet.IGamePacket;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;

import java.io.IOException;
import java.util.function.Supplier;

public abstract class PacketGameUpdate<T extends IGamePacket> implements ITimePacket {
    private T gamePacket;
    private BlockPos masterPos;

    public <G extends LootGame<?, G>> PacketGameUpdate(LootGame<?, G> game, T gamePacket) {
        this.masterPos = game.getMasterPos();
        this.gamePacket = gamePacket;
    }

    protected PacketGameUpdate() {

    }

    public BlockPos getMasterPos() {
        return masterPos;
    }

    public T getGamePacket() {
        return gamePacket;
    }

    protected void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
    }

    protected void setGamePacket(T gamePacket) {
        this.gamePacket = gamePacket;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends T> getGamePacketClass() {
        return (Class<? extends T>) gamePacket.getClass();
    }

    public abstract Storage<T> getStorage();

    public static class Handler<T extends IGamePacket, P extends PacketGameUpdate<T>> implements ITimePacketHandler<P> {
        private final Supplier<P> packetFactory;
        private final ThreeConsumer<NetworkEvent.Context, LootGame<?, ?>, T> gameUpdater;

        public Handler(Supplier<P> packetFactory, ThreeConsumer<NetworkEvent.Context, LootGame<?, ?>, T> gameUpdater) {
            this.packetFactory = packetFactory;
            this.gameUpdater = gameUpdater;
        }

        public void encode(P packet, PacketBuffer buffer) throws IOException {
            buffer.writeBlockPos(packet.getMasterPos());

            Storage<T> storage = packet.getStorage();
            Key info = storage.getKey(packet.getGamePacketClass());

            buffer.writeUtf(info.getModId());
            buffer.writeInt(info.getPacketId());
            packet.getGamePacket().encode(buffer);
        }

        public P decode(PacketBuffer buffer) throws IOException {
            P packet = packetFactory.get();
            BlockPos masterPos = buffer.readBlockPos();

            Key key = new Key(buffer.readUtf(), buffer.readInt());
            Class<? extends T> packetClass = packet.getStorage().getPacketClass(key);

            T gamePacket;
            try {
                gamePacket = packetClass.newInstance();
                gamePacket.decode(buffer);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Can't decode received game packet, due to lack of public nullary constructor in " + packetClass, e);
            }

            packet.setMasterPos(masterPos);
            packet.setGamePacket(gamePacket);

            return packet;
        }

        @Override
        public boolean handle(P packet, NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                TileEntity te = getWorld(ctx).getBlockEntity(packet.getMasterPos());
                if (te instanceof GameMasterTile<?>) {
                    GameMasterTile<?> master = ((GameMasterTile<?>) te);
                    gameUpdater.accept(ctx, master.getGame(), packet.getGamePacket());
                } else {
                    throw new RuntimeException("Something went wrong. Can't find TileEntityMaster on pos " + packet.getMasterPos() + " for packet " + packet.getGamePacketClass().getName());
                }
            });
            return true;
        }
    }
}
