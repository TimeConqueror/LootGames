package ru.timeconqueror.lootgames.minigame.minesweeper.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.minigame.minesweeper.tileentity.TileEntityMSMaster;
import ru.timeconqueror.timecore.api.auxiliary.RenderHelper;

import static ru.timeconqueror.lootgames.minigame.minesweeper.MSBoard.MSField.*;

public class TESRMSMaster extends TileEntitySpecialRenderer<TileEntityMSMaster> {
    private static final ResourceLocation MS_BOARD = new ResourceLocation(LootGames.MOD_ID, "textures/blocks/minesweeper/ms_board.png");

    @Override
    public void render(TileEntityMSMaster te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        bindTexture(MS_BOARD);
        GameMineSweeper game = te.getGame();
        int boardSize = game.getBoardSize();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 1F, z);
        GlStateManager.disableLighting();
        GlStateManager.rotate(90, 1, 0, 0);

        if (!game.cIsGenerated) {
            for (int xL = 0; xL < boardSize; xL++) {
                for (int zL = 0; zL < boardSize; zL++) {
                    RenderHelper.drawTexturedRectP(xL, zL, 1, 1, -0.005F, 0, 0, 1, 1, 0.25F);
                }
            }
        } else {
            for (int xL = 0; xL < boardSize; xL++) {
                for (int zL = 0; zL < boardSize; zL++) {
                    boolean isHidden = game.getBoard().isHidden(xL, zL);

                    @Type
                    int type = game.getBoard().getType(xL, zL);

                    if (!isHidden && type == BOMB) {
                        int max = game.detonationTimeInTicks;
                        int ticks = game.getTicks();

                        int times = 3;
                        float period = (float) max / times;

                        float extendedPeriod = period * (times + 1) / times; // is needed because we want that it will explode at red state that comes on half period.
                        double alphaFactor = Math.abs(Math.sin(Math.toRadians(ticks / extendedPeriod * 180F)));

                        RenderHelper.drawTexturedRectP(xL, zL, 1, 1, -0.005F, 1, 0, 1, 1, 0.25F);
                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                        GlStateManager.enableAlpha();
                        GL11.glColor4d(1, 1, 1, alphaFactor);
                        RenderHelper.drawTexturedRectP(xL, zL, 1, 1, -0.005F, 1, 3, 1, 1, 0.25F);
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                    } else {
                        @Mark
                        int mark = game.getBoard().getMark(xL, zL);

                        int textureX;
                        int textureY;

                        if (isHidden) {
                            if (mark == NO_MARK) {
                                textureX = 0;
                                textureY = 0;
                            } else if (mark == FLAG) {
                                textureX = 3;
                                textureY = 0;
                            } else {
                                textureX = 0;
                                textureY = 3;
                            }
                        } else {
                            if (type > 0) {
                                textureX = type % 4 == 0 ? 3 : (type % 4 - 1);
                                textureY = type <= 4 ? 1 : 2;
                            } else /*if type == empty*/ {
                                textureX = 2;
                                textureY = 0;
                            }
                        }

                        RenderHelper.drawTexturedRectP(xL, zL, 1, 1, -0.005F, textureX, textureY, 1, 1, 0.25F);
                    }
                }
            }
        }
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        MSOverlayHandler.addSupportedMaster(te);
    }

    @Override
    public boolean isGlobalRenderer(TileEntityMSMaster te) {
        return true;
    }
}
