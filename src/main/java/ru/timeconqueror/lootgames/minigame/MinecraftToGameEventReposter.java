package ru.timeconqueror.lootgames.minigame;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.api.minigame.event.BlockClickEvent;
import ru.timeconqueror.lootgames.api.minigame.event.GameEvents;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.room.RoomUtils;
import ru.timeconqueror.lootgames.utils.MouseClickType;

@Mod.EventBusSubscriber
public class MinecraftToGameEventReposter {
    @SubscribeEvent
    public static void onBlockBreak(PlayerInteractEvent.LeftClickBlock event) {
        if (onPlayerClicked(event.getLevel(), event.getEntity(), event.getPos(), event.getHand(), event.getFace(), MouseClickType.LEFT)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(PlayerInteractEvent.RightClickBlock event) {
        if (onPlayerClicked(event.getLevel(), event.getEntity(), event.getPos(), event.getHand(), event.getFace(), MouseClickType.RIGHT)) {
            event.setCanceled(true);
        }
    }

    private static boolean onPlayerClicked(Level level, Player player, BlockPos pos, InteractionHand hand, Direction face, MouseClickType type) {
        RoomCoords coords = RoomCoords.of(pos);
        Room room = RoomUtils.getLoadedRoom(level, coords);
        if (room != null && room.getGame() != null) {
            BlockState blockState = level.getBlockState(pos);
            var gameEvent = new BlockClickEvent(level, player, blockState, coords.toRelative(pos), hand, face, type);
            room.getGame().getEventBus().post(GameEvents.CLICK_BLOCK, gameEvent);
            return gameEvent.isAccepted();
        }

        return false;
    }
}
