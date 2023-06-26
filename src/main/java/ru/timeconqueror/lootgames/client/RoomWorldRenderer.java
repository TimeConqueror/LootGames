package ru.timeconqueror.lootgames.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.client.render.game.MinesweeperRenderer;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.room.client.ClientRoom;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RoomWorldRenderer {
    private static final MinesweeperRenderer minesweeperRenderer = new MinesweeperRenderer();

    @SubscribeEvent
    public static void renderAfterBlockEntities(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            ClientRoom room = ClientRoom.getInstance();
            if (room == null || !(room.getGame() instanceof GameMineSweeper game)) return;
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            minesweeperRenderer.render(game, event.getPartialTick(), event.getPoseStack(), bufferSource);
        }
    }
}
