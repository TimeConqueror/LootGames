package ru.timeconqueror.lootgames.minigame;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.api.minigame.event.GameEvents;
import ru.timeconqueror.lootgames.api.minigame.event.LeftClickBlockEvent;
import ru.timeconqueror.lootgames.api.minigame.event.RightClickBlockEvent;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomAccess;
import ru.timeconqueror.lootgames.api.room.RoomCoords;

@Mod.EventBusSubscriber
public class MinecraftToGameEventReposter {
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (onPlayerLeftClicked(event.getLevel(), event.getEntity(), event.getPos(), event.getHand(), event.getFace(), event.getAction())) {
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (onPlayerRightClicked(event.getLevel(), event.getEntity(), event.getPos(), event.getHand(), event.getFace())) {
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
            event.setCanceled(true);
        }
    }

    private static boolean onPlayerRightClicked(Level level, Player player, BlockPos pos, InteractionHand hand, Direction face) {
        RoomCoords coords = RoomCoords.of(pos);

        Room room = RoomAccess.getRoom(coords, level.isClientSide());
        if (room != null && room.getGame() != null) {
            BlockState blockState = level.getBlockState(pos);
            var gameEvent = new RightClickBlockEvent(level, player, blockState, coords.toLocalPos(pos), hand, face);
            room.getGame().getEventBus().post(GameEvents.RIGHT_CLICK_BLOCK, gameEvent);
            return gameEvent.isAccepted();
        }

        return false;
    }

    private static boolean onPlayerLeftClicked(Level level, Player player, BlockPos pos, InteractionHand hand, Direction face, PlayerInteractEvent.LeftClickBlock.Action action) {
        RoomCoords coords = RoomCoords.of(pos);

        Room room = RoomAccess.getRoom(coords, level.isClientSide());
        if (room != null && room.getGame() != null) {
            BlockState blockState = level.getBlockState(pos);
            var gameEvent = new LeftClickBlockEvent(level, player, blockState, coords.toLocalPos(pos), hand, face, action);
            room.getGame().getEventBus().post(GameEvents.LEFT_CLICK_BLOCK, gameEvent);
            return gameEvent.isAccepted();
        }

        return false;
    }
}
