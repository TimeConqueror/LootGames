package ru.timeconqueror.lootgames.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.util.holder.Late;

import java.io.IOException;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LGShaders {
    public static final Late<ShaderInstance> FULLBRIGHT_TRANSLUCENT = new Late.Insertable<>();

    @SubscribeEvent
    public static void onShaderReload(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), LootGames.rl("rendertype_fullbright_translucent"), DefaultVertexFormat.POSITION_COLOR_TEX), shaderInstance -> set(FULLBRIGHT_TRANSLUCENT, shaderInstance));
    }

    private static <T> void set(Late<T> late, T obj) {
        ((Late.Insertable<T>) late).set(obj);
    }
}
