package ru.timeconqueror.lootgames.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.client.render.game.MinesweeperRenderer;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.LGGames;
import ru.timeconqueror.lootgames.room.RoomUtils;
import ru.timeconqueror.lootgames.room.client.ClientRoom;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RoomWorldRenderer {
    private static final MinesweeperRenderer minesweeperRenderer = new MinesweeperRenderer();

    @SubscribeEvent
    public static void renderAfterBlockEntities(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            ClientLevel level = Minecraft.getInstance().level;
            ClientRoom room = ClientRoom.getInstance();
            if (level == null || !RoomUtils.inRoomWorld(level) || room == null || room.getGame() == null || !room.getGame().isStarted())
                return;
            LootGame<?> game = room.getGame();
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            if (game.getId().equals(LGGames.MINESWEEPER)) {
                minesweeperRenderer.render((GameMineSweeper) game, event.getPoseStack(), bufferSource, event.getPartialTick());
            }
        }
    }
}
