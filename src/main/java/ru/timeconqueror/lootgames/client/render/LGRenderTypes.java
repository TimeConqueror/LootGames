package ru.timeconqueror.lootgames.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.util.client.GLDrawMode;
import ru.timeconqueror.timecore.api.util.client.RenderHelper;

public class LGRenderTypes extends RenderType {
    private LGRenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType brightened(ResourceLocation texture) {
        return RenderHelper.rtTextured(LootGames.rl("brightened"), GLDrawMode.QUADS, DefaultVertexFormats.POSITION_TEX, texture, RenderHelper.emptyTuner());
    }

    public static RenderType brightenedTranslucent(ResourceLocation texture) {
        return RenderHelper.rtTextured(LootGames.rl("brightened_translucent"),
                GLDrawMode.QUADS,
                DefaultVertexFormats.POSITION_TEX,
                texture,
                builder -> builder.setAlphaState(RenderState.DEFAULT_ALPHA)
                        .setTransparencyState(RenderState.TRANSLUCENT_TRANSPARENCY));
    }

    public static RenderType brightenedCutout(ResourceLocation texture) {
        return RenderHelper.rtTextured(
                LootGames.rl("brightened_cutout"),
                GLDrawMode.QUADS,
                DefaultVertexFormats.POSITION_TEX,
                texture,
                state -> state.setAlphaState(MIDWAY_ALPHA)
        );
    }
}