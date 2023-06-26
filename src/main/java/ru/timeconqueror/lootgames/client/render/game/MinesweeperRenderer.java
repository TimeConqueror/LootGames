package ru.timeconqueror.lootgames.client.render.game;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4i;
import org.joml.Vector4ic;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.Stage;
import ru.timeconqueror.lootgames.client.render.LGRenderTypes;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper.StageDetonating;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper.StageExploding;
import ru.timeconqueror.lootgames.minigame.minesweeper.Mark;
import ru.timeconqueror.lootgames.minigame.minesweeper.Type;
import ru.timeconqueror.timecore.api.util.client.DrawHelper;

import java.util.List;

public class MinesweeperRenderer {
    private static final ResourceLocation MS_BOARD = LootGames.rl("textures/game/ms_board.png");
    private static final RenderType RT_BRIGHTENED_BOARD = LGRenderTypes.fullbright(MS_BOARD);
    private static final RenderType RT_BRIGHTENED_TRANSLUCENT_BOARD = LGRenderTypes.fullbrightTranslucent(MS_BOARD);

    private static final CellMesh HIDDEN_CELL_MESH = new CellMesh(
            List.of(
                    new Vertex(0F, 0F, 0F, 0F, 0F),
                    new Vertex(1F, 0F, 0F, 1F, 0F),
                    new Vertex(1F, 1F, 0F, 1F, 1F),
                    new Vertex(0F, 1F, 0F, 0F, 1F),
                    new Vertex(2F / 16, 2F / 16, -2F / 16, 2F / 16, 2F / 16),
                    new Vertex(14F / 16, 2F / 16, -2F / 16, 14F / 16, 2F / 16),
                    new Vertex(14F / 16, 14F / 16, -2F / 16, 14F / 16, 14F / 16),
                    new Vertex(2F / 16, 14F / 16, -2F / 16, 2F / 16, 14F / 16)
            ),
            List.of(
                    new Vector4i(7, 4, 0, 3),
                    new Vector4i(5, 1, 0, 4),
                    new Vector4i(6, 2, 1, 5),
                    new Vector4i(7, 3, 2, 6),
                    new Vector4i(6, 5, 4, 7)
            ));

    public void render(GameMineSweeper game, float partialTicks, PoseStack matrix, @NotNull MultiBufferSource bufferIn) {

        int boardSize = game.getCurrentBoardSize();
        Stage stage = game.getStage();

        matrix.pushPose();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        matrix.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
        game.prepareMatrix(matrix);

        if (!game.cIsGenerated) {
            VertexConsumer brightenedBuilder = bufferIn.getBuffer(RT_BRIGHTENED_BOARD);
            for (int xL = 0; xL < boardSize; xL++) {
                for (int zL = 0; zL < boardSize; zL++) {
                    HIDDEN_CELL_MESH.render(brightenedBuilder, matrix, xL, zL, 0, 0, 0, 1, 1, 4);
                }
            }
        } else {
            for (int xL = 0; xL < boardSize; xL++) {
                for (int zL = 0; zL < boardSize; zL++) {
                    boolean isHidden = game.getBoard().isHidden(xL, zL);

                    Type type = game.getBoard().getType(xL, zL);

                    VertexConsumer brightenedBuilder = bufferIn.getBuffer(RT_BRIGHTENED_BOARD);
                    if (!isHidden && type == Type.BOMB) {
                        int max = stage instanceof StageDetonating ? ((StageDetonating) stage).getDetonationTicks() : 1;
                        int ticks = game.getTicks();

                        float period = 8;

                        float times = max / period;

                        float extendedPeriod = period * (times + 1) / times; // is needed because we want for it to explode at red state that comes on half period.
                        double alphaFactor = stage instanceof StageExploding ? 1 : Math.abs(Math.sin(Math.toRadians(ticks / extendedPeriod * 180F)));
                        int alphaColor = DrawHelper.changeAlpha(0xFFFFFFFF, (int) (alphaFactor * 255));

                        DrawHelper.buildTexturedRectByParts(brightenedBuilder, matrix, xL, zL, 1, 1, -0.005F, 1, 0, 1, 1, 4F);

                        VertexConsumer translucentBuilder = bufferIn.getBuffer(RT_BRIGHTENED_TRANSLUCENT_BOARD);
                        DrawHelper.buildTexturedRectByParts(translucentBuilder, matrix, xL, zL, 1, 1, -0.005F, 1, 3, 1, 1, 4F, alphaColor);
                    } else {

                        Mark mark = game.getBoard().getMark(xL, zL);

                        int textureX;
                        int textureY;

                        if (isHidden) {
                            if (mark == Mark.NO_MARK) {
                                textureX = 0;
                                textureY = 0;
                            } else if (mark == Mark.FLAG) {
                                textureX = 3;
                                textureY = 0;
                            } else {
                                textureX = 0;
                                textureY = 3;
                            }
                        } else {
                            if (type.getId() > 0) {
                                textureX = type.getId() % 4 == 0 ? 3 : (type.getId() % 4 - 1);
                                textureY = type.getId() <= 4 ? 1 : 2;
                            } else /*if type == empty*/ {
                                textureX = 2;
                                textureY = 0;
                            }
                        }

                        DrawHelper.buildTexturedRectByParts(brightenedBuilder, matrix, xL, zL, 1, 1, -0.005F, textureX, textureY, 1, 1, 4);
                    }
                }
            }
        }

        matrix.popPose();
    }

    @Data
    @AllArgsConstructor
    public static class CellMesh {
        private List<Vertex> vertices;
        private List<Vector4ic> quads;

        public void render(VertexConsumer buffer, PoseStack poseStack, float x, float y, float z, float tX, float tY, float tW, float tH, float texturePartCount) {
            PoseStack.Pose last = poseStack.last();
            Matrix4f matrix = last.pose();
            for (Vector4ic quad : quads) {
                renderVertex(vertices.get(quad.x()), buffer, matrix, x, y, z, tX, tY, tW, tH, texturePartCount);
                renderVertex(vertices.get(quad.y()), buffer, matrix, x, y, z, tX, tY, tW, tH, texturePartCount);
                renderVertex(vertices.get(quad.z()), buffer, matrix, x, y, z, tX, tY, tW, tH, texturePartCount);
                renderVertex(vertices.get(quad.w()), buffer, matrix, x, y, z, tX, tY, tW, tH, texturePartCount);
            }
        }

        public void renderVertex(Vertex v, VertexConsumer buffer, Matrix4f matrix, float x, float y, float z, float tX, float tY, float tW, float tH, float texturePartCount) {
            float dt = 1F / texturePartCount;
            buffer.vertex(matrix, v.x + x, v.y + y, v.z + z).uv((tX + tW * v.u) * dt, (tY + tH * v.v) * dt).endVertex();
        }
    }

    @Data
    @AllArgsConstructor
    public static class Vertex {
        private float x, y, z;
        private float u, v;
    }
}
