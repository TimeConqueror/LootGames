package ru.timeconqueror.lootgames.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.util.client.RenderHelper;

public class LGRenderTypes extends RenderType {
    protected static final RenderStateShard.ShaderStateShard FULLBRIGHT_TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(LGShaders.FULLBRIGHT_TRANSLUCENT);

    private LGRenderTypes(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType fullbright(ResourceLocation texture) {
        return RenderHelper.rtTextured(LootGames.rl("fullbright"), VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, texture, b -> {
            b.setShaderState(RenderStateShard.POSITION_TEX_SHADER);
        });
    }

    public static RenderType fullbrightTranslucent(ResourceLocation texture) {
        return RenderHelper.rtTextured(LootGames.rl("fullbright_translucent"),
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_COLOR_TEX,
                texture,
                builder -> builder
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setShaderState(FULLBRIGHT_TRANSLUCENT_SHADER)
        );
    }

    public static RenderType fullbrightCutout(ResourceLocation texture) {
        return RenderHelper.rtTextured(
                LootGames.rl("fullbright_cutout"),
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX,
                texture,
                state -> {
                    state.setShaderState(RenderStateShard.POSITION_TEX_SHADER);
                } /*fixme state.setAlphaState(MIDWAY_ALPHA)*/
        );
    }
}