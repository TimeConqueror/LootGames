package ru.timeconqueror.lootgames.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomAccess;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.timecore.api.util.client.DrawHelper;
import ru.timeconqueror.timecore.api.util.client.DrawHelper.TexturedRect;

import java.awt.*;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MSOverlayHandler {
    private static final TexturedRect FIRST_SLOT_START = new TexturedRect(3 * 1.5F, 16 * 1.5F, 15, 0, 3, 16);
    private static final TexturedRect FIRST_SLOT_REPEAT = new TexturedRect(26 * 1.5F, 16 * 1.5F, 18, 0, 26, 16);
    private static final TexturedRect FIRST_SLOT_END = new TexturedRect(4 * 1.5F, 16 * 1.5F, 44, 0, 4, 16);

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
        });
    }

    private static void renderNearbyGameBombs(GuiGraphics guiGraphics) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        Room room = RoomAccess.getLoadedRoom(player.clientLevel, RoomCoords.of(player.blockPosition()));
        if (room == null || !(room.getGame() instanceof GameMineSweeper game)) {
            return;
        }
        Font fontRenderer = Minecraft.getInstance().font;
        Color color = game.getStage() instanceof GameMineSweeper.StageDetonating || game.getStage() instanceof GameMineSweeper.StageExploding ? Color.RED : Color.WHITE;
        String toDisplay = getBombDisplayString(game);

        DrawHelper.drawBatched(GUI_POS_TEX_SETUP, buffer -> {
            DrawHelper.buildTexturedRectByParts(buffer, guiGraphics.pose(), 5, 5, 15 * 1.5F, 16 * 1.5F, 0, 0, 0, 15, 16, 48);
            DrawHelper.buildWidthExpandableTexturedRect(buffer, guiGraphics.pose(), 5 + 15 * 1.5F, 5, fontRenderer.width(toDisplay) + 5.5F * 2, 0, FIRST_SLOT_START, FIRST_SLOT_REPEAT, FIRST_SLOT_END, 48);
        });

        DrawHelper.drawYCenteredStringWithShadow(guiGraphics, fontRenderer, toDisplay, 32.6F, 17.5F, color.getRGB());
    }

    private static String getBombDisplayString(GameMineSweeper game) {
        int bombDisplay = game.getStage() instanceof GameMineSweeper.StageDetonating || game.getStage() instanceof GameMineSweeper.StageExploding ? game.getBoard().getBombCount() :
                game.getBoard().getBombCount() - game.getBoard().cGetFlaggedField();
        return "x" + bombDisplay;
    }
}