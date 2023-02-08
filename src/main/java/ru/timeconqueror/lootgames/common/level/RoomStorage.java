package ru.timeconqueror.lootgames.common.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.registry.LGCapabilities;
import ru.timeconqueror.lootgames.registry.LGDimensions;
import ru.timeconqueror.timecore.common.capability.CoffeeCapabilityInstance;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerCodec;

public class RoomStorage extends CoffeeCapabilityInstance<Level> {
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
    public void sendChangesToClients(@NotNull SimpleChannel simpleChannel, @NotNull Object o) {

    }

    public static RoomStorage of(ServerLevel level) {
        if (level.dimension() != LGDimensions.TEST_SITE_DIM) {
            throw new IllegalArgumentException("Level should have '" + LGDimensions.TEST_SITE_DIM.location() + "' dimension");
        }

        LazyOptional<RoomStorage> lazy = level.getCapability(LGCapabilities.ROOM_STORAGE);
        return lazy.orElseThrow(IllegalStateException::new);
    }
}
