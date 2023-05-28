package ru.timeconqueror.lootgames.registry;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.api.room.RoomUtils;
import ru.timeconqueror.lootgames.room.Room;
import ru.timeconqueror.lootgames.room.RoomStorage;
import ru.timeconqueror.timecore.api.CapabilityManagerAPI;
import ru.timeconqueror.timecore.api.registry.CapabilityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LGCapabilities {
    @AutoRegistrable
    private static final CapabilityRegister REGISTER = new CapabilityRegister(LootGames.MODID);

    public static final Capability<RoomStorage> ROOM_STORAGE = REGISTER.register(RoomStorage.class);
    public static final Capability<Room> ROOM = REGISTER.register(Room.class);

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> CapabilityManagerAPI.registerStaticCoffeeAttacher(CapabilityOwner.LEVEL, ROOM_STORAGE, level -> !level.isClientSide, level -> new RoomStorage(((ServerLevel) level))));
        event.enqueueWork(() -> CapabilityManagerAPI.registerStaticCoffeeAttacher(CapabilityOwner.CHUNK, ROOM, chunk -> !chunk.getLevel().isClientSide && RoomUtils.isRoomHolder(chunk.getPos()), chunk -> new Room((ServerLevel) chunk.getLevel(), RoomCoords.of(chunk))));
    }
}
