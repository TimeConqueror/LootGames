package ru.timeconqueror.lootgames.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.util.client.GLDrawMode;
import ru.timeconqueror.timecore.api.util.client.RenderHelper;

public class LGRenderTypes extends RenderType {
    private LGRenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType brightened(ResourceLocation texture) {
        return RenderHelper.rtTextured(LootGames.rl("brightened"), GLDrawMode.QUADS, DefaultVertexFormat.POSITION_TEX, texture, RenderHelper.emptyTuner());
    }

    public static RenderType brightenedTranslucent(ResourceLocation texture) {
        return RenderHelper.rtTextured(LootGames.rl("brightened_translucent"),
                GLDrawMode.QUADS,
                DefaultVertexFormat.POSITION_COLOR_TEX,
                texture,
                builder -> builder.setAlphaState(RenderStateShard.DEFAULT_ALPHA)
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY));
    }

    public static RenderType brightenedCutout(ResourceLocation texture) {
        return RenderHelper.rtTextured(
                LootGames.rl("brightened_cutout"),
                GLDrawMode.QUADS,
                DefaultVertexFormat.POSITION_TEX,
                texture,
                state -> state.setAlphaState(MIDWAY_ALPHA)
        );
    }
}