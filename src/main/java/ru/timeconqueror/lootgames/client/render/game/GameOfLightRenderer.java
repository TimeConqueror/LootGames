package ru.timeconqueror.lootgames.client.render.game;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2ic;
import ru.timeconqueror.lootgames.DrawHelper;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.GameProgress;
import ru.timeconqueror.lootgames.client.render.LGRenderTypes;
import ru.timeconqueror.lootgames.minigame.gol.DisplayedSymbol;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.minigame.gol.Symbol;

import java.util.ArrayList;
import java.util.List;

public class GameOfLightRenderer {
    private static final RenderType RT_BOARD = LGRenderTypes.solidWithCustomTexture(LootGames.rl("textures/game/game_of_light_board.png"));
    private static final RenderType RT_BOARD_ACTIVE = LGRenderTypes.solidWithCustomTexture(LootGames.rl("textures/game/game_of_light_board_active.png"));
    private static final RenderType RT_EYES = LGRenderTypes.solidWithCustomTexture(LootGames.rl("textures/game/game_of_light_eyes.png"));
    private static final RenderType RT_ADDITIONALS = LGRenderTypes.solidWithCustomTextureTranslucent(LootGames.rl("textures/game/game_of_light_adds.png"));

    private static final DrawHelper.DrawSettings EYE_TILES = new DrawHelper.DrawSettings().tileCountX(12).tileLengthX(16);

    public void render(GameOfLight game, PoseStack poseStack, @NotNull MultiBufferSource bufferIn, float partialTick) {
        if (game.getProgress() == GameProgress.FINISHED) {
            return;
        }

        poseStack.pushPose();

        Vec3i originOffset = game.getBoardOrigin();
        poseStack.translate(originOffset.getX(), originOffset.getY(), originOffset.getZ());

        poseStack.translate(0, 1, 1.006F);
        drawDecorationBorder(game, poseStack, bufferIn);

        drawBoard(game, poseStack, bufferIn);

        poseStack.popPose();
    }

    private void drawBoard(GameOfLight game, PoseStack poseStack, MultiBufferSource bufferIn) {
        List<DisplayedSymbol> displayedSymbols = game.getDisplayedSymbols();
        List<Symbol> activeSymbols = new ArrayList<>(displayedSymbols.size());

        for (DisplayedSymbol symbol : displayedSymbols) {
            activeSymbols.add(symbol.getSymbol());
        }

        poseStack.translate(0, -3, 0);
        PoseStack.Pose last = poseStack.last();

        int size = game.getBoardSize();
        for (Symbol symbol : Symbol.VALUES) {
            RenderType renderType = activeSymbols.contains(symbol) ? RT_BOARD_ACTIVE : RT_BOARD;
            VertexConsumer buffer = bufferIn.getBuffer(renderType);

            Vector2ic pos = symbol.getPos();
            drawFace(buffer, last, pos.x(), size - 1 - pos.y(), 3);
        }
        drawFace(bufferIn.getBuffer(RT_BOARD), last, 1, 1, 3);

        int eyeIndex = game.getClientEyeState().getIndex();
        drawEye(bufferIn.getBuffer(RT_EYES), last, eyeIndex);

        poseStack.translate(0, 3, 0);
    }

    private void drawDecorationBorder(GameOfLight game, PoseStack matrix, MultiBufferSource bufferIn) {
        VertexConsumer buffer = bufferIn.getBuffer(RT_ADDITIONALS);
        PoseStack.Pose last = matrix.last();

        int tileCount = 5;
        int size = game.getBoardSize();
        matrix.translate(-1, -(size + 1), 0);
        drawFace(buffer, last, 0, 0, tileCount);
        drawFace(buffer, last, 1, 0, tileCount);
        drawFace(buffer, last, 2, 0, tileCount);
        drawFace(buffer, last, 3, 0, tileCount);
        drawFace(buffer, last, 4, 0, tileCount);

        drawFace(buffer, last, 0, 1, tileCount);
        drawFace(buffer, last, 4, 1, tileCount);

        drawFace(buffer, last, 0, 2, tileCount);
        drawFace(buffer, last, 4, 2, tileCount);

        drawFace(buffer, last, 0, 3, tileCount);
        drawFace(buffer, last, 4, 3, tileCount);

        drawFace(buffer, last, 0, 4, tileCount);
        drawFace(buffer, last, 1, 4, tileCount);
        drawFace(buffer, last, 2, 4, tileCount);
        drawFace(buffer, last, 3, 4, tileCount);
        drawFace(buffer, last, 4, 4, tileCount);
        matrix.translate(1, size + 1, 0);
    }

    private void drawEye(VertexConsumer buffer, PoseStack.Pose currentPose, int index) {
        DrawHelper.drawTile(currentPose, EYE_TILES, index, index, (pose, normal, x, y, u1, v1, u2,v2) -> {
            x = 1 - 3/16F;
            y = 1 - 3/16F;
            float step = 1 + 6/16F;
            buffer.vertex(pose, x, y, 0.005F)
                    .color(1F, 1F, 1F, 1F)
                    .uv(u1, v2)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(normal, 0, 0, 1)
                    .endVertex();
            buffer.vertex(pose, x + step, y, 0.005F)
                    .color(1F, 1F, 1F, 1F)
                    .uv(u2, v2)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(normal, 0, 0, 1)
                    .endVertex();
            buffer.vertex(pose, x + step, y + step, 0.005F)
                    .color(1F, 1F, 1F, 1F)
                    .uv(u2, v1)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(normal, 0, 0, 1)
                    .endVertex();
            buffer.vertex(pose, x, y + step, 0.005F)
                    .color(1F, 1F, 1F, 1F)
                    .uv(u1, v1)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(normal, 0, 0, 1)
                    .endVertex();
        });
    }

    private void drawFace(VertexConsumer buffer, PoseStack.Pose pose, int x, int y, int tileCount) {
        drawFace(buffer, pose, x, y, x, (tileCount - 1 - y), tileCount);
    }

    private void drawFace(VertexConsumer buffer, PoseStack.Pose matrices, int x, int y, int u1In, int v1In, int tileCount) {
        Matrix4f pose = matrices.pose();
        Matrix3f normal = matrices.normal();

        float u1 = u1In / (float) tileCount;
        float v1 = v1In / (float) tileCount;

        buffer.vertex(pose, x, y, 0)
                .color(1F, 1F, 1F, 1F)
                .uv(u1, v1 + 1 / (float) tileCount)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0, 1, 0)
                .endVertex();
        buffer.vertex(pose, x + 1, y, 0)
                .color(1F, 1F, 1F, 1F)
                .uv(u1 + 1 / (float) tileCount, v1 + 1 / (float) tileCount)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0, 1, 0)
                .endVertex();
        buffer.vertex(pose, x + 1, y + 1, 0)
                .color(1F, 1F, 1F, 1F)
                .uv(u1 + 1 / (float) tileCount, v1)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0, 1, 0)
                .endVertex();
        buffer.vertex(pose, x, y + 1, 0)
                .color(1F, 1F, 1F, 1F)
                .uv(u1, v1)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0, 1, 0)
                .endVertex();
    }
}
