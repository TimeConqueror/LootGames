package ru.timeconqueror.lootgames.minigame.minesweeper.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.util.client.RenderUtils;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.minigame.minesweeper.tileentity.TileEntityMSMaster;

public class TESRMSMaster extends TileEntitySpecialRenderer<TileEntityMSMaster> {
    private static final ResourceLocation MS_BOARD = new ResourceLocation(LootGames.MODID, "textures/blocks/minesweeper/ms_board.png");

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
                    RenderUtils.drawRect(xL, zL, xL + 1, zL + 1, -0.005F, 0, 0, 1, 1, 0.25F);
                }
            }
        } else {
            for (int xL = 0; xL < boardSize; xL++) {
                for (int zL = 0; zL < boardSize; zL++) {
                    if (game.getBoard().asArray()[xL][zL] == null) {
                        RenderUtils.drawRect(xL, zL, xL + 1, zL + 1, -0.005F, 0, 0, 1, 1, 0.25F);
                        continue;
                    }

                    int type = game.getBoard().getFieldTypeByPos(xL, zL);
                    int mark = game.getBoard().getFieldMarkByPos(xL, zL);
                    if (type > 8 || type < -1 || mark < -1 || mark > 1) {
                        LootGames.logHelper.error("Something went wrong. Got type {} in MS TESR.", type);
                        continue;
                    }

                    int textureX;
                    int textureY = 0;
                    if (type > 0) {
                        textureX = type % 4 == 0 ? 3 : (type % 4 - 1);
                        textureY = type <= 4 ? 1 : 2;
                    } else {
                        if (mark != -1) {
                            if (mark == 0) {
                                textureX = 3;
                                textureY = 0;
                            } else {
                                textureX = 0;
                                textureY = 3;
                            }
                        } else {
                            textureX = type == -1 ? 1 : 2;
                        }
                    }

                    RenderUtils.drawRect(xL, zL, xL + 1, zL + 1, -0.005F, textureX, textureY, 1, 1, 0.25F);
                }
            }
        }

        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TileEntityMSMaster te) {
        return true;
    }
}
