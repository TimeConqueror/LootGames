package ru.timeconqueror.lootgames.client.debug;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.registry.LGDimensions;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RoomDebugger {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key keyInput) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.level.dimension() != LGDimensions.TEST_SITE_DIM) return;

        if (keyInput.getAction() == InputConstants.PRESS
                && InputConstants.isKeyDown(mc.getWindow().getWindow(), InputConstants.KEY_F6)) {
            if (keyInput.getKey() == InputConstants.KEY_R) {
                RoomDebugRenderer.switchRenderRoomBorders();
            } else if (keyInput.getKey() == InputConstants.KEY_I) {
                RoomDebugScreenOverlay.INSTANCE.switchRenderDebugInfo();
            }
        }
    }
}
