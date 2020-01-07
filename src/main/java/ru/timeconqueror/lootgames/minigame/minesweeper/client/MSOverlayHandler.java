package ru.timeconqueror.lootgames.minigame.minesweeper.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.minigame.minesweeper.tileentity.TileEntityMSMaster;
import ru.timeconqueror.timecore.api.auxiliary.RenderHelper;
import ru.timeconqueror.timecore.api.auxiliary.RenderHelper.TexturedRect;
import ru.timeconqueror.timecore.api.auxiliary.client.ClientUtils;

import java.awt.*;
import java.util.ArrayList;
//TODO F1 bug while looking at field
//todo FIX SERVER
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber
public class MSOverlayHandler {
    private static final ResourceLocation OVERLAY = new ResourceLocation(LootGames.MOD_ID, "textures/gui/minesweeper/ms_overlay.png");
    private static final ArrayList<TileEntityMSMaster> MS_MASTERS = new ArrayList<>(1);

    private static final TexturedRect firstSlotStart = new TexturedRect(3 * 1.5F, 16 * 1.5F, 15, 0, 3, 16);
    private static final TexturedRect firstSlotRepeat = new TexturedRect(26 * 1.5F, 16 * 1.5F, 18, 0, 26, 16);
    private static final TexturedRect firstSlotEnd = new TexturedRect(4 * 1.5F, 16 * 1.5F, 44, 0, 4, 16);

    private static final TexturedRect extraSlotStart = new TexturedRect(3 * 1.5F, 10 * 1.5F, 15, 16, 3, 10);
    private static final TexturedRect extraSlotRepeat = new TexturedRect(26 * 1.5F, 10 * 1.5F, 18, 16, 26, 10);
    private static final TexturedRect extraSlotEnd = new TexturedRect(4 * 1.5F, 10 * 1.5F, 44, 16, 4, 10);

    @SubscribeEvent
    public static void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            renderNearbyGameBombs();
            MS_MASTERS.clear();
        }
    }

    private static void renderNearbyGameBombs() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        MS_MASTERS.removeIf(tileEntityMSMaster -> {
            GameMineSweeper game = tileEntityMSMaster.getGame();
            BlockPos gamePos = game.getCentralGamePos();

            return player.getDistanceSq(gamePos) > game.getDefaultBroadcastDistance() * game.getDefaultBroadcastDistance();
        });

        if (MS_MASTERS.isEmpty()) return;

        boolean extendedInfo = false;
        if (MS_MASTERS.size() > 1) extendedInfo = true;

        float maxRectWidth = 0;
        for (TileEntityMSMaster msMaster : MS_MASTERS) {
            GameMineSweeper game = msMaster.getGame();
            String toDisplay = getBombDisplayString(game, extendedInfo);

            maxRectWidth = Math.max(maxRectWidth, fontRenderer.getStringWidth(toDisplay) + 5.5F * 2);
        }

        float startY = 20;
        for (int i = 0; i < MS_MASTERS.size(); i++) {
            TileEntityMSMaster msMaster = MS_MASTERS.get(i);
            GameMineSweeper game = msMaster.getGame();

            ClientUtils.bindTexture(OVERLAY);
            GlStateManager.color(1, 1, 1);

            Color color = game.getStage() == GameMineSweeper.Stage.DETONATING || game.getStage() == GameMineSweeper.Stage.EXPLODING ? Color.RED : Color.WHITE;
            String toDisplay = getBombDisplayString(game, extendedInfo);

            if (i == 0) {
                RenderHelper.drawTexturedRect(5, 5, 15 * 1.5, 16.0 * 1.5, 0, 0, 0, 15, 16, 48);

                RenderHelper.drawWidthExpandableTexturedRect(5 + 15 * 1.5F, 5, maxRectWidth, 0, firstSlotStart, firstSlotRepeat, firstSlotEnd, 48);

                RenderHelper.drawYCenteredStringWithShadow(fontRenderer, toDisplay, 32.6F, 17.5F, color.getRGB());
            } else {
                RenderHelper.drawWidthExpandableTexturedRect(27.5F, startY, maxRectWidth, 0, extraSlotStart, extraSlotRepeat, extraSlotEnd, 48);
                RenderHelper.drawYCenteredStringWithShadow(fontRenderer, toDisplay, 32.6F, startY + 8F, color.getRGB());
                startY += 7 * 1.5F;
            }
        }
    }

    private static String getBombDisplayString(GameMineSweeper game, boolean extended) {
        int bombDisplay = game.getStage() == GameMineSweeper.Stage.DETONATING || game.getStage() == GameMineSweeper.Stage.EXPLODING ? game.getBoard().getBombCount() :
                game.getBoard().getBombCount() - game.getBoard().getFlaggedField_c();
        BlockPos gamePos = game.getCentralGamePos();
        return extended ? "x" + bombDisplay + " {" + gamePos.getX() + ", " + gamePos.getY() + ", " + gamePos.getZ() + "}" : "x" + bombDisplay;
    }

    public static void addSupportedMaster(TileEntityMSMaster master) {
        MS_MASTERS.add(master);
    }
}
