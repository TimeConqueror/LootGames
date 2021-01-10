package ru.timeconqueror.lootgames.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.tile.TileBoardGameMaster;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.client.render.LGRenderTypes;
import ru.timeconqueror.lootgames.client.render.MSOverlayHandler;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityMSMaster;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper.StageDetonating;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper.StageExploding;
import ru.timeconqueror.lootgames.minigame.minesweeper.Mark;
import ru.timeconqueror.lootgames.minigame.minesweeper.Type;
import ru.timeconqueror.timecore.api.util.client.DrawHelper;

public class TESRMSMaster extends TileEntityRenderer<TileEntityMSMaster> {
    private static final ResourceLocation MS_BOARD = LootGames.rl("textures/game/ms_board.png");
    private static final RenderType RT_BRIGHTENED_BOARD = LGRenderTypes.brightened(MS_BOARD);
    private static final RenderType RT_BRIGHTENED_TRANSLUCENT_BOARD = LGRenderTypes.brightenedTranslucent(MS_BOARD);

    public TESRMSMaster(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEntityMSMaster te, float partialTicks, MatrixStack matrix, @NotNull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        GameMineSweeper game = te.getGame();
        int boardSize = game.getCurrentBoardSize();
        LootGame.Stage stage = game.getStage();

        matrix.pushPose();
        TileBoardGameMaster.prepareMatrix(matrix, te);

        if (!game.cIsGenerated) {
            IVertexBuilder brightenedBuilder = bufferIn.getBuffer(RT_BRIGHTENED_BOARD);
            for (int xL = 0; xL < boardSize; xL++) {
                for (int zL = 0; zL < boardSize; zL++) {
                    DrawHelper.drawTexturedRectByParts(brightenedBuilder, matrix, xL, zL, 1, 1, -0.005F, 0, 0, 1, 1, 4);
                }
            }
        } else {
            for (int xL = 0; xL < boardSize; xL++) {
                for (int zL = 0; zL < boardSize; zL++) {
                    boolean isHidden = game.getBoard().isHidden(xL, zL);

                    Type type = game.getBoard().getType(xL, zL);

                    IVertexBuilder brightenedBuilder = bufferIn.getBuffer(RT_BRIGHTENED_BOARD);
                    if (!isHidden && type == Type.BOMB) {
                        int max = stage instanceof StageDetonating ? ((StageDetonating) stage).getDetonationTicks() : 1;
                        int ticks = game.getTicks();

                        float period = 8;

                        float times = max / period;

                        float extendedPeriod = period * (times + 1) / times; // is needed because we want for it to explode at red state that comes on half period.
                        double alphaFactor = stage instanceof StageExploding ? 1 : Math.abs(Math.sin(Math.toRadians(ticks / extendedPeriod * 180F)));
                        int alphaColor = DrawHelper.changeAlpha(0xFFFFFFFF, (int) (alphaFactor * 255));

                        DrawHelper.drawTexturedRectByParts(brightenedBuilder, matrix, xL, zL, 1, 1, -0.005F, 1, 0, 1, 1, 4F);

                        IVertexBuilder translucentBuilder = bufferIn.getBuffer(RT_BRIGHTENED_TRANSLUCENT_BOARD);
                        DrawHelper.drawTexturedRectByParts(translucentBuilder, matrix, xL, zL, 1, 1, -0.005F, 1, 3, 1, 1, 4F, alphaColor);
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

                        DrawHelper.drawTexturedRectByParts(brightenedBuilder, matrix, xL, zL, 1, 1, -0.005F, textureX, textureY, 1, 1, 4);
                    }
                }
            }
        }

        matrix.popPose();

        MSOverlayHandler.addSupportedMaster(te);
    }

    @Override
    public boolean shouldRenderOffScreen(TileEntityMSMaster te) {
        return true;
    }
}
