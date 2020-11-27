package ru.timeconqueror.lootgames.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityMSMaster;

public class TESRMSMaster extends TileEntityRenderer<TileEntityMSMaster> {
    private static final ResourceLocation MS_BOARD = new ResourceLocation(LootGames.MODID, "textures/game/ms_board.png");

    public TESRMSMaster(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEntityMSMaster tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

    }
//FIXME
//    @Override
//    public void render(TileEntityMSMaster te, double x, double y, double z, float partialTicks, int destroyStage) {
//        bindTexture(MS_BOARD);
//        GameMineSweeper game = te.getGame();
//        int boardSize = game.getBoardSize();
//
//        GlStateManager.pushMatrix();
//        GlStateManager.translated(x, y + 1D, z);
//        GlStateManager.disableLighting();
//        setLightmapDisabled(true);
//        GlStateManager.rotatef(90, 1, 0, 0);
//
//        if (!game.cIsGenerated) {
//            for (int xL = 0; xL < boardSize; xL++) {
//                for (int zL = 0; zL < boardSize; zL++) {
//                    DrawHelper.drawTexturedRect(xL, zL, 1, 1, -0.005F, 0, 0, 1, 1, 4);
//                }
//            }
//        } else {
//            for (int xL = 0; xL < boardSize; xL++) {
//                for (int zL = 0; zL < boardSize; zL++) {
//                    boolean isHidden = game.getBoard().isHidden(xL, zL);
//
//                    @Type
//                    int type = game.getBoard().getType(xL, zL);
//
//                    if (!isHidden && type == BOMB) {
////                        int max = game.detonationTimeInTicks;//FIXME restore
//                        int max = 1;
//                        int ticks = game.getTicks();
//
//                        int times = 9;
//                        float period = (float) max / times;
//
//                        float extendedPeriod = period * (times + 1) / times; // is needed because we want that it will explode at red state that comes on half period.
//                        double alphaFactor = game.getStage() instanceof StageExploding ? 1 : Math.abs(Math.sin(Math.toRadians(ticks / extendedPeriod * 180F)));
//
//                        DrawHelper.drawTexturedRect(xL, zL, 1, 1, -0.005F, 1, 0, 1, 1, 4F);//TODO timecore, change point number to mor adequate name
//                        GlStateManager.enableBlend();
//                        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//                        GlStateManager.enableAlphaTest();
//                        GL11.glColor4d(1, 1, 1, alphaFactor);
//                        DrawHelper.drawTexturedRect(xL, zL, 1, 1, -0.005F, 1, 3, 1, 1, 4F);
//                        GlStateManager.disableAlphaTest();
//                        GlStateManager.disableBlend();
//                    } else {
//
//                        Mark mark = game.getBoard().getMark(xL, zL);
//
//                        int textureX;
//                        int textureY;
//
//                        if (isHidden) {
//                            if (mark == Mark.NO_MARK) {
//                                textureX = 0;
//                                textureY = 0;
//                            } else if (mark == Mark.FLAG) {
//                                textureX = 3;
//                                textureY = 0;
//                            } else {
//                                textureX = 0;
//                                textureY = 3;
//                            }
//                        } else {
//                            if (type > 0) {
//                                textureX = type % 4 == 0 ? 3 : (type % 4 - 1);
//                                textureY = type <= 4 ? 1 : 2;
//                            } else /*if type == empty*/ {
//                                textureX = 2;
//                                textureY = 0;
//                            }
//                        }
//
//                        DrawHelper.drawTexturedRectP(xL, zL, 1, 1, -0.005F, textureX, textureY, 1, 1, 0.25F);
//                    }
//                }
//            }
//        }
//        setLightmapDisabled(false);
//        GlStateManager.enableLighting();
//        GlStateManager.popMatrix();
//
////        MSOverlayHandler.addSupportedMaster(te);//FIXME restore
//    }

//    @Override
//    public boolean isGlobalRenderer(TileEntityMSMaster te) {
//        return true;
//    }
}
