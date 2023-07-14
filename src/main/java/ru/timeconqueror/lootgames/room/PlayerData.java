package ru.timeconqueror.lootgames.room;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.room.PlayerRoomData;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGCapabilities;
import ru.timeconqueror.timecore.common.capability.CoffeeCapabilityInstance;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerCodec;
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty;

public class PlayerData extends CoffeeCapabilityInstance<Entity> implements PlayerRoomData {
    private final ServerPlayer player;

    private final CoffeeProperty<RoomCoords> lastAllowedCoords = prop("last_allowed", (RoomCoords) null, RoomCoords.Serializer.NULLABLE_INSTANCE);

    public PlayerData(ServerPlayer player) {
        this.player = player;
    }

    @NotNull
    @Override
    public Capability<? extends CoffeeCapabilityInstance<Entity>> getCapability() {
        return LGCapabilities.PLAYER_DATA;
    }

    @NotNull
    @Override
    public CapabilityOwnerCodec<Entity> getOwnerSerializer() {
        return CapabilityOwner.ENTITY.getSerializer();
    }

    @Override
    public void sendChangesToClient(@NotNull SimpleChannel simpleChannel, @NotNull Object o) {

    }

    @Override
    public void setLastAllowedCoords(RoomCoords coords) {
        lastAllowedCoords.set(coords);
    }

    @Override
    public @Nullable RoomCoords getLastAllowedCoords() {
        return lastAllowedCoords.get();
    }

    public static LazyOptional<PlayerData> of(ServerPlayer player) {
        return player.getCapability(LGCapabilities.PLAYER_DATA);
    }
}
