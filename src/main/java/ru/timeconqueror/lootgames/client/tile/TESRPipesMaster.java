package ru.timeconqueror.lootgames.client.tile;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityPipesMaster;
import ru.timeconqueror.lootgames.minigame.pipes.GamePipes;
import ru.timeconqueror.lootgames.minigame.pipes.board.PipeState;

public class TESRPipesMaster extends TileEntityRenderer<TileEntityPipesMaster> {

    private static final ResourceLocation BOARD = new ResourceLocation(LootGames.MODID, "textures/game/pipes.png");

    @Override
    public void render(TileEntityPipesMaster te, double x, double y, double z, float partialTicks, int destroyStage) {
        GamePipes game = te.getGame();
        int size = game.getBoardSize();

        bindTexture(BOARD);
        GlStateManager.pushMatrix();

        setLightmapDisabled(true);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.enableTexture();
        GlStateManager.translated(x, y + 1.001, z);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(7, DefaultVertexFormats.POSITION_TEX);

        double height = 0.01;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buf.pos(i, 0, j).tex(0.0, 0.0).endVertex();
                buf.pos(i, 0, j + 1).tex(0.0, 0.25).endVertex();
                buf.pos(i + 1, 0, j + 1).tex(0.25, 0.25).endVertex();
                buf.pos(i + 1, 0, j).tex(0.25, 0.0).endVertex();

                PipeState state = game.getBoard().getState(i, j);
                int type = state.getPipeType();
                int rotation = state.getRotation();

                if (type != 0) {

                    double tx = (double) (type % 4) / 4;
                    double ty = (double) (type / 4) / 4;

                    textures(buf.pos(i, height, j), tx, ty, 0, rotation).endVertex();
                    textures(buf.pos(i, height, j + 1), tx, ty, 1, rotation).endVertex();
                    textures(buf.pos(i + 1, height, j + 1), tx, ty, 2, rotation).endVertex();
                    textures(buf.pos(i + 1, height, j), tx, ty, 3, rotation).endVertex();
                }
            }
        }

        tessellator.draw();

        setLightmapDisabled(false);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private BufferBuilder textures(BufferBuilder buf, double tx, double ty, int i, int rotation) {
        int c = (i + rotation) % 4;
        switch (c) {
            case 0:
                return buf.tex(tx, ty);
            case 1:
                return buf.tex(tx, ty + 0.25);
            case 2:
                return buf.tex(tx + 0.25, ty + 0.25);
            default:
                return buf.tex(tx + 0.25, ty);
        }
    }

    @Override
    public boolean isGlobalRenderer(TileEntityPipesMaster te) {
        return true;
    }
}
