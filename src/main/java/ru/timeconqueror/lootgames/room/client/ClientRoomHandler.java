package ru.timeconqueror.lootgames.room.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.room.RoomUtils;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientRoomHandler {

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!RoomUtils.inRoomWorld(event.level)) {
            ClientRoom.clearInstance();
        }

        ClientRoom room = ClientRoom.getInstance();
        if (room != null) {
            room.tick();
        }
    }
}
