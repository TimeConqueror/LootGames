package ru.timeconqueror.lootgames.common.packet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.GamePacket;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry.Key;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegistry.Storage;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomAccess;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.timecore.api.common.packet.ITimePacketHandler;

import java.io.IOException;
import java.util.function.Supplier;

@NoArgsConstructor
@Log4j2
public abstract class PacketGameUpdate<T extends GamePacket> {
    @Getter
    @Setter
    private T gamePacket;
    @Getter
    @Setter
    private RoomCoords coords;

    public PacketGameUpdate(LootGame<?> game, T gamePacket) {
        this.coords = game.getRoom().getCoords();
        this.gamePacket = gamePacket;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends T> getGamePacketClass() {
        return (Class<? extends T>) gamePacket.getClass();
    }

    public abstract Storage<T> getStorage();

    public static class Handler<T extends GamePacket, P extends PacketGameUpdate<T>> implements ITimePacketHandler<P> {
        private final Supplier<P> packetFactory;
        private final GamePacketHandler<T> gameUpdater;

        public Handler(Supplier<P> packetFactory, GamePacketHandler<T> gameUpdater) {
            this.packetFactory = packetFactory;
            this.gameUpdater = gameUpdater;
        }

        public void encode(P packet, FriendlyByteBuf buffer) throws IOException {
            packet.getCoords().write(buffer);

            Storage<T> storage = packet.getStorage();
            Key info = storage.getKey(packet.getGamePacketClass());

            buffer.writeUtf(info.getModId());
            buffer.writeInt(info.getPacketId());
            packet.getGamePacket().encode(buffer);
        }

        public P decode(FriendlyByteBuf buffer) throws IOException {
            P packet = packetFactory.get();
            RoomCoords coords = RoomCoords.read(buffer);

            Key key = new Key(buffer.readUtf(Short.MAX_VALUE), buffer.readInt());
            Class<? extends T> packetClass = packet.getStorage().getPacketClass(key);

            T gamePacket;
            try {
                gamePacket = packetClass.newInstance();//FIXME
                gamePacket.decode(buffer);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Can't decode received game packet, due to lack of public nullary constructor in " + packetClass, e);
            }

            packet.setCoords(coords);
            packet.setGamePacket(gamePacket);

            return packet;
        }

        @Override
        public void handle(P packet, NetworkEvent.Context ctx) {
            Room loadedRoom = RoomAccess.getLoadedRoom(getWorld(ctx), packet.getCoords());
            if (loadedRoom == null) {
                log.debug("Room is not found on {} ({})", packet.getCoords(), ctx.getDirection().getReceptionSide());
                return;
            }

            LootGame<?> game = loadedRoom.getGame();
            if (game == null) {
                log.debug("Game is not loaded on {} ({})", packet.getCoords(), ctx.getDirection().getReceptionSide());
            }

            gameUpdater.handle(ctx, game, packet.getGamePacket());
        }

        public interface GamePacketHandler<T extends GamePacket> {
            void handle(NetworkEvent.Context ctx, LootGame<?> game, T packet);
        }
    }
}
