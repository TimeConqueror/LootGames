package ru.timeconqueror.lootgames.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import ru.timeconqueror.lootgames.LootGames;

public class RoomWorldSpecialEffects extends DimensionSpecialEffects.EndEffects {
    private static final ResourceLocation FRONT = LootGames.rl("textures/environment/room_world_sky_front.png");
    private static final ResourceLocation BACK = LootGames.rl("textures/environment/room_world_sky_back.png");
    private static final ResourceLocation TOP = LootGames.rl("textures/environment/room_world_sky_top.png");
    private static final ResourceLocation BOTTOM = LootGames.rl("textures/environment/room_world_sky_bot.png");
    private static final ResourceLocation LEFT = LootGames.rl("textures/environment/room_world_sky_left.png");
    private static final ResourceLocation RIGHT = LootGames.rl("textures/environment/room_world_sky_right.png");
    private static final Direction[] DIRECTIONS = Direction.values();

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        setupFog.run();

        if (isFoggy) {
            return false;
        }

        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        for (Direction direction : DIRECTIONS) {
            poseStack.pushPose();

            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            setupSide(direction, poseStack);

            Matrix4f matrix4f = poseStack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            bufferbuilder.vertex(matrix4f, -100, -100, -100).uv(0, 0).color(5, 5, 5, 255).endVertex();
            bufferbuilder.vertex(matrix4f, -100, -100, 100.0F).uv(0, 1).color(5, 5, 5, 255).endVertex();
            bufferbuilder.vertex(matrix4f, 100.0F, -100, 100.0F).uv(1, 1).color(5, 5, 5, 255).endVertex();
            bufferbuilder.vertex(matrix4f, 100.0F, -100, -100).uv(1, 0).color(5, 5, 5, 255).endVertex();
            tesselator.end();
            poseStack.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        return true;
    }

    private void setupSide(Direction direction, PoseStack poseStack) {
        if (direction == Direction.DOWN) {
            RenderSystem.setShaderTexture(0, BOTTOM);
        } else if (direction == Direction.NORTH) {
            RenderSystem.setShaderTexture(0, FRONT);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        } else if (direction == Direction.SOUTH) {
            RenderSystem.setShaderTexture(0, BACK);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        } else if (direction == Direction.EAST) {
            RenderSystem.setShaderTexture(0, RIGHT);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        } else if (direction == Direction.WEST) {
            RenderSystem.setShaderTexture(0, LEFT);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        } else if (direction == Direction.UP) {
            RenderSystem.setShaderTexture(0, TOP);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        }
    }
}
