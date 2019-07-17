package ru.timeconqueror.lootgames.client.render.tesr;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.minigame.gameoflight.EnumPosOffset;
import ru.timeconqueror.lootgames.packets.CMessageGOLFeedback;
import ru.timeconqueror.lootgames.packets.NetworkHandler;
import ru.timeconqueror.lootgames.tileentity.TileEntityGOLMaster;

import static ru.timeconqueror.lootgames.tileentity.TileEntityGOLMaster.GameStage.*;
import static ru.timeconqueror.lootgames.tileentity.TileEntityGOLMaster.MAX_TICKS_EXPANDING;

public class TESRGOLMaster extends TileEntitySpecialRenderer<TileEntityGOLMaster> {
    private static final ResourceLocation GAME_FIELD = new ResourceLocation(LootGames.MODID, "textures/blocks/gameoflight/game_field.png");
    private static final ResourceLocation GAME_FIELD_ACTIVATED = new ResourceLocation(LootGames.MODID, "textures/blocks/gameoflight/game_field_active.png");

    @Override
    public void render(TileEntityGOLMaster te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.getGameStage() == NOT_CONSTRUCTED) {
            return;
        }

        drawField(te, x, y, z, te.getTicks(), partialTicks);

        if (te.getGameStage() == SHOWING_SEQUENCE) {
            if (!te.isOnPause()) {
                if (!te.hasShowedAllSymbols()) {
                    drawSymbol(te, te.getCurrentSymbolPosOffset(), x, y, z, te.getTicks(), partialTicks);
                } else if (!te.isFeedbackPacketReceived()) {
                    NetworkHandler.INSTANCE.sendToServer(new CMessageGOLFeedback(te.getPos())); //done to be lag-resistant
                    te.onClientThingsDone();
                }
            }
        }


        if (te.getSymbolsEnteredByPlayer() != null) {
            te.getSymbolsEnteredByPlayer().forEach((clickInfo -> drawSymbol(te, clickInfo.getOffset(), x, y, z, te.getTicks(), partialTicks)));
        }
    }

    public void drawSymbol(TileEntityGOLMaster te, EnumPosOffset offset, double masterX, double masterY, double masterZ, int ticks, float partialTicks) {
        if (te.getTicks() > TileEntityGOLMaster.ticksPerShowSymbols) {
            return;
        }

        this.bindTexture(GAME_FIELD_ACTIVATED);

        GlStateManager.pushMatrix();

        GlStateManager.translate(masterX + offset.getOffsetX(), masterY + 1f, masterZ + offset.getOffsetZ());

        GlStateManager.disableLighting();

        GlStateManager.rotate(90, 1, 0, 0);

        float f = 0.020833334f; // 1/48
        GlStateManager.scale(f, f, f);

        float textureX = 16 + 16 * offset.getOffsetX();
        float textureY = 16 + 16 * offset.getOffsetZ();

        drawRect(0, 0, 48, 48, -0.02, textureX, textureY, 16, 16, f);

        GlStateManager.color(1, 1, 1);
        GlStateManager.popMatrix();
    }

    public void drawField(TileEntityGOLMaster te, double x, double y, double z, int ticks, float partialTicks) {
        this.bindTexture(GAME_FIELD);

        boolean isExpanding = false;

        if (te.getGameStage() == UNDER_EXPANDING) {
            isExpanding = true;
        }

        GlStateManager.pushMatrix();

        GlStateManager.translate(x + 0.5F, y + 1f, z + 0.5F);

        GlStateManager.disableLighting();

        GlStateManager.rotate(90, 1, 0, 0);

        float f = 0.020833334f; // 1/48
        GlStateManager.scale(f, f, f);

        float length = !isExpanding || ticks >= MAX_TICKS_EXPANDING ? 144F : 48F + (96F * (ticks + partialTicks)) / MAX_TICKS_EXPANDING;

        GlStateManager.translate(-length / 2F, -length / 2F, 0);

        float textureStart = !isExpanding || ticks >= MAX_TICKS_EXPANDING ? 0F : 16F - 16F * (ticks + partialTicks) / MAX_TICKS_EXPANDING;
        float textureLength = !isExpanding || ticks >= MAX_TICKS_EXPANDING ? 48F : 32F + 16F * (ticks + partialTicks) / MAX_TICKS_EXPANDING - textureStart;

        drawRect(0, 0, length, length, -0.01, textureStart, textureStart, textureLength, textureLength, f);

        GlStateManager.color(1, 1, 1);
        GlStateManager.popMatrix();
    }

    private void drawRect(double x0, double y0, double width, double height, double zLevel, double textureX, double textureY, double textureWidth, double textureHeight, double portionFactor) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x0, y0, zLevel).tex(textureX * portionFactor, textureY * portionFactor).endVertex();
        bufferbuilder.pos(x0, height, zLevel).tex(textureX * portionFactor, (textureY + textureHeight) * portionFactor).endVertex();
        bufferbuilder.pos(width, height, zLevel).tex((textureX + textureWidth) * portionFactor, (textureY + textureHeight) * portionFactor).endVertex();
        bufferbuilder.pos(width, y0, zLevel).tex((textureX + textureWidth) * portionFactor, textureY * portionFactor).endVertex();
        tessellator.draw();
    }
}
