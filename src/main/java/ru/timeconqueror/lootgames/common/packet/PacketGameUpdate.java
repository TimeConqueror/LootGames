package ru.timeconqueror.lootgames.common.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry.Key;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry.Storage;
import ru.timeconqueror.lootgames.api.packet.IGamePacket;
import ru.timeconqueror.timecore.api.common.packet.ITimePacketHandler;

import java.io.IOException;
import java.util.function.Supplier;

public abstract class PacketGameUpdate<T extends IGamePacket> {
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
        private final GamePacketHandler<T> gameUpdater;

        public Handler(Supplier<P> packetFactory, GamePacketHandler<T> gameUpdater) {
            this.packetFactory = packetFactory;
            this.gameUpdater = gameUpdater;
        }

        public void encode(P packet, FriendlyByteBuf buffer) throws IOException {
            buffer.writeBlockPos(packet.getMasterPos());

            Storage<T> storage = packet.getStorage();
            Key info = storage.getKey(packet.getGamePacketClass());

            buffer.writeUtf(info.getModId());
            buffer.writeInt(info.getPacketId());
            packet.getGamePacket().encode(buffer);
        }

        public P decode(FriendlyByteBuf buffer) throws IOException {
            P packet = packetFactory.get();
            BlockPos masterPos = buffer.readBlockPos();

            Key key = new Key(buffer.readUtf(Short.MAX_VALUE), buffer.readInt());
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
        public void handle(P packet, NetworkEvent.Context ctx) {
            BlockEntity te = getWorld(ctx).getBlockEntity(packet.getMasterPos());
            if (te instanceof GameMasterTile<?> master) {
                gameUpdater.handle(ctx, master.getGame(), packet.getGamePacket());
            } else {
                throw new RuntimeException("Something went wrong. Can't find TileEntityMaster on pos " + packet.getMasterPos() + " for packet " + packet.getGamePacketClass().getName());
            }
        }

        public interface GamePacketHandler<T extends IGamePacket> {
            void handle(NetworkEvent.Context ctx, LootGame<?, ?> game, T packet);
        }
    }
}
