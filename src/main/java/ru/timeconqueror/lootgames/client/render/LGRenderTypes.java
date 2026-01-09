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

    public static RenderType blackHole(ResourceLocation texture, boolean affectsOutline) {
        //copypaste from entity_translucent, but with changed hasCrumbling to false
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(affectsOutline);
        return RenderType.create("black_hole", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, rendertype$compositestate);
    }

    private static final Function<ResourceLocation, RenderType> SOLID_WITH_CUSTOM_TEXTURE_TRANSLUCENT = Util.memoize((tex) -> {
        RenderStateShard.TextureStateShard texShard = new RenderStateShard.TextureStateShard(tex, false, true);
        return create(LootGames.rl("solid_custom_tex").toString(),
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS,
                256,
                true,
                true,
                CompositeState.builder().setLightmapState(LIGHTMAP)
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setShaderState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER)
                        .setTextureState(texShard)
                        .createCompositeState(true));
    });

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

    public static RenderType solidWithCustomTextureTranslucent(ResourceLocation texture) {
        RenderStateShard.TextureStateShard texShard = new RenderStateShard.TextureStateShard(texture, false, true);
        return create(LootGames.rl("solid_custom_tex_translucent").toString(),
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS,
                256,
                true,
                false,
                CompositeState.builder().setLightmapState(LIGHTMAP)
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setShaderState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER)
                        .setTextureState(texShard)
                        .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
                        .createCompositeState(true));
    }
}