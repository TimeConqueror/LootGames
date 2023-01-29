package ru.timeconqueror.lootgames.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.client.render.LGRenderTypes;
import ru.timeconqueror.lootgames.common.block.tile.PipesMasterTile;
import ru.timeconqueror.lootgames.minigame.pipes.GamePipes;
import ru.timeconqueror.lootgames.minigame.pipes.board.PipeState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PipesMasterRenderer implements BlockEntityRenderer<PipesMasterTile> {

    private static final ResourceLocation BOARD = new ResourceLocation(LootGames.MODID, "textures/game/pipes.png");
    private static final RenderType BOARD_RENDER_TYPE = LGRenderTypes.brightenedCutout(BOARD);

    @Override
    public void render(PipesMasterTile te, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        GamePipes game = te.getGame();
        int size = game.getCurrentBoardSize();
        int animation = (int) (te.getAge() / 2 % 32);

        VertexConsumer vb = bufferIn.getBuffer(BOARD_RENDER_TYPE);

        BlockPos boardOrigin = game.getBoardOrigin();
        BlockPos offset = boardOrigin.subtract(te.getBlockPos());

        matrixStack.pushPose();
        matrixStack.translate(offset.getX(), 1.005, offset.getZ());
        Matrix4f matrix = matrixStack.last().pose();

        float height = 0.005f;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                vb.vertex(matrix, i, 0, j).uv(0.0f, 0.0f).endVertex();
                vb.vertex(matrix, i, 0, j + 1).uv(0.0f, 0.25f / 32).endVertex();
                vb.vertex(matrix, i + 1, 0, j + 1).uv(0.25f, 0.25f / 32).endVertex();
                vb.vertex(matrix, i + 1, 0, j).uv(0.25f, 0.0f).endVertex();

                PipeState state = game.getBoard().getState(i, j);
                int type = state.getPipeType();
                int rotation = state.getRotation();

                if (type != 0) {
                    float tx = (float) (type % 4) / 4;
                    float ty = (float) (type / 4) / 4;

                    withTexture(vb.vertex(matrix, i, height, j), tx, ty, 0, rotation, animation).endVertex();
                    withTexture(vb.vertex(matrix, i, height, j + 1), tx, ty, 1, rotation, animation).endVertex();
                    withTexture(vb.vertex(matrix, i + 1, height, j + 1), tx, ty, 2, rotation, animation).endVertex();
                    withTexture(vb.vertex(matrix, i + 1, height, j), tx, ty, 3, rotation, animation).endVertex();
                }
            }
        }

        matrixStack.popPose();
    }

    private VertexConsumer withTexture(VertexConsumer buf, float textureX, float textureY, int vertexId, int rotation, int animation) {
        float startY = textureY + animation;

        int corner = (rotation + vertexId) % 4;
        switch (corner) {
            case 0:
                return buf.uv(textureX, startY / 32);
            case 1:
                return buf.uv(textureX, (startY + 0.25f) / 32);
            case 2:
                return buf.uv(textureX + 0.25f, (startY + 0.25f) / 32);
            default:
                return buf.uv(textureX + 0.25f, startY / 32);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(PipesMasterTile te) {
        return true;
    }
}
