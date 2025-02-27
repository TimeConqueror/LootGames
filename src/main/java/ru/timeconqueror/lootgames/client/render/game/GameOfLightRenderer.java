package ru.timeconqueror.lootgames.client.render.game;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2ic;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.client.render.LGRenderTypes;
import ru.timeconqueror.lootgames.minigame.gol.DisplayedSymbol;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.minigame.gol.QMarkAppearance;
import ru.timeconqueror.lootgames.minigame.gol.Symbol;
import ru.timeconqueror.timecore.animation.Ease;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.client.DrawHelper;

import static ru.timeconqueror.lootgames.minigame.gol.GameOfLight.StageUnderExpanding.MAX_TICKS_EXPANDING;

//TODO draw board by symbol, not by fully not activated board with activated overlay
public class GameOfLightRenderer {
    private static final RenderType RT_BOARD = LGRenderTypes.fullbright(LootGames.rl("textures/game/gol_board.png"));
    private static final RenderType RT_BOARD_ACTIVE = LGRenderTypes.fullbright(LootGames.rl("textures/game/gol_board_active.png"));
    private static final RenderType RT_MARKS = LGRenderTypes.fullbrightTranslucent(LootGames.rl("textures/game/gol_marks.png"));
    private static final RenderType RT_BORDER = LGRenderTypes.solidWithCustomTexture(LootGames.rl("textures/game/game_light_off_new.png"));

    public void render(GameOfLight game, PoseStack poseStack, @NotNull MultiBufferSource bufferIn, float partialTick) {
        poseStack.pushPose();
        game.prepareMatrix(poseStack);

        BoardLootGame.BoardStage stage = game.getStage();
        int ticks = stage instanceof GameOfLight.StageUnderExpanding ? ((GameOfLight.StageUnderExpanding) stage).getTicks() : 0;

        drawDecorationBorder(game, poseStack, bufferIn);

        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        drawBoard(game, poseStack, bufferIn, ticks, partialTick);

        if (stage instanceof GameOfLight.StageShowSequence) {
            Symbol symbol = ((GameOfLight.StageShowSequence) stage).getSymbolForRender();
            if (symbol != null) {
                drawSymbol(poseStack, bufferIn, symbol);
            }
        }

        for (DisplayedSymbol symbol : game.getDisplayedSymbols()) {
            drawSymbol(poseStack, bufferIn, symbol.getSymbol());
        }

        QMarkAppearance.State questionMark = game.getMarkState();
        if (questionMark != QMarkAppearance.State.NONE) {
            drawMark(poseStack, bufferIn, questionMark);
        }

        poseStack.popPose();
    }

    private void drawSymbol(PoseStack matrix, MultiBufferSource bufferIn, Symbol symbol) {
        VertexConsumer buffer = bufferIn.getBuffer(RT_BOARD_ACTIVE);

        Vector2ic pos = symbol.getPos();

        DrawHelper.buildTexturedRectByParts(buffer, matrix, pos.x(), pos.y(), 1, 1, -0.006F, pos.x(), pos.y(), 1, 1, 3);
    }

    private void drawMark(PoseStack matrix, MultiBufferSource bufferIn, QMarkAppearance.State state) {
        VertexConsumer buffer = bufferIn.getBuffer(RT_MARKS);

        int textureX = 0;
        int textureY = 0;
        switch (state) {
            case ACCEPTED -> textureX = 1;
            case DENIED -> {
                textureX = 0;
                textureY = 1;
            }
        }

        DrawHelper.buildTexturedRectByParts(buffer, matrix, 1, 1, 1, 1, -0.006F, textureX, textureY, 1, 1, 2);
    }

    private void drawBoard(GameOfLight game, PoseStack matrix, MultiBufferSource bufferIn, int ticks, float partialTicks) {
        VertexConsumer buffer = bufferIn.getBuffer(RT_BOARD);

        float length = 3;
        float textureStart = 0;
        float textureLength = 48;

        if (game.getStage() instanceof GameOfLight.StageUnderExpanding) {
            float progress = Ease.inOutQuart((float) MathUtils.coerceInRange(ticks, 0, MAX_TICKS_EXPANDING) / MAX_TICKS_EXPANDING);

            length = MathUtils.lerp(progress, 1, 3);
            textureStart = 16F - 16F * progress;
            textureLength = 32F + 16F * progress - textureStart;
        }

        matrix.translate(3 / 2F, 3 / 2F, 0);
        DrawHelper.buildTexturedRectByParts(buffer, matrix, -length / 2, -length / 2, length, length, -0.005F, textureStart, textureStart, textureLength, textureLength, 48);
        matrix.translate(-3 / 2F, -3 / 2F, 0);
    }

    private void drawDecorationBorder(GameOfLight game, PoseStack matrix, MultiBufferSource bufferIn) {
        VertexConsumer buffer = bufferIn.getBuffer(RT_BORDER);
        PoseStack.Pose last = matrix.last();

        matrix.translate(-1, 0, -1);
        drawBorderBlock(buffer, last, 0, 0, 15, 15, 15, 15);
        drawBorderBlock(buffer, last, 1, 0, 15, 15, 15, 15);
        drawBorderBlock(buffer, last, 2, 0, 15, 15, 15, 15);
        drawBorderBlock(buffer, last, 3, 0, 15, 15, 15, 15);
        drawBorderBlock(buffer, last, 4, 0, 15, 15, 15, 15);

        drawBorderBlock(buffer, last, 0, 1, 15, 15, 15, 15);
        drawBorderBlock(buffer, last, 4, 1, 15, 15, 15, 15);

        drawBorderBlock(buffer, last, 0, 2, 15, 15, 15, 15);
        drawBorderBlock(buffer, last, 4, 2, 15, 15, 15, 15);

        drawBorderBlock(buffer, last, 0, 3, 15, 15, 15, 15);
        drawBorderBlock(buffer, last, 4, 3, 15, 15, 15, 15);

        drawBorderBlock(buffer, last, 0, 4, 15, 15, 15, 15);
        drawBorderBlock(buffer, last, 1, 4, 15, 15, 15, 15);
        drawBorderBlock(buffer, last, 2, 4, 15, 15, 15, 15);
        drawBorderBlock(buffer, last, 3, 4, 15, 15, 15, 15);
        drawBorderBlock(buffer, last, 4, 4, 15, 15, 15, 15);
        matrix.translate(1, 0, 1);
    }

    private void drawBorderBlock(VertexConsumer buffer, PoseStack.Pose matrices, int x, int z, int blockLight1, int blockLight2, int blockLight3, int blockLight4) {
        Matrix4f pose = matrices.pose();
        Matrix3f normal = matrices.normal();

        float tileCount = 5;

        float u1 = x / tileCount;
        float v1 = z / tileCount;

        buffer.vertex(pose, x, 0, z)
                .color(1F, 1F, 1F, 1F)
                .uv(u1, v1)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0, 1, 0)
                .endVertex();
        buffer.vertex(pose, x, 0, z + 1)
                .color(1F, 1F, 1F, 1F)
                .uv(u1, v1 + 1 / tileCount)
                .uv2(LightTexture.FULL_BRIGHT)
//                .uv2(LightTexture.pack(blockLight, LightTextureExt.MAX_SKY_LIGHT))
                .normal(normal, 0, 1, 0)
                .endVertex();
        buffer.vertex(pose, x + 1, 0, z + 1)
                .color(1F, 1F, 1F, 1F)
                .uv(u1 + 1 / tileCount, v1 + 1 / tileCount)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0, 1, 0)
                .endVertex();
        buffer.vertex(pose, x + 1, 0, z)
                .color(1F, 1F, 1F, 1F)
                .uv(u1 + 1 / tileCount, v1)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0, 1, 0)
                .endVertex();
    }
}
