package ru.timeconqueror.lootgames.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.block.tile.MSMasterTile;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.client.DrawHelper;
import ru.timeconqueror.timecore.api.util.client.DrawHelper.TexturedRect;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MSOverlayHandler {
    private static final ArrayList<WeakReference<MSMasterTile>> MS_MASTERS = new ArrayList<>(1);

    private static final TexturedRect FIRST_SLOT_START = new TexturedRect(3 * 1.5F, 16 * 1.5F, 15, 0, 3, 16);
    private static final TexturedRect FIRST_SLOT_REPEAT = new TexturedRect(26 * 1.5F, 16 * 1.5F, 18, 0, 26, 16);
    private static final TexturedRect FIRST_SLOT_END = new TexturedRect(4 * 1.5F, 16 * 1.5F, 44, 0, 4, 16);

    private static final TexturedRect EXTRA_SLOT_START = new TexturedRect(3 * 1.5F, 10 * 1.5F, 15, 16, 3, 10);
    private static final TexturedRect EXTRA_SLOT_REPEAT = new TexturedRect(26 * 1.5F, 10 * 1.5F, 18, 16, 26, 10);
    private static final TexturedRect EXTRA_SLOT_END = new TexturedRect(4 * 1.5F, 10 * 1.5F, 44, 16, 4, 10);

    private static final ResourceLocation OVERLAY_TEXTURE = LootGames.rl("textures/gui/minesweeper/ms_overlay.png");

    public static final Consumer<BufferBuilder> GUI_POS_TEX_SETUP = bufferBuilder -> {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, OVERLAY_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    };

    @SubscribeEvent
    public static void renderOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("lg_ms_overlay", (gui, graphics, partialTick, screenWidth, screenHeight) -> {
            renderNearbyGameBombs(graphics);
            MS_MASTERS.clear();
        });
    }

    private static void renderNearbyGameBombs(GuiGraphics guiGraphics) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        Font fontRenderer = Minecraft.getInstance().font;

        List<MSMasterTile> masters = new ArrayList<>(1);
        Iterator<WeakReference<MSMasterTile>> iterator = MS_MASTERS.iterator();
        while (iterator.hasNext()) {
            MSMasterTile master = iterator.next().get();

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

        boolean extendedInfo = masters.size() > 1;

        float maxRectWidth = 0;
        for (MSMasterTile msMaster : masters) {
            GameMineSweeper game = msMaster.getGame();
            String toDisplay = getBombDisplayString(game, extendedInfo);

            maxRectWidth = Math.max(maxRectWidth, fontRenderer.width(toDisplay) + 5.5F * 2);
        }

        float startY = 20;

        for (int i = 0; i < masters.size(); i++) {
            MSMasterTile msMaster = masters.get(i);
            GameMineSweeper game = msMaster.getGame();

            Color color = game.getStage() instanceof GameMineSweeper.StageDetonating || game.getStage() instanceof GameMineSweeper.StageExploding ? Color.RED : Color.WHITE;
            String toDisplay = getBombDisplayString(game, extendedInfo);

            float finalMaxRectWidth = maxRectWidth;
            if (i == 0) {
                DrawHelper.drawBatched(GUI_POS_TEX_SETUP, buffer -> {
                    DrawHelper.buildTexturedRectByParts(buffer, guiGraphics.pose(), 5, 5, 15 * 1.5F, 16 * 1.5F, 0, 0, 0, 15, 16, 48);
                    DrawHelper.buildWidthExpandableTexturedRect(buffer, guiGraphics.pose(), 5 + 15 * 1.5F, 5, finalMaxRectWidth, 0, FIRST_SLOT_START, FIRST_SLOT_REPEAT, FIRST_SLOT_END, 48);
                });

                DrawHelper.drawYCenteredStringWithShadow(guiGraphics, fontRenderer, toDisplay, 32.6F, 17.5F, color.getRGB());
            } else {
                float finalStartY = startY;

                DrawHelper.drawBatched(GUI_POS_TEX_SETUP, buffer -> {
                    DrawHelper.buildWidthExpandableTexturedRect(buffer, guiGraphics.pose(), 27.5F, finalStartY, finalMaxRectWidth, 0, EXTRA_SLOT_START, EXTRA_SLOT_REPEAT, EXTRA_SLOT_END, 48);
                });

                DrawHelper.drawYCenteredStringWithShadow(guiGraphics, fontRenderer, toDisplay, 32.6F, startY + 8F, color.getRGB());
                startY += 7 * 1.5F;
            }
        }
    }

    private static String getBombDisplayString(GameMineSweeper game, boolean extended) {
        int bombDisplay = game.getStage() instanceof GameMineSweeper.StageDetonating || game.getStage() instanceof GameMineSweeper.StageExploding ? game.getBoard().getBombCount() :
                game.getBoard().getBombCount() - game.getBoard().cGetFlaggedField();
        BlockPos gamePos = game.getGameCenter();
        return extended ? "x" + bombDisplay + " on "/*todo translate */ + gamePos.getX() + ", " + gamePos.getY() + ", " + gamePos.getZ() : "x" + bombDisplay;
    }

    public static void addSupportedMaster(MSMasterTile master) {
        if (!Minecraft.getInstance().options.hideGui || Minecraft.getInstance().screen != null) {
            MS_MASTERS.add(new WeakReference<>(master));
        }
    }
}