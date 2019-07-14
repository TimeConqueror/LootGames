package ru.timeconqueror.lootgames.client.render.tesr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.tileentity.TileEntityGOLMaster;

import static ru.timeconqueror.lootgames.tileentity.TileEntityGOLMaster.GameStage.NOT_CONSTRUCTED;
import static ru.timeconqueror.lootgames.tileentity.TileEntityGOLMaster.GameStage.UNDER_EXPANDING;
import static ru.timeconqueror.lootgames.tileentity.TileEntityGOLMaster.MAX_TICKS_EXPANDING;

public class TESRGOLMaster extends TileEntitySpecialRenderer<TileEntityGOLMaster> {
    private static final ResourceLocation GAME_FIELD = new ResourceLocation(LootGames.MODID, "textures/blocks/gameoflight/game_field.png");

    public void drawField(TileEntityGOLMaster te, double x, double y, double z, int ticks, float partialTicks) {
        boolean isExpanding = false;

        if (te.getGameStage() == UNDER_EXPANDING) {
            isExpanding = true;
        }

        GlStateManager.pushMatrix();

        GlStateManager.translate(x + 0.5F, y + 1f, z + 0.5F);

        GlStateManager.disableLighting();

        GlStateManager.rotate(90, 1, 0, 0);

        float f = 0.020833334f; // 1/48
        GlStateManager.scale(f, f, f);

        float length = !isExpanding || ticks >= MAX_TICKS_EXPANDING ? 144F : 48F + (96F * (ticks + partialTicks)) / MAX_TICKS_EXPANDING;

        GlStateManager.translate(-length / 2F, -length / 2F, 0);

        float textureStart = !isExpanding || ticks >= MAX_TICKS_EXPANDING ? 0F : 16F - 16F * (ticks + partialTicks) / MAX_TICKS_EXPANDING;
        float textureWidth = !isExpanding || ticks >= MAX_TICKS_EXPANDING ? 48F : 32F + 16F * (ticks + partialTicks) / MAX_TICKS_EXPANDING - textureStart;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0, 0, -0.01).tex((textureStart) * f, (textureStart + 0) * f).endVertex();
        bufferbuilder.pos(0, length, -0.01).tex((textureStart) * f, (textureStart + textureWidth) * f).endVertex();
        bufferbuilder.pos(length, length, -0.01).tex((textureStart + textureWidth) * f, (textureStart + textureWidth) * f).endVertex();
        bufferbuilder.pos(length, 0, -0.01).tex((textureStart + textureWidth) * f, (textureStart + 0) * f).endVertex();
        tessellator.draw();

        GlStateManager.color(1, 1, 1);
        GlStateManager.popMatrix();
    }

    @Override
    public void render(TileEntityGOLMaster te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.getGameStage() == NOT_CONSTRUCTED) {
            return;
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(GAME_FIELD);
        drawField(te, x, y, z, te.getTicks(), partialTicks);
    }
}
