package ru.timeconqueror.lootgames.client.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.room.RoomUtils;

import static ru.timeconqueror.lootgames.utils.Log.DEBUGGER;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RoomDebugRenderer {
    private static boolean renderRoomBorders = false;

    public static void switchRenderRoomBorders() {
        renderRoomBorders = !renderRoomBorders;
        DEBUGGER.info("Render Room Borders: {}", renderRoomBorders);
    }

    public static void render(PoseStack poseStack, MultiBufferSource bufferSource, float camX, float camY, float camZ) {
        Minecraft mc = Minecraft.getInstance();
        if (!renderRoomBorders || !RoomUtils.inRoomWorld(mc.level)) return;

        Entity entity = mc.gameRenderer.getMainCamera().getEntity();
        float yMin = (float) mc.level.getMinBuildHeight() - camY;
        float yMax = (float) mc.level.getMaxBuildHeight() - camY;
        RoomCoords coords = RoomCoords.of(entity.chunkPosition().x, entity.chunkPosition().z);
        BlockPos lowestCorner = coords.minPos(mc.level);
        float minX = (float) lowestCorner.getX() - camX;
        float maxZ = (float) lowestCorner.getZ() - camZ;

        VertexConsumer bb = bufferSource.getBuffer(RenderType.debugLineStrip(1.0D));
        Matrix4f matrix = poseStack.last().pose();

        for (int i = -RoomCoords.ROOM_SIZE; i <= RoomCoords.ROOM_SIZE * 2; i += RoomCoords.ROOM_SIZE) {
            for (int j = -RoomCoords.ROOM_SIZE; j <= RoomCoords.ROOM_SIZE * 2; j += RoomCoords.ROOM_SIZE) {
                bb.vertex(matrix, minX + (float) i, yMin, maxZ + (float) j).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
                bb.vertex(matrix, minX + (float) i, yMin, maxZ + (float) j).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                bb.vertex(matrix, minX + (float) i, yMax, maxZ + (float) j).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                bb.vertex(matrix, minX + (float) i, yMax, maxZ + (float) j).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
            }
        }

        int cellBorder = FastColor.ARGB32.color(255, 0, 155, 155);
        for (int xIndex = 8; xIndex < RoomCoords.ROOM_SIZE; xIndex += 8) {
            bb.vertex(matrix, minX + (float) xIndex, yMin, maxZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bb.vertex(matrix, minX + (float) xIndex, yMin, maxZ).color(cellBorder).endVertex();
            bb.vertex(matrix, minX + (float) xIndex, yMax, maxZ).color(cellBorder).endVertex();
            bb.vertex(matrix, minX + (float) xIndex, yMax, maxZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bb.vertex(matrix, minX + (float) xIndex, yMin, maxZ + RoomCoords.ROOM_SIZE).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bb.vertex(matrix, minX + (float) xIndex, yMin, maxZ + RoomCoords.ROOM_SIZE).color(cellBorder).endVertex();
            bb.vertex(matrix, minX + (float) xIndex, yMax, maxZ + RoomCoords.ROOM_SIZE).color(cellBorder).endVertex();
            bb.vertex(matrix, minX + (float) xIndex, yMax, maxZ + RoomCoords.ROOM_SIZE).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }

        for (int zIndex = 8; zIndex < RoomCoords.ROOM_SIZE; zIndex += 8) {
            bb.vertex(matrix, minX, yMin, maxZ + (float) zIndex).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bb.vertex(matrix, minX, yMin, maxZ + (float) zIndex).color(cellBorder).endVertex();
            bb.vertex(matrix, minX, yMax, maxZ + (float) zIndex).color(cellBorder).endVertex();
            bb.vertex(matrix, minX, yMax, maxZ + (float) zIndex).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bb.vertex(matrix, minX + RoomCoords.ROOM_SIZE, yMin, maxZ + (float) zIndex).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bb.vertex(matrix, minX + RoomCoords.ROOM_SIZE, yMin, maxZ + (float) zIndex).color(cellBorder).endVertex();
            bb.vertex(matrix, minX + RoomCoords.ROOM_SIZE, yMax, maxZ + (float) zIndex).color(cellBorder).endVertex();
            bb.vertex(matrix, minX + RoomCoords.ROOM_SIZE, yMax, maxZ + (float) zIndex).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }

        for (int yIndex = mc.level.getMinBuildHeight(); yIndex <= mc.level.getMaxBuildHeight(); yIndex += 8) {
            float d4 = yIndex - camY;
            bb.vertex(matrix, minX, d4, maxZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
            bb.vertex(matrix, minX, d4, maxZ).color(cellBorder).endVertex();
            bb.vertex(matrix, minX, d4, maxZ + RoomCoords.ROOM_SIZE).color(cellBorder).endVertex();
            bb.vertex(matrix, minX + RoomCoords.ROOM_SIZE, d4, maxZ + RoomCoords.ROOM_SIZE).color(cellBorder).endVertex();
            bb.vertex(matrix, minX + RoomCoords.ROOM_SIZE, d4, maxZ).color(cellBorder).endVertex();
            bb.vertex(matrix, minX, d4, maxZ).color(cellBorder).endVertex();
            bb.vertex(matrix, minX, d4, maxZ).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
        }
    }
}
