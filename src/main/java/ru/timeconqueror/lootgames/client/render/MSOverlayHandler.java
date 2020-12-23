package ru.timeconqueror.lootgames.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityMSMaster;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.timecore.util.MathUtils;
import ru.timeconqueror.timecore.util.client.DrawHelper;
import ru.timeconqueror.timecore.util.client.DrawHelper.TexturedRect;
import ru.timeconqueror.timecore.util.client.RenderHelper;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class MSOverlayHandler {
    private static final ArrayList<WeakReference<TileEntityMSMaster>> MS_MASTERS = new ArrayList<>(1);

    private static final TexturedRect FIRST_SLOT_START = new TexturedRect(3 * 1.5F, 16 * 1.5F, 15, 0, 3, 16);
    private static final TexturedRect FIRST_SLOT_REPEAT = new TexturedRect(26 * 1.5F, 16 * 1.5F, 18, 0, 26, 16);
    private static final TexturedRect FIRST_SLOT_END = new TexturedRect(4 * 1.5F, 16 * 1.5F, 44, 0, 4, 16);

    private static final TexturedRect EXTRA_SLOT_START = new TexturedRect(3 * 1.5F, 10 * 1.5F, 15, 16, 3, 10);
    private static final TexturedRect EXTRA_SLOT_REPEAT = new TexturedRect(26 * 1.5F, 10 * 1.5F, 18, 16, 26, 10);
    private static final TexturedRect EXTRA_SLOT_END = new TexturedRect(4 * 1.5F, 10 * 1.5F, 44, 16, 4, 10);

    public static final RenderType TEX_QUAD_TYPE = RenderHelper.rtTexturedRectangles(LootGames.rl("textures/gui/minesweeper/ms_overlay.png"));

    @SubscribeEvent
    public static void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            renderNearbyGameBombs(event.getMatrixStack());
            MS_MASTERS.clear();
        }
    }

    private static void renderNearbyGameBombs(MatrixStack matrixStack) {
        PlayerEntity player = Minecraft.getInstance().player;
        FontRenderer fontRenderer = Minecraft.getInstance().font;

        List<TileEntityMSMaster> masters = new ArrayList<>(1);
        Iterator<WeakReference<TileEntityMSMaster>> iterator = MS_MASTERS.iterator();
        while (iterator.hasNext()) {
            TileEntityMSMaster master = iterator.next().get();

            if (master == null) {
                iterator.remove();
                continue;
            }

            GameMineSweeper game = master.getGame();
            BlockPos gamePos = game.getGameCenter();

            if (MathUtils.distSqr(gamePos, player) > game.getBroadcastDistance() * game.getBroadcastDistance()) {
                iterator.remove();
                continue;
            }

            masters.add(master);
        }

        if (masters.isEmpty()) return;

        boolean extendedInfo = false;
        if (masters.size() > 1) extendedInfo = true;

        float maxRectWidth = 0;
        for (TileEntityMSMaster msMaster : masters) {
            GameMineSweeper game = msMaster.getGame();
            String toDisplay = getBombDisplayString(game, extendedInfo);

            maxRectWidth = Math.max(maxRectWidth, fontRenderer.width(toDisplay) + 5.5F * 2);
        }

        float startY = 20;

        RenderHelper.RenderPipeline renderPipeline = RenderHelper.guiRenderPipeline();

        for (int i = 0; i < masters.size(); i++) {
            TileEntityMSMaster msMaster = masters.get(i);
            GameMineSweeper game = msMaster.getGame();

            Color color = game.getStage() instanceof GameMineSweeper.StageDetonating || game.getStage() instanceof GameMineSweeper.StageExploding ? Color.RED : Color.WHITE;
            String toDisplay = getBombDisplayString(game, extendedInfo);

            float finalMaxRectWidth = maxRectWidth;
            if (i == 0) {
                renderPipeline.renderAndEnd(TEX_QUAD_TYPE, builder -> {
                    DrawHelper.drawTexturedRectByParts(builder, matrixStack, 5, 5, 15 * 1.5F, 16 * 1.5F, 0, 0, 0, 15, 16, 48);
                    DrawHelper.drawWidthExpandableTexturedRect(builder, matrixStack, 5 + 15 * 1.5F, 5, finalMaxRectWidth, 0, FIRST_SLOT_START, FIRST_SLOT_REPEAT, FIRST_SLOT_END, 48);
                });

                DrawHelper.drawYCenteredStringWithShadow(matrixStack, fontRenderer, toDisplay, 32.6F, 17.5F, color.getRGB());
            } else {
                float finalStartY = startY;

                renderPipeline.renderAndEnd(TEX_QUAD_TYPE, builder -> {
                    DrawHelper.drawWidthExpandableTexturedRect(builder, matrixStack, 27.5F, finalStartY, finalMaxRectWidth, 0, EXTRA_SLOT_START, EXTRA_SLOT_REPEAT, EXTRA_SLOT_END, 48);
                });

                DrawHelper.drawYCenteredStringWithShadow(matrixStack, fontRenderer, toDisplay, 32.6F, startY + 8F, color.getRGB());
                startY += 7 * 1.5F;
            }
        }

        renderPipeline.end();
    }

    private static String getBombDisplayString(GameMineSweeper game, boolean extended) {
        int bombDisplay = game.getStage() instanceof GameMineSweeper.StageDetonating || game.getStage() instanceof GameMineSweeper.StageExploding ? game.getBoard().getBombCount() :
                game.getBoard().getBombCount() - game.getBoard().cGetFlaggedField();
        BlockPos gamePos = game.getGameCenter();
        return extended ? "x" + bombDisplay + " on "/*todo translate */ + gamePos.getX() + ", " + gamePos.getY() + ", " + gamePos.getZ() : "x" + bombDisplay;
    }

    public static void addSupportedMaster(TileEntityMSMaster master) {
        if (!Minecraft.getInstance().options.hideGui || Minecraft.getInstance().screen != null) {
            MS_MASTERS.add(new WeakReference<>(master));
        }
    }
}