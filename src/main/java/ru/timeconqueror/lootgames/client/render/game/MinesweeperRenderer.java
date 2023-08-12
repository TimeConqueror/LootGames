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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4i;
import org.joml.Vector4ic;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.Stage;
import ru.timeconqueror.lootgames.client.render.LGRenderTypes;
import ru.timeconqueror.lootgames.common.block.MinesweeperTechnicalBlock;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper.StageDetonating;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper.StageExploding;
import ru.timeconqueror.lootgames.minigame.minesweeper.Mark;
import ru.timeconqueror.lootgames.minigame.minesweeper.Type;
import ru.timeconqueror.lootgames.utils.VertexSink;

import java.util.List;

public class MinesweeperRenderer {
    private static final ResourceLocation MS_BOARD = LootGames.rl("textures/game/ms_board.png");
    private static final RenderType RT_BRIGHTENED_BOARD = LGRenderTypes.fullbright(MS_BOARD);
    private static final RenderType RT_BRIGHTENED_TRANSLUCENT_BOARD = RenderType.entityTranslucent(MS_BOARD);

    private static final CellMesh CONVEX_CELL_MESH = new CellMesh(true);
    private static final CellMesh CONCAVE_CELL_MESH = new CellMesh(false);

    private static final Vector3f NORMAL = new Vector3f();

    public void render(GameMineSweeper game, PoseStack matrix, @NotNull MultiBufferSource bufferIn, float partialTick) {
        NORMAL.set(0, 1, 0).mul(matrix.last().normal());

        int boardSize = game.getBoardSize();
        Stage stage = game.getStage();

        matrix.pushPose();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        matrix.translate(0, MinesweeperTechnicalBlock.VERTICAL_OFFSET, 0);
        matrix.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
        game.prepareMatrix(matrix);

        if (!game.cIsGenerated) {
            VertexConsumer brightenedBuilder = bufferIn.getBuffer(RT_BRIGHTENED_BOARD);
            for (int xL = 0; xL < boardSize; xL++) {
                for (int zL = 0; zL < boardSize; zL++) {
                    CONVEX_CELL_MESH.render(brightenedBuilder, matrix, xL, zL, 0, 0, 0, 1, 1, 4);
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
                        float ticks = game.getTicks() + partialTick;
                        float explosionFlashCount = 8.5F;
                        float alphaFactor = stage instanceof StageExploding ? 1 : Mth.abs(Mth.sin((float) Math.toRadians(ticks / (max / explosionFlashCount) * 180F)));

                        quad(new VertexSink(brightenedBuilder), matrix, xL, zL, 1, 1, 0, 1, 0, 1, 1, 4F, 255, NORMAL);

                        VertexConsumer translucentBuilder = bufferIn.getBuffer(RT_BRIGHTENED_TRANSLUCENT_BOARD);
                        quad(new VertexSink(translucentBuilder), matrix, xL, zL, 1, 1, 0, 1, 3, 1, 1, 4F, (int) (alphaFactor * 255), NORMAL);
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

                            CONVEX_CELL_MESH.render(brightenedBuilder, matrix, xL, zL, 0, textureX, textureY, 1, 1, 4);
                        } else {
                            if (type.getId() > 0) {
                                textureX = type.getId() % 4 == 0 ? 3 : (type.getId() % 4 - 1);
                                textureY = type.getId() <= 4 ? 1 : 2;
                            } else /*if type == empty*/ {
                                textureX = 2;
                                textureY = 0;
                            }

                            CONCAVE_CELL_MESH.render(brightenedBuilder, matrix, xL, zL, 0, textureX, textureY, 1, 1, 4);
                        }
                    }
                }
            }
        }

        matrix.popPose();
    }

    private static void quad(VertexSink sink, PoseStack matrixStack, float x0, float y0, float width, float height, float zLevel, float textureX, float textureY, float textureWidth, float textureHeight, float texturePartCount, int alpha, Vector3f normal) {
        Matrix4f pose = matrixStack.last().pose();
        float portionFactor = 1 / texturePartCount;

        sink.vertex(pose, x0, y0, zLevel).colorAlphaI(alpha).uv(textureX * portionFactor, textureY * portionFactor).endVertex(normal);
        sink.vertex(pose, x0, y0 + height, zLevel).colorAlphaI(alpha).uv(textureX * portionFactor, (textureY + textureHeight) * portionFactor).endVertex(normal);
        sink.vertex(pose, x0 + width, y0 + height, zLevel).colorAlphaI(alpha).uv((textureX + textureWidth) * portionFactor, (textureY + textureHeight) * portionFactor).endVertex(normal);
        sink.vertex(pose, x0 + width, y0, zLevel).colorAlphaI(alpha).uv((textureX + textureWidth) * portionFactor, textureY * portionFactor).endVertex(normal);
    }

    @Data
    @AllArgsConstructor
    public static class CellMesh {
        private List<Vertex> vertices;
        private List<Vector4ic> quads;

        public CellMesh(boolean isConvex) {
            vertices = List.of(
                    new Vertex(0F, 0F, 0F, 0F, 0F),
                    new Vertex(1F, 0F, 0F, 1F, 0F),
                    new Vertex(1F, 1F, 0F, 1F, 1F),
                    new Vertex(0F, 1F, 0F, 0F, 1F),
                    new Vertex(2F / 16, 2F / 16, isConvex ? -2F / 16 : 2F / 16, 2F / 16, 2F / 16),
                    new Vertex(14F / 16, 2F / 16, isConvex ? -2F / 16 : 2F / 16, 14F / 16, 2F / 16),
                    new Vertex(14F / 16, 14F / 16, isConvex ? -2F / 16 : 2F / 16, 14F / 16, 14F / 16),
                    new Vertex(2F / 16, 14F / 16, isConvex ? -2F / 16 : 2F / 16, 2F / 16, 14F / 16)
            );
            quads = List.of(
                    new Vector4i(7, 4, 0, 3),
                    new Vector4i(5, 1, 0, 4),
                    new Vector4i(6, 2, 1, 5),
                    new Vector4i(7, 3, 2, 6),
                    new Vector4i(6, 5, 4, 7)
            );
        }

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
