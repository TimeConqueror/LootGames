package ru.timeconqueror.lootgames.client.debug;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.room.RoomCoords;

import java.util.ArrayList;
import java.util.List;

import static ru.timeconqueror.lootgames.client.debug.RoomDebugRenderer.isInRoomWorld;
import static ru.timeconqueror.lootgames.utils.Log.DEBUGGER;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RoomDebugScreenOverlay extends GuiComponent implements IGuiOverlay {
    public static final RoomDebugScreenOverlay INSTANCE = new RoomDebugScreenOverlay();
    public boolean renderDebugInfo;

    @SubscribeEvent
    public static void renderOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("ms_overlay", INSTANCE);
    }

    public void switchRenderDebugInfo() {
        renderDebugInfo = !renderDebugInfo;
        DEBUGGER.info("Render Debug Info: {}", renderDebugInfo);
    }

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (!renderDebugInfo || !isInRoomWorld(mc)) return;

        Font font = mc.font;

        List<String> lines = new ArrayList<>();
        lines.add("LootGames Debugger");
        fillDebugInfo(lines, mc);
        lines.add("To close: F6 + I");

        for (int yIndex = 0; yIndex < lines.size(); ++yIndex) {
            String line = lines.get(yIndex);
            if (!Strings.isNullOrEmpty(line)) {
                int height = font.lineHeight;
                int width = font.width(line);
                int l = 2;
                int y = l + height * yIndex;
                fill(poseStack, 1, y - 1, 2 + width + 1, y + height - 1, 0x90505050);
                font.draw(poseStack, line, 2.0F, (float) y, 0xe0e0e0);
            }
        }
    }

    private void fillDebugInfo(List<String> debugInfo, Minecraft mc) {
        RoomCoords coords = getRoomCoords(mc);

        debugInfo.add("Room Coords: " + (coords != null ? coords.x() + ", " + coords.z() : "<unable>"));
    }

    @Nullable
    private RoomCoords getRoomCoords(Minecraft mc) {
        LocalPlayer player = mc.player;
        return player != null ? RoomCoords.of(player.blockPosition()) : null;
    }
}
