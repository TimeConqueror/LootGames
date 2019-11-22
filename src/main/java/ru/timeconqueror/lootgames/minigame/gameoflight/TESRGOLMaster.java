package ru.timeconqueror.lootgames.minigame.gameoflight;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.util.client.RenderUtils;
import ru.timeconqueror.lootgames.packets.CMessageGOLFeedback;
import ru.timeconqueror.lootgames.packets.NetworkHandler;

import static ru.timeconqueror.lootgames.minigame.gameoflight.TileEntityGOLMaster.EnumDrawStuff;
import static ru.timeconqueror.lootgames.minigame.gameoflight.TileEntityGOLMaster.GameStage.*;
import static ru.timeconqueror.lootgames.minigame.gameoflight.TileEntityGOLMaster.MAX_TICKS_EXPANDING;

public class TESRGOLMaster extends TileEntitySpecialRenderer<TileEntityGOLMaster> {
    private static final ResourceLocation GAME_FIELD = new ResourceLocation(LootGames.MOD_ID, "textures/blocks/gameoflight/game_field.png");
    private static final ResourceLocation GAME_FIELD_ACTIVATED = new ResourceLocation(LootGames.MOD_ID, "textures/blocks/gameoflight/game_field_active.png");
    private static final ResourceLocation SPECIAL_STUFF = new ResourceLocation(LootGames.MOD_ID, "textures/blocks/gameoflight/special_stuff.png");

    @Override
    public void render(TileEntityGOLMaster te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.getGameStage() == NOT_CONSTRUCTED) {
            return;
        }

        drawField(te, x, y, z, te.getTicks(), partialTicks);

        if (te.getGameStage() == SHOWING_SEQUENCE) {
            drawStuff(te, EnumDrawStuff.SHOWING_SEQUENCE, x, y, z, te.getTicks(), partialTicks);

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

        if (te.getStuffToDraw() != null) {
            te.getStuffToDraw().forEach((stuff -> drawStuff(te, stuff.getStuff(), x, y, z, te.getTicks(), partialTicks)));
        }
    }

    public void drawStuff(TileEntityGOLMaster te, TileEntityGOLMaster.EnumDrawStuff stuff, double masterX, double masterY, double masterZ, int ticks, float partialTicks) {

        this.bindTexture(SPECIAL_STUFF);

        GlStateManager.pushMatrix();

        GlStateManager.translate(masterX, masterY + 1F, masterZ);

        GlStateManager.disableLighting();

        GlStateManager.rotate(90, 1, 0, 0);

        float f = 0.020833334f; // 1/48
        GlStateManager.scale(f, f, f);

        float textureX = 16 * (stuff == EnumDrawStuff.SEQUENCE_ACCEPTED ? 0 : stuff == EnumDrawStuff.SHOWING_SEQUENCE ? 1 : 2);
        float textureY = 0;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.disableColorMaterial();
        RenderUtils.drawRect(0, 0, 48, 48, -0.07, textureX, textureY, 16, 16, f);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        GlStateManager.color(1, 1, 1);
        GlStateManager.popMatrix();
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

        RenderUtils.drawRect(0, 0, 48, 48, -0.07, textureX, textureY, 16, 16, f);

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

        RenderUtils.drawRect(0, 0, length, length, -0.05, textureStart, textureStart, textureLength, textureLength, f);

        GlStateManager.color(1, 1, 1);
        GlStateManager.popMatrix();
    }
}
