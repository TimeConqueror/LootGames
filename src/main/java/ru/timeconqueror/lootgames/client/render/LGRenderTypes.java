package ru.timeconqueror.lootgames.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.timeconqueror.lootgames.LootGames;

public class LGRenderTypes extends RenderType {
    private LGRenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType brightened(ResourceLocation location) {
        return RenderType.create(LootGames.MODID + ":brightened",
                DefaultVertexFormats.POSITION_TEX,
                GL11.GL_QUADS,
                256,
                false,
                false,
                RenderType.State.builder()
                        .setTextureState(new TextureState(location, false/*blur*/, false/*mipmap*/))
                        .createCompositeState(false));
    }

    public static RenderType brightenedTranslucent(ResourceLocation location) {
        return RenderType.create(LootGames.MODID + ":brightened_translucent",
                DefaultVertexFormats.POSITION_COLOR_TEX,
                GL11.GL_QUADS,
                256,
                false,
                false,
                RenderType.State.builder()
                        .setTextureState(new TextureState(location, false/*blur*/, false/*mipmap*/))
                        .setAlphaState(RenderState.DEFAULT_ALPHA)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false));
    }
}