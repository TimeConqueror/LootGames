package ru.timeconqueror.lootgames.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.tile.BoardGameMasterTile;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.client.render.LGRenderTypes;
import ru.timeconqueror.lootgames.common.block.tile.GOLMasterTile;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.timecore.animation.Ease;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.client.DrawHelper;

import static ru.timeconqueror.lootgames.minigame.gol.GameOfLight.StageUnderExpanding.MAX_TICKS_EXPANDING;

public class GOLMasterRenderer extends TileEntityRenderer<GOLMasterTile> {
    private static final RenderType RT_BOARD = LGRenderTypes.brightened(LootGames.rl("textures/game/gol_board.png"));
    private static final RenderType RT_BOARD_ACTIVE = LGRenderTypes.brightened(LootGames.rl("textures/game/gol_board_active.png"));

    public GOLMasterRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(GOLMasterTile tileEntityIn, float partialTicks, MatrixStack matrix, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrix.pushPose();
        BoardGameMasterTile.prepareMatrix(matrix, tileEntityIn);

        GameOfLight game = tileEntityIn.getGame();
        BoardLootGame.BoardStage stage = game.getStage();
        int ticks = stage instanceof GameOfLight.StageUnderExpanding ? ((GameOfLight.StageUnderExpanding) stage).getTicks() : 0;

        drawBoard(game, matrix, bufferIn, ticks, partialTicks);

        matrix.popPose();
    }

    public void drawBoard(GameOfLight game, MatrixStack matrix, IRenderTypeBuffer bufferIn, int ticks, float partialTicks) {
        IVertexBuilder buffer = bufferIn.getBuffer(RT_BOARD);

        float length = 3;
        float textureStart = 0;
        float textureLength = 48;

        if (game.getStage() instanceof GameOfLight.StageUnderExpanding) {
            float progress = Ease.inOutQuart((float) MathUtils.coerceInRange(ticks, 0, MAX_TICKS_EXPANDING) / MAX_TICKS_EXPANDING);

            length = MathUtils.interpolate(progress, 1, 3);
            textureStart = 16F - 16F * progress;
            textureLength = 32F + 16F * progress - textureStart;
        }

        matrix.translate(3 / 2F, 3 / 2F, 0);

        DrawHelper.drawTexturedRectByParts(buffer, matrix, -length / 2, -length / 2, length, length, -0.005F, textureStart, textureStart, textureLength, textureLength, 48);
    }

    @Override
    public boolean shouldRenderOffScreen(GOLMasterTile te) {
        return true;
    }
}
