package ru.timeconqueror.lootgames.client.render;

import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.api.util.holder.Late;

import java.io.IOException;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LGShaders {
    @SubscribeEvent
    public static void onShaderReload(RegisterShadersEvent event) throws IOException {
    }

    private static <T> void set(Late<T> late, T obj) {
        ((Late.Insertable<T>) late).set(obj);
    }
}
