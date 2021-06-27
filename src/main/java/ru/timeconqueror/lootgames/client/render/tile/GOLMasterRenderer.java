package ru.timeconqueror.lootgames.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.tile.BoardGameMasterTile;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame.BoardStage;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.client.render.LGRenderTypes;
import ru.timeconqueror.lootgames.common.block.tile.GOLMasterTile;
import ru.timeconqueror.lootgames.minigame.gol.DisplayedSymbol;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight.StageShowSequence;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight.StageUnderExpanding;
import ru.timeconqueror.lootgames.minigame.gol.QMarkAppearance.State;
import ru.timeconqueror.lootgames.minigame.gol.Symbol;
import ru.timeconqueror.timecore.animation.Ease;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.client.DrawHelper;

import static ru.timeconqueror.lootgames.minigame.gol.GameOfLight.StageUnderExpanding.MAX_TICKS_EXPANDING;

//TODO draw board by symbol, not by fully not activated board with activated overlay
public class GOLMasterRenderer extends TileEntityRenderer<GOLMasterTile> {
    private static final RenderType RT_BOARD = LGRenderTypes.brightened(LootGames.rl("textures/game/gol_board.png"));
    private static final RenderType RT_BOARD_ACTIVE = LGRenderTypes.brightened(LootGames.rl("textures/game/gol_board_active.png"));
    private static final RenderType RT_MARKS = LGRenderTypes.brightenedTranslucent(LootGames.rl("textures/game/gol_marks.png"));

    public GOLMasterRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(GOLMasterTile tileEntityIn, float partialTicks, MatrixStack matrix, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrix.pushPose();
        BoardGameMasterTile.prepareMatrix(matrix, tileEntityIn);

        GameOfLight game = tileEntityIn.getGame();
        BoardStage stage = game.getStage();
        int ticks = stage instanceof StageUnderExpanding ? ((StageUnderExpanding) stage).getTicks() : 0;

        drawBoard(game, matrix, bufferIn, ticks, partialTicks);

        if (stage instanceof StageShowSequence) {
            Symbol symbol = ((StageShowSequence) stage).getSymbolForRender();
            if (symbol != null) {
                drawSymbol(matrix, bufferIn, symbol);
            }
        }

        for (DisplayedSymbol symbol : game.getDisplayedSymbols()) {
            drawSymbol(matrix, bufferIn, symbol.getSymbol());
        }

        State questionMark = game.getMarkState();
        if (questionMark != State.NONE) {
            drawMark(matrix, bufferIn, questionMark);
        }

        matrix.popPose();
    }

    private void drawSymbol(MatrixStack matrix, IRenderTypeBuffer bufferIn, Symbol symbol) {
        IVertexBuilder buffer = bufferIn.getBuffer(RT_BOARD_ACTIVE);

        Pos2i pos = symbol.getPos();

        DrawHelper.drawTexturedRectByParts(buffer, matrix, pos.getX(), pos.getY(), 1, 1, -0.006F, pos.getX(), pos.getY(), 1, 1, 3);
    }

    private void drawMark(MatrixStack matrix, IRenderTypeBuffer bufferIn, State state) {
        IVertexBuilder buffer = bufferIn.getBuffer(RT_MARKS);

        int textureX = 0;
        int textureY = 0;
        switch (state) {
            case ACCEPTED:
                textureX = 1;
                break;
            case DENIED:
                textureX = 0;
                textureY = 1;
                break;
        }

        DrawHelper.drawTexturedRectByParts(buffer, matrix, 1, 1, 1, 1, -0.006F, textureX, textureY, 1, 1, 2);
    }

    private void drawBoard(GameOfLight game, MatrixStack matrix, IRenderTypeBuffer bufferIn, int ticks, float partialTicks) {
        IVertexBuilder buffer = bufferIn.getBuffer(RT_BOARD);

        float length = 3;
        float textureStart = 0;
        float textureLength = 48;

        if (game.getStage() instanceof StageUnderExpanding) {
            float progress = Ease.inOutQuart((float) MathUtils.coerceInRange(ticks, 0, MAX_TICKS_EXPANDING) / MAX_TICKS_EXPANDING);

            length = MathUtils.lerp(progress, 1, 3);
            textureStart = 16F - 16F * progress;
            textureLength = 32F + 16F * progress - textureStart;
        }

        matrix.translate(3 / 2F, 3 / 2F, 0);
        DrawHelper.drawTexturedRectByParts(buffer, matrix, -length / 2, -length / 2, length, length, -0.005F, textureStart, textureStart, textureLength, textureLength, 48);
        matrix.translate(-3 / 2F, -3 / 2F, 0);
    }

    @Override
    public boolean shouldRenderOffScreen(GOLMasterTile te) {
        return true;
    }
}
