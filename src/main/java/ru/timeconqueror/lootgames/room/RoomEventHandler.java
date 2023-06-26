package ru.timeconqueror.lootgames.room;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.room.IPlayerRoomData;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.utils.Log;
import ru.timeconqueror.timecore.api.util.PlayerUtils;

import java.util.Objects;

import static ru.timeconqueror.lootgames.api.Markers.ROOM;

@Mod.EventBusSubscriber
public class RoomEventHandler {
    private static final Logger LOGGER = Log.LOG_ROOM_HANDLER;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tickRooms(TickEvent.LevelTickEvent event) {
        if (!event.level.isClientSide && RoomUtils.inRoomWorld(event.level)) {
            ServerRoomStorage.getInstance(event.level)
                    .ifPresent(storage -> {
                        if (event.phase == TickEvent.Phase.END) {
                            storage.tick();
                        } else {
                            storage.getPlayerTaskHelper().tick();
                        }
                    });
        }
    }

    @SubscribeEvent
    public static void onPlayer(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.side == LogicalSide.CLIENT) {
            return;
        }

        ServerPlayer sp = (ServerPlayer) event.player;
        onPlayerMove(sp);

        if (!RoomUtils.inRoomWorld(sp.level())) {
            IPlayerRoomData.of(sp)
                    .ifPresent(data -> data.setLastAllowedCoords(null));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void checkLegality(PlayerInteractEvent event) {
        if (event.isCancelable()) {
            if (!isActionAllowed(event.getEntity(), event.getLevel(), event.getPos())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void checkLegality(EntityItemPickupEvent event) {
        if (!isActionAllowed(event.getEntity(), event.getEntity().level(), event.getItem().blockPosition())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void checkLegality(BlockEvent.BreakEvent event) {
        if (!isActionAllowed(event.getPlayer(), event.getPlayer().level(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void checkLegality(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!isActionAllowed(player, event.getEntity().level(), event.getPos())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void checkLegality(BlockEvent.FluidPlaceBlockEvent event) {
        if (event.getLevel().isClientSide()
                || !(event.getLevel() instanceof Level level)
                || !RoomUtils.inRoomWorld(level)) {
            return;
        }

        BlockPos liquidPos = event.getLiquidPos();
        if (!RoomCoords.isSameRoom(liquidPos, event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void checkLegality(LivingDestroyBlockEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide || !RoomUtils.inRoomWorld(level)) {
            return;
        }

        if (!RoomCoords.isSameRoom(event.getEntity().blockPosition(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    public static void onPlayerMove(ServerPlayer player) {
        if (!RoomUtils.inRoomWorld(player.level())) {
            return;
        }

        IPlayerRoomData data = IPlayerRoomData.of(player).resolve().orElse(null);
        if (data == null) return;

        RoomCoords prevCoords = data.getLastAllowedCoords();
        RoomCoords newCoords = RoomCoords.of(player.blockPosition());
        boolean changedRoom = !Objects.equals(prevCoords, newCoords);
        boolean isAllowedToCheat = PlayerUtils.isCreativeOrOp(player);

        ServerRoomStorage roomStorage = ServerRoomStorage.getInstance(player.level()).orElseThrow();
        if (changedRoom) {
            LOGGER.debug(ROOM, "{} tried to illegally jump from {} to {}", player.getName().getString(), prevCoords, newCoords);
            if (isAllowedToCheat) {
                LOGGER.debug(ROOM, "{} is allowed to jump from {} to {} because he's admin", player.getName().getString(), prevCoords, newCoords);
                roomStorage.leaveRoom(player, prevCoords);

                ServerRoom room = roomStorage.getRoom(newCoords);
                roomStorage.enterRoom(player, room);
            } else {
                LOGGER.debug(ROOM, "{} is denied to jump from {} to {}", player.getName().getString(), prevCoords, newCoords);
                if (!roomStorage.teleportToRoom(player, prevCoords)) {
                    roomStorage.teleportAway(player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !RoomUtils.inRoomWorld(event.getLevel())) {
            return;
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            if (!authorizePlayer(player)) {
                event.setCanceled(true);
                ServerRoomStorage.getInstance(player.level())
                        .map(ServerRoomStorage::getPlayerTaskHelper)
                        .ifPresent(helper -> helper.delayTeleportAway(player));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityRemovedFromLevel(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide || !RoomUtils.inRoomWorld(event.getLevel())) {
            return;
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            ServerRoomStorage storage = ServerRoomStorage.getInstance(event.getLevel()).orElseThrow();
            RoomCoords coords = RoomCoords.of(player.blockPosition());
            LOGGER.debug(ROOM, "{} removed from room world in {}", player.getName().getString(), coords);
            storage.leaveRoom(player, coords);
        }
    }

    private static boolean authorizePlayer(ServerPlayer player) {
        if (!RoomUtils.inRoomWorld(player.level())) {
            return true;
        }

        ServerRoomStorage roomStorage = ServerRoomStorage.getInstance(player.level()).orElseThrow();
        RoomCoords coords = RoomCoords.of(player.blockPosition());

        IPlayerRoomData playerRoomData = IPlayerRoomData.of(player).resolve().orElse(null);
        if (playerRoomData == null) return false;

        RoomCoords lastAllowedCoords = playerRoomData.getLastAllowedCoords();

        LOGGER.debug(ROOM, "{} tried to enter the room world in {}", player.getName().getString(), coords);
        ServerRoom room = roomStorage.getRoom(coords);
        if (Objects.equals(lastAllowedCoords, coords) || room.isPendingToEnter(player) || PlayerUtils.isCreativeOrOp(player)) {
            roomStorage.enterRoom(player, room);
            return true;
        } else {
            LOGGER.debug(ROOM, "{} is disallowed to enter the room {}", player.getName().getString(), coords);
            return false;
        }
    }

//   FIXME public static void transferPlayerDrops(LivingDropsEvent event) {}

    private static boolean isActionAllowed(Player player, Level level) {
        return isActionAllowed(player, level, null);
    }

    private static boolean isActionAllowed(Player player, Level level, @Nullable BlockPos actionPos) {
        if (level.isClientSide || !RoomUtils.inRoomWorld(player.level())) {
            return true;
        }

        return actionPos == null || RoomCoords.isSameRoom(actionPos, player.blockPosition());
    }
}
