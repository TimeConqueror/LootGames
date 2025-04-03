package ru.timeconqueror.lootgames.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.client.render.game.GameOfLightRenderer;
import ru.timeconqueror.lootgames.client.render.game.MinesweeperRenderer;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.LGGames;
import ru.timeconqueror.lootgames.room.RoomUtils;
import ru.timeconqueror.lootgames.room.client.ClientRoom;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RoomWorldRenderer {
    private static final MinesweeperRenderer MINESWEEPER_RENDERER = new MinesweeperRenderer();
    private static final GameOfLightRenderer GAME_OF_LIGHT_RENDERER = new GameOfLightRenderer();

    @SubscribeEvent
    public static void renderAfterBlockEntities(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            ClientLevel level = Minecraft.getInstance().level;
            ClientRoom room = ClientRoom.getInstance();
            if (!RoomUtils.inRoomWorld(level) || room == null || room.getGame() == null)
                return;
            LootGame<?> game = room.getGame();
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

            PoseStack poseStack = event.getPoseStack();

            poseStack.pushPose();
            {
                Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
                Vec3 cameraPos = camera.getPosition();
                poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());

                if (game.getGameDefinition() == LGGames.MINESWEEPER.get()) {
                    MINESWEEPER_RENDERER.render((GameMineSweeper) game, poseStack, bufferSource, event.getPartialTick());
                } else if (game.getGameDefinition() == LGGames.GAME_OF_LIGHT.get()) {
                    GAME_OF_LIGHT_RENDERER.render((GameOfLight) game, poseStack, bufferSource, event.getPartialTick());
                }
            }
            poseStack.popPose();
        }
    }
}
