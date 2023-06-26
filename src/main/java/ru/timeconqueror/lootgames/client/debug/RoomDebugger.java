package ru.timeconqueror.lootgames.client.debug;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGDimensions;
import ru.timeconqueror.lootgames.room.RoomUtils;
import ru.timeconqueror.lootgames.room.ServerRoom;
import ru.timeconqueror.lootgames.room.ServerRoomStorage;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RoomDebugger {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key keyInput) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !RoomUtils.inRoomWorld(mc.level)) return;

        if (keyInput.getAction() == InputConstants.PRESS
                && InputConstants.isKeyDown(mc.getWindow().getWindow(), InputConstants.KEY_F6)) {
            if (keyInput.getKey() == InputConstants.KEY_R) {
                RoomDebugRenderer.switchRenderRoomBorders();
            } else if (keyInput.getKey() == InputConstants.KEY_I) {
                RoomDebugScreenOverlay.INSTANCE.switchRenderDebugInfo();
            } else if (keyInput.getKey() == InputConstants.KEY_C) {
                System.out.println("Executed");
                ServerLevel level = ServerLifecycleHooks.getCurrentServer().getLevel(LGDimensions.TEST_SITE_DIM);
                ServerRoomStorage serverRoomStorage = ServerRoomStorage.getInstance(level)
                        .get();
                ServerRoom room = serverRoomStorage.getRoom(new RoomCoords(0, 0));
                ChunkPos chunkPos = room.getCoords().containerPos();
                System.out.println(chunkPos);
                level.getChunk(chunkPos.x, chunkPos.z).setUnsaved(true);

                System.out.println(room.getPlayers());
            }
        }
    }
}
