package ru.timeconqueror.lootgames.client.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGDimensions;

import static ru.timeconqueror.lootgames.utils.Log.DEBUGGER;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RoomDebugRenderer {
    private static boolean renderRoomBorders = false;

    public static void switchRenderRoomBorders() {
        renderRoomBorders = !renderRoomBorders;
        DEBUGGER.info("Render Room Borders: {}", renderRoomBorders);
    }

    public static boolean isInRoomWorld(Minecraft mc) {
        return mc.level != null && mc.level.dimension() == LGDimensions.TEST_SITE_DIM;
    }

    public static void render(PoseStack poseStack, MultiBufferSource bufferSource, double camX, double camY, double camZ) {
        Minecraft mc = Minecraft.getInstance();
        if (!renderRoomBorders || !isInRoomWorld(mc)) return;

        int cellBorder = FastColor.ARGB32.color(255, 0, 155, 155);

        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Entity entity = mc.gameRenderer.getMainCamera().getEntity();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        double yMin = (double) mc.level.getMinBuildHeight() - camY;
        double yMax = (double) mc.level.getMaxBuildHeight() - camY;
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RoomCoords coords = RoomCoords.of(entity.chunkPosition().x, entity.chunkPosition().z);
        BlockPos lowestCorner = coords.lowestCorner();
        double minX = (double) lowestCorner.getX() - camX;
        double maxZ = (double) lowestCorner.getZ() - camZ;
        RenderSystem.lineWidth(1.0F);
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        for (int i = -RoomCoords.ROOM_SIZE; i <= RoomCoords.ROOM_SIZE * 2; i += RoomCoords.ROOM_SIZE) {
            for (int j = -RoomCoords.ROOM_SIZE; j <= RoomCoords.ROOM_SIZE * 2; j += RoomCoords.ROOM_SIZE) {
                bufferbuilder.vertex(minX + (double) i, yMin, maxZ + (double) j).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
                bufferbuilder.vertex(minX + (double) i, yMin, maxZ + (double) j).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                bufferbuilder.vertex(minX + (double) i, yMax, maxZ + (double) j).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                bufferbuilder.vertex(minX + (double) i, yMax, maxZ + (double) j).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
            }
        }

        for (int xIndex = 8; xIndex < RoomCoords.ROOM_SIZE; xIndex += 8) {
            bufferbuilder.vertex(minX + (double) xIndex, yMin, maxZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(minX + (double) xIndex, yMin, maxZ).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX + (double) xIndex, yMax, maxZ).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX + (double) xIndex, yMax, maxZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(minX + (double) xIndex, yMin, maxZ + RoomCoords.ROOM_SIZE).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(minX + (double) xIndex, yMin, maxZ + RoomCoords.ROOM_SIZE).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX + (double) xIndex, yMax, maxZ + RoomCoords.ROOM_SIZE).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX + (double) xIndex, yMax, maxZ + RoomCoords.ROOM_SIZE).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }

        for (int zIndex = 8; zIndex < RoomCoords.ROOM_SIZE; zIndex += 8) {
            bufferbuilder.vertex(minX, yMin, maxZ + (double) zIndex).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(minX, yMin, maxZ + (double) zIndex).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX, yMax, maxZ + (double) zIndex).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX, yMax, maxZ + (double) zIndex).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(minX + RoomCoords.ROOM_SIZE, yMin, maxZ + (double) zIndex).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(minX + RoomCoords.ROOM_SIZE, yMin, maxZ + (double) zIndex).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX + RoomCoords.ROOM_SIZE, yMax, maxZ + (double) zIndex).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX + RoomCoords.ROOM_SIZE, yMax, maxZ + (double) zIndex).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }

        for (int yIndex = mc.level.getMinBuildHeight(); yIndex <= mc.level.getMaxBuildHeight(); yIndex += 8) {
            double d4 = (double) yIndex - camY;
            bufferbuilder.vertex(minX, d4, maxZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(minX, d4, maxZ).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX, d4, maxZ + RoomCoords.ROOM_SIZE).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX + RoomCoords.ROOM_SIZE, d4, maxZ + RoomCoords.ROOM_SIZE).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX + RoomCoords.ROOM_SIZE, d4, maxZ).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX, d4, maxZ).color(cellBorder).endVertex();
            bufferbuilder.vertex(minX, d4, maxZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }

        tesselator.end();
        RenderSystem.lineWidth(1.0F);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
    }
}
