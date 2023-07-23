package ru.timeconqueror.lootgames.registry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.room.PlayerData;
import ru.timeconqueror.lootgames.room.RoomUtils;
import ru.timeconqueror.lootgames.room.ServerRoom;
import ru.timeconqueror.lootgames.room.ServerRoomStorage;
import ru.timeconqueror.timecore.api.CapabilityManagerAPI;
import ru.timeconqueror.timecore.api.registry.CapabilityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LGCapabilities {
    @AutoRegistrable
    private static final CapabilityRegister REGISTER = new CapabilityRegister(LootGames.MODID);

    public static final Capability<ServerRoomStorage> ROOM_STORAGE = REGISTER.register(ServerRoomStorage.class);
    public static final Capability<ServerRoom> ROOM = REGISTER.register(ServerRoom.class);
    public static final Capability<PlayerData> PLAYER_DATA = REGISTER.register(PlayerData.class);

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> CapabilityManagerAPI.registerStaticCoffeeAttacher(CapabilityOwner.LEVEL, ROOM_STORAGE, level -> !level.isClientSide && RoomUtils.inRoomWorld(level), level -> new ServerRoomStorage(((ServerLevel) level))));
        event.enqueueWork(() -> CapabilityManagerAPI.registerStaticCoffeeAttacher(CapabilityOwner.CHUNK, ROOM, chunk -> !chunk.getLevel().isClientSide && RoomUtils.inRoomWorld(chunk.getLevel()) && RoomUtils.isRoomHolder(chunk.getPos()), chunk -> new ServerRoom((ServerLevel) chunk.getLevel(), RoomCoords.of(chunk))));
        event.enqueueWork(() -> CapabilityManagerAPI.registerStaticCoffeeAttacher(CapabilityOwner.ENTITY, PLAYER_DATA, entity -> entity instanceof ServerPlayer, entity -> new PlayerData((ServerPlayer) entity)));
    }
}
