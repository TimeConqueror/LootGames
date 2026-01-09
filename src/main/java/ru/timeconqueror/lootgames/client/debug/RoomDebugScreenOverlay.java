package ru.timeconqueror.lootgames.client.debug;

import com.google.common.base.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.FlatBoardGame;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.api.room.RoomLocalPos;
import ru.timeconqueror.lootgames.api.room.RoomProgress;
import ru.timeconqueror.lootgames.room.RoomUtils;
import ru.timeconqueror.lootgames.room.client.ClientRoom;

import java.util.ArrayList;
import java.util.List;

import static ru.timeconqueror.lootgames.utils.Log.DEBUGGER;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RoomDebugScreenOverlay implements IGuiOverlay {
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
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (!renderDebugInfo || !RoomUtils.inRoomWorld(mc.level)) return;

        Font font = mc.font;

        List<String> lines = new ArrayList<>();
        lines.add("LootGames Debugger");
        fillDebugInfo(lines, mc);
        lines.add("");
        lines.add("To close: F6 + I");

        for (int yIndex = 0; yIndex < lines.size(); ++yIndex) {
            String line = lines.get(yIndex);
            if (!Strings.isNullOrEmpty(line)) {
                int height = font.lineHeight;
                int width = font.width(line);
                int l = 2;
                int y = l + height * yIndex;
                graphics.fill(1, y - 1, 2 + width + 1, y + height - 1, 0x90505050);
                graphics.drawString(font, line, 2, y, 0xe0e0e0);
            }
        }
    }

    private void fillDebugInfo(List<String> debugInfo, Minecraft mc) {
        LocalPlayer player = mc.player;

        HitResult hitResult = player.pick(20.0D, 0.0F, false);

        RoomCoords coords = getRoomCoords(player);
        debugInfo.add("Room Coords: " + (coords != null ? coords.x() + ", " + coords.z() : "<unable>"));
        RoomLocalPos localPos = getRoomLocalPos(player);
        debugInfo.add("Room Local Pos: " + toString(localPos));

        if(hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            RoomLocalPos pos = RoomLocalPos.of(blockHitResult.getBlockPos());
            debugInfo.add("Room Local Pos (Target): " + toString(pos));
        } else {
            debugInfo.add("");
        }

        debugInfo.add("");
        ClientRoom room = ClientRoom.getInstance();
        if (room != null) {
            debugInfo.add("Room Status: " + room.getProgress());

            LootGame<?> game = room.getGame();
            debugInfo.add("Game: " + (game != null ? game.getGameDefinition() : "None"));
            if (game != null) {
                debugInfo.add("Progress: " + game.getProgress());
                debugInfo.add("Stage: " + game.getStage().getID());
                if(game instanceof FlatBoardGame flatBoardGame) {
                    debugInfo.add("Local Board Origin: " + toString(RoomLocalPos.of(flatBoardGame.getBoardOrigin())));
                    debugInfo.add("Size: " + flatBoardGame.getBoardSize());
                }
            }
        }
    }

    private String toString(RoomLocalPos localPos) {
        return (localPos != null ? localPos.getX() + ", " + localPos.getY()  + ", " + localPos.getZ() : "<unable>");
    }

    @Nullable
    private RoomCoords getRoomCoords(@Nullable LocalPlayer player) {
        return player != null ? RoomCoords.of(player.blockPosition()) : null;
    }

    @Nullable
    private RoomLocalPos getRoomLocalPos(@Nullable LocalPlayer player) {
        RoomCoords coords = getRoomCoords(player);
        if(player == null || coords == null) {
            return null;
        }

        return RoomLocalPos.of(coords, player.blockPosition());
    }
}
