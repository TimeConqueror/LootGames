package ru.timeconqueror.lootgames.api.util.client;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class RenderUtils {
    public static void drawRect(double x0, double y0, double widthPosX, double heightPosX, double zLevel, double textureX, double textureY, double textureWidth, double textureHeight, double texturePortionFactor) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x0, y0, zLevel).tex(textureX * texturePortionFactor, textureY * texturePortionFactor).endVertex();
        bufferbuilder.pos(x0, heightPosX, zLevel).tex(textureX * texturePortionFactor, (textureY + textureHeight) * texturePortionFactor).endVertex();
        bufferbuilder.pos(widthPosX, heightPosX, zLevel).tex((textureX + textureWidth) * texturePortionFactor, (textureY + textureHeight) * texturePortionFactor).endVertex();
        bufferbuilder.pos(widthPosX, y0, zLevel).tex((textureX + textureWidth) * texturePortionFactor, textureY * texturePortionFactor).endVertex();
        tessellator.draw();
    }
}
