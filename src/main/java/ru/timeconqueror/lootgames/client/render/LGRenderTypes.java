package ru.timeconqueror.lootgames.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.util.client.RenderHelper;

import java.util.function.Function;

public class LGRenderTypes extends RenderType {
    private LGRenderTypes(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType fullbright(ResourceLocation texture) {
        return RenderHelper.rtTextured(LootGames.rl("fullbright"), VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX, texture, b -> b.setShaderState(RenderStateShard.POSITION_TEX_SHADER));
    }

    public static RenderType fullbrightTranslucent(ResourceLocation texture) {
        return RenderHelper.rtTextured(LootGames.rl("fullbright_translucent"),
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX,
                texture,
                b -> b.setShaderState(RenderStateShard.POSITION_TEX_SHADER)
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
        );
    }

    private static final Function<ResourceLocation, RenderType> SOLID_WITH_CUSTOM_TEXTURE = Util.memoize((tex) -> {
        RenderStateShard.TextureStateShard texShard = new RenderStateShard.TextureStateShard(tex, false, true);
        return create(LootGames.rl("solid_custom_tex").toString(),
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS,
                256,
                true,
                false,
                CompositeState.builder().setLightmapState(LIGHTMAP)
                        .setShaderState(RENDERTYPE_SOLID_SHADER)
                        .setTextureState(texShard)
                        .createCompositeState(true));
    });

    public static RenderType solidWithCustomTexture(ResourceLocation texture) {
        return SOLID_WITH_CUSTOM_TEXTURE.apply(texture);
    }
}