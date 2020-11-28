package ru.timeconqueror.lootgames.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import ru.timeconqueror.timecore.api.util.Requirements;

//FIXME move to timecore
public class DrawHelper {
    /**
     * Draws textured rectangle.
     * <p>
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartsCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0                start x-coordinate. (x of left-top corner)
     * @param y0                start y-coordinate. (y of left-top corner)
     * @param width             represents both coordinate and texture width along the axis X. For texture it means texture width in parts.
     * @param height            represents both coordinate and texture width along the axis Y. For texture it means texture height in parts.
     * @param zLevel            z-coordinate.
     * @param textureX          index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY          index of start subtexture part on axis Y (y of left-top texture corner).
     * @param texturePartsCount in how much parts texture must be divided in both axis. Part description is mentioned above.
     */
    public static void drawTexturedRectByParts(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float texturePartsCount) {
        drawTexturedRectByParts(vertexBuilder, matrixStack, x0, y0, width, height, zLevel, textureX, textureY, width, height, texturePartsCount);
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartsCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0                start x-coordinate. (x of left-top corner)
     * @param y0                start y-coordinate. (y of left-top corner)
     * @param width             Represents coordinate length along the axis X.
     * @param height            Represents coordinate length along the axis Y.
     * @param zLevel            z-coordinate.
     * @param textureX          index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY          index of start subtexture part on axis Y (y of left-top texture corner).
     * @param textureWidth      subtexture width in parts.
     * @param textureHeight     subtexture height in parts.
     * @param texturePartsCount in how much parts texture must be divided in both axis. Part description is mentioned above.
     */
    public static void drawTexturedRectByParts(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float textureWidth, float textureHeight, float texturePartsCount) {
        float portionFactor = 1 / texturePartsCount;
        drawTexturedRect(vertexBuilder, matrixStack, x0, y0, width, height, zLevel, textureX, textureY, textureWidth, textureHeight, portionFactor);
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartsCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0                  start x-coordinate. (x of left-top corner)
     * @param y0                  start y-coordinate. (y of left-top corner)
     * @param width               Represents coordinate length along the axis X.
     * @param height              Represents coordinate length along the axis Y.
     * @param zLevel              z-coordinate.
     * @param textureX            index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY            index of start subtexture part on axis Y (y of left-top texture corner).
     * @param textureWidth        subtexture width in parts.
     * @param textureHeight       subtexture height in parts.
     * @param textureDivideFactor represents the value equal to 1 / parts. Part count determines in how much parts texture must be divided in both axis. Part description is mentioned above.
     */
    private static void drawTexturedRect(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float textureWidth, float textureHeight, float textureDivideFactor) {
        Matrix4f pose = matrixStack.last().pose();
        vertexBuilder.vertex(pose, x0, y0, zLevel).uv(textureX * textureDivideFactor, textureY * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0, y0 + height, zLevel).uv(textureX * textureDivideFactor, (textureY + textureHeight) * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0 + width, y0 + height, zLevel).uv((textureX + textureWidth) * textureDivideFactor, (textureY + textureHeight) * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0 + width, y0, zLevel).uv((textureX + textureWidth) * textureDivideFactor, textureY * textureDivideFactor).endVertex();
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_COLOR_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartsCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0                start x-coordinate. (x of left-top corner)
     * @param y0                start y-coordinate. (y of left-top corner)
     * @param width             represents both coordinate and texture width along the axis X. For texture it means texture width in parts.
     * @param height            represents both coordinate and texture width along the axis Y. For texture it means texture height in parts.
     * @param zLevel            z-coordinate.
     * @param textureX          index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY          index of start subtexture part on axis Y (y of left-top texture corner).
     * @param texturePartsCount in how much parts texture must be divided in both axis. Part description is mentioned above.
     * @param argbColor         color which will be applied to the texture
     */
    public static void drawTexturedRectByParts(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float texturePartsCount, int argbColor) {
        drawTexturedRectByParts(vertexBuilder, matrixStack, x0, y0, width, height, zLevel, textureX, textureY, width, height, texturePartsCount, argbColor);
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_COLOR_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartsCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0                start x-coordinate. (x of left-top corner)
     * @param y0                start y-coordinate. (y of left-top corner)
     * @param width             Represents coordinate length along the axis X.
     * @param height            Represents coordinate length along the axis Y.
     * @param zLevel            z-coordinate.
     * @param textureX          index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY          index of start subtexture part on axis Y (y of left-top texture corner).
     * @param textureWidth      subtexture width in parts.
     * @param textureHeight     subtexture height in parts.
     * @param texturePartsCount in how much parts texture must be divided in both axis. Part description is mentioned above.
     * @param argbColor         color which will be applied to the texture
     */
    public static void drawTexturedRectByParts(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float textureWidth, float textureHeight, float texturePartsCount, int argbColor) {
        float portionFactor = 1 / texturePartsCount;
        drawTexturedRect(vertexBuilder, matrixStack, x0, y0, width, height, zLevel, textureX, textureY, textureWidth, textureHeight, portionFactor, argbColor);
    }

    /**
     * Draws textured rectangle.
     * <p>
     * Required VertexFormat: {@link DefaultVertexFormats#POSITION_COLOR_TEX}
     * <p>
     * Term, used in parameters:
     * Parts are used to determine the actual size of {@code textureX, textureY, textureWidth, textureHeight} and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as {@code texturePartsCount} it wil be splitted into 4 pieces with width 16.
     * Then if we set {@code textureX} to 1 and {@code textureWidth} to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x0                  start x-coordinate. (x of left-top corner)
     * @param y0                  start y-coordinate. (y of left-top corner)
     * @param width               Represents coordinate length along the axis X.
     * @param height              Represents coordinate length along the axis Y.
     * @param zLevel              z-coordinate.
     * @param textureX            index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY            index of start subtexture part on axis Y (y of left-top texture corner).
     * @param textureWidth        subtexture width in parts.
     * @param textureHeight       subtexture height in parts.
     * @param textureDivideFactor represents the value equal to 1 / parts. Part count determines in how much parts texture must be divided in both axis. Part description is mentioned above.
     * @param argbColor           color which will be applied to the texture
     */
    private static void drawTexturedRect(IVertexBuilder vertexBuilder, MatrixStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float textureWidth, float textureHeight, float textureDivideFactor, int argbColor) {
        Matrix4f pose = matrixStack.last().pose();

        int r = getRed(argbColor);
        int g = getGreen(argbColor);
        int b = getBlue(argbColor);
        int a = getAlpha(argbColor);

        vertexBuilder.vertex(pose, x0, y0, zLevel).color(r, g, b, a).uv(textureX * textureDivideFactor, textureY * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0, y0 + height, zLevel).color(r, g, b, a).uv(textureX * textureDivideFactor, (textureY + textureHeight) * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0 + width, y0 + height, zLevel).color(r, g, b, a).uv((textureX + textureWidth) * textureDivideFactor, (textureY + textureHeight) * textureDivideFactor).endVertex();
        vertexBuilder.vertex(pose, x0 + width, y0, zLevel).color(r, g, b, a).uv((textureX + textureWidth) * textureDivideFactor, textureY * textureDivideFactor).endVertex();
    }

    /**
     * Returns red channel data of the ARGB color.
     */
    public static int getRed(int argb) {
        return argb >> 16 & 0xFF;
    }

    /**
     * Returns green channel data of the ARGB color.
     */
    public static int getGreen(int argb) {
        return argb >> 8 & 0xFF;
    }

    /**
     * Returns blue channel data of the ARGB color.
     */
    public static int getBlue(int argb) {
        return argb & 0xFF;
    }

    /**
     * Returns alpha channel data of the ARGB color.
     */
    public static int getAlpha(int argb) {
        return argb >> 24 & 0xFF;
    }

    /**
     * Returns the opaque version of this color (without alpha)
     */
    public static int opaquefy(int argb) {
        return argb | 0xFF000000;
    }

    /**
     * Returns color with changed alpha
     *
     * @param alpha should be in range from 0 to 255.
     */
    public static int changeAlpha(int argb, int alpha) {
        Requirements.inRangeInclusive(alpha, 0, 255);
        argb &= 0x00FFFFFF;
        return argb | alpha << 24;
    }
}