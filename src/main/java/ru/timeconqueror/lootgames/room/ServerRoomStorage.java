package ru.timeconqueror.lootgames.room;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.room.PlayerRoomData;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.api.room.RoomStorage;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.common.packet.room.SLeaveRoomPacket;
import ru.timeconqueror.lootgames.common.packet.room.SLoadRoomPacket;
import ru.timeconqueror.lootgames.registry.LGCapabilities;
import ru.timeconqueror.timecore.common.capability.CoffeeCapabilityInstance;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerCodec;
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ru.timeconqueror.lootgames.api.Markers.ROOM;

@Log4j2
public class ServerRoomStorage extends CoffeeCapabilityInstance<Level> implements RoomStorage {
    private static final TicketType<ChunkPos> ROOM_CONTAINER_TICKET =
            TicketType.create(LootGames.rl("room_container").toString(), Comparator.comparingLong(ChunkPos::toLong));
    private final CoffeeProperty<Integer> freeIndex = prop("free_index", 0);
    private final ServerLevel roomLevel;
    private int tickCount;
    private final Map<RoomCoords, ServerRoom> loadedRooms = new HashMap<>();
    @Getter
    private final DelayedPlayerTaskHelper playerTaskHelper = new DelayedPlayerTaskHelper();

    public ServerRoomStorage(ServerLevel roomLevel) {
        this.roomLevel = roomLevel;
    }

    @Override
    public int reserveFreeIndex() {
        int i = freeIndex.get();
        freeIndex.set(i + 1);

        return i;
    }

    private ServerRoom getRoomCap(RoomCoords coords) {
        ChunkPos cp = coords.containerPos();
        LevelChunk chunk = roomLevel.getChunk(cp.x, cp.z);
        LazyOptional<ServerRoom> lazy = chunk.getCapability(LGCapabilities.ROOM);
        return lazy.orElseThrow(IllegalStateException::new);
    }

    public ServerRoom getRoom(RoomCoords coords) {
        if (!loadedRooms.containsKey(coords)) {
            loadRoom(coords);
        }

        return getLoadedRoom(coords);
    }

    @Nullable
    public ServerRoom getLoadedRoom(RoomCoords coords) {
        return loadedRooms.get(coords);
    }

    private void loadRoom(RoomCoords coords) {
        ServerRoom room = getRoomCap(coords);
        loadedRooms.put(coords, room);

        ChunkPos containerPos = coords.containerPos();
        log.debug("Room {} has enabled chunkloading...", coords);
        roomLevel.getChunkSource().addRegionTicket(ROOM_CONTAINER_TICKET, containerPos, 0, containerPos);
    }

    private void removeChunkLoading(Room room) {
        ChunkPos containerPos = room.getCoords().containerPos();
        log.debug("Room {} has disabled chunkloading...", room.getCoords());
        roomLevel.getChunkSource().removeRegionTicket(ROOM_CONTAINER_TICKET, containerPos, 0, containerPos);
    }

    public void enterRoom(ServerPlayer player, ServerRoom room) {
        PlayerRoomData.of(player).ifPresent(data -> data.setLastAllowedCoords(room.getCoords()));

        LGNetwork.sendToPlayer(player, new SLoadRoomPacket(room));
        if (room.getGame() != null) {
            room.syncGame(player);
        }
        log.debug(ROOM, "{} is entering the room ({}).", player.getName().getString(), room.getCoords());
    }

    public void leaveRoom(ServerPlayer player, RoomCoords roomCoords) {
        log.debug(ROOM, "{} is leaving the room ({}).", player.getName().getString(), roomCoords);
        LGNetwork.sendToPlayer(player, new SLeaveRoomPacket());
    }

    public boolean teleportToRoom(ServerPlayer player, RoomCoords coords) {
        ServerRoom room = getRoom(coords);
        if (!room.isPendingToEnter(player)) {
            return false;
        }

        Vec3 center = room.getRoomBox().getCenter();

        player.teleportTo(roomLevel, center.x, 1, center.z, 0, 0);
        return true;
    }

    public void teleportAway(ServerPlayer player, ServerRoom room) {
        //TODO handle room spawn
        teleportAway(player);
//        player.changeDimension(ServerLifecycleHooks.getCurrentServer().overworld());
        //LOGGER.debug(ROOM, "{} has been teleported to room entrance '()'.", player.getName(), );
        //LOGGER.debug(ROOM, "{} has been teleported to his spawnpoint.", player.getName());
        //FIXME teleport to last visited room ENTER or outside
    }

    public void teleportAway(ServerPlayer player) {
        ServerLevel respLevel = player.server.getLevel(player.getRespawnDimension());
        if (respLevel == null) {
            respLevel = player.server.overworld();
        }

        BlockPos respawnPos = player.getRespawnPosition();
        float respawnAngle = player.getRespawnAngle();
        if (respawnPos == null) {
            respawnPos = respLevel.getSharedSpawnPos();
            respawnAngle = respLevel.getSharedSpawnAngle();
        }

        player.teleportTo(respLevel, respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), respawnAngle, 0);
    }

//    @Override
//    public void teleportPlayer(RoomCoords coords) {
//        if(!loadedRooms.containsKey(coords)) {
//
//        }
//
//        Room room = loadedRooms.get(coords);
//    }

    public void tick() {
        if (tickCount % 20 == 0) {
            loadedRooms.values().removeIf(room -> {
                ChunkPos chunkPos = room.getCoords().containerPos();
                var chunk = roomLevel.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.EMPTY, false);
                if (chunk == null || room.getPlayers().isEmpty()) {
                    removeChunkLoading(room);
                    return true;
                }

                return false;
            });
        }

        for (ServerRoom room : loadedRooms.values()) {
            room.tick();
        }

        tickCount++;

        playerTaskHelper.tick();
    }

    @NotNull
    @Override
    public Capability<? extends CoffeeCapabilityInstance<Level>> getCapability() {
        return LGCapabilities.ROOM_STORAGE;
    }

    @NotNull
    @Override
    public CapabilityOwnerCodec<Level> getOwnerSerializer() {
        return CapabilityOwner.LEVEL.getSerializer();
    }

    @Override
    public void sendChangesToClient(@NotNull SimpleChannel simpleChannel, @NotNull Object o) {

    }

    public static Optional<ServerRoomStorage> getInstance(Level level) {
        if (level.isClientSide) {
            return Optional.empty();
        }

        return level.getCapability(LGCapabilities.ROOM_STORAGE).resolve();
    }
}
