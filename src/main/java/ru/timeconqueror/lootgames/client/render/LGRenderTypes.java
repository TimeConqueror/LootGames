package ru.timeconqueror.lootgames.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.util.client.RenderHelper;

public class LGRenderTypes extends RenderType {
    private LGRenderTypes(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType brightened(ResourceLocation texture) {
        return RenderHelper.rtTextured(LootGames.rl("brightened"), VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, texture, b -> {
            b.setShaderState(RenderStateShard.POSITION_TEX_SHADER);
        });
    }

    public static RenderType brightenedTranslucent(ResourceLocation texture) {
        return RenderHelper.rtTextured(LootGames.rl("brightened_translucent"),
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_COLOR_TEX,
                texture,
                builder -> builder/*.setAlphaState(RenderStateShard.DEFAULT_ALPHA) fixme*/
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setShaderState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER)
        );
    }

    public static RenderType brightenedCutout(ResourceLocation texture) {
        return RenderHelper.rtTextured(
                LootGames.rl("brightened_cutout"),
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX,
                texture,
                state -> {
                    state.setShaderState(RenderStateShard.POSITION_TEX_SHADER);
                } /*fixme state.setAlphaState(MIDWAY_ALPHA)*/
        );
    }
}