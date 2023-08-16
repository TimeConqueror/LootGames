package ru.timeconqueror.lootgames.client;

import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.registry.LGDimensions;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LGClientLoader {
    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(LGDimensions.TEST_SITE_DIM_TYPE.location(), new RoomWorldSpecialEffects());
    }
}
