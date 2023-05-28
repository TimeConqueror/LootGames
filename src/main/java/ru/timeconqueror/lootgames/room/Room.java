package ru.timeconqueror.lootgames.room;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.room.IRoom;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGCapabilities;
import ru.timeconqueror.timecore.common.capability.CoffeeCapabilityInstance;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerCodec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Room extends CoffeeCapabilityInstance<LevelChunk> implements IRoom {
    private final RoomCoords coords;
    private final AABB roomBox;
    private final ServerLevel level;

    private final Set<UUID> allowedToEnter = new HashSet<>();

    public Room(ServerLevel level, RoomCoords coords) {
        this.level = level;
        this.coords = coords;
        this.roomBox = new AABB(coords.lowestCorner().atY(level.getMinBuildHeight()),
                coords.lowestCorner().offset(RoomCoords.ROOM_SIZE - 1, 0, RoomCoords.ROOM_SIZE - 1).atY(level.getMaxBuildHeight() - 1));
    }

    public void sendCurrentStateToPlayer() {
        //TODO
    }

    @Override
    public RoomCoords getCoords() {
        return coords;
    }

    @Override
    public AABB getRoomBox() {
        return roomBox;
    }

    public List<ServerPlayer> getPlayers() {
        return level.getEntitiesOfClass(ServerPlayer.class, roomBox);
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    @Override
    public void allowEnterDuringTick(ServerPlayer player) {
        allowedToEnter.add(player.getUUID());
    }

    @Override
    public boolean isAllowedToEnter(ServerPlayer player) {
        return allowedToEnter.contains(player.getUUID());
    }

    public void tick() {
        allowedToEnter.clear();
    }

    @NotNull
    @Override
    public Capability<? extends CoffeeCapabilityInstance<LevelChunk>> getCapability() {
        return LGCapabilities.ROOM;
    }

    @NotNull
    @Override
    public CapabilityOwnerCodec<LevelChunk> getOwnerSerializer() {
        return CapabilityOwner.CHUNK.getSerializer();
    }

    @Override
    public void sendChangesToClients(@NotNull SimpleChannel simpleChannel, @NotNull Object o) {

    }

    @NotNull
    @Override
    public CompoundTag serializeNBT() {
        return super.serializeNBT();
    }
}
