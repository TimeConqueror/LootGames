package ru.timeconqueror.lootgames.room;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
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
import ru.timeconqueror.lootgames.api.room.IServerPlayerRoomData;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGDimensions;
import ru.timeconqueror.lootgames.utils.Log;
import ru.timeconqueror.timecore.api.util.PlayerUtils;

import java.util.Objects;

import static ru.timeconqueror.lootgames.api.Markers.ROOM;

@Mod.EventBusSubscriber
public class RoomEventHandler {
    private static final Logger LOGGER = Log.LOG_ROOM_HANDLER;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tickRooms(TickEvent.LevelTickEvent event) {
        if (!event.level.isClientSide && event.level.dimension() == LGDimensions.TEST_SITE_DIM) {
            if (event.phase == TickEvent.Phase.END) {
                RoomStorage.getInstance().tick();
            } else {
                RoomStorage.getInstance().getPlayerTaskHelper().tick();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayer(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.side == LogicalSide.CLIENT) {
            return;
        }

        onPlayerMove((ServerPlayer) event.player);
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
        if (!isActionAllowed(event.getEntity(), event.getEntity().level, event.getItem().blockPosition())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void checkLegality(BlockEvent.BreakEvent event) {
        if (!isActionAllowed(event.getPlayer(), event.getPlayer().level, event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void checkLegality(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!isActionAllowed(player, event.getEntity().level, event.getPos())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void checkLegality(BlockEvent.FluidPlaceBlockEvent event) {
        if (event.getLevel().isClientSide()
                || !(event.getLevel() instanceof Level level)
                || level.dimension() != LGDimensions.TEST_SITE_DIM) {
            return;
        }

        BlockPos liquidPos = event.getLiquidPos();
        if (!RoomCoords.isSameRoom(liquidPos, event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void checkLegality(LivingDestroyBlockEvent event) {
        Level level = event.getEntity().getLevel();
        if (level.isClientSide || level.dimension() != LGDimensions.TEST_SITE_DIM) {
            return;
        }

        if (!RoomCoords.isSameRoom(event.getEntity().blockPosition(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    public static void onPlayerMove(ServerPlayer player) {
        if (player.level.dimension() != LGDimensions.TEST_SITE_DIM) {
            return;
        }

        IServerPlayerRoomData playerRoomData = IServerPlayerRoomData.of(player);
        RoomCoords prevCoords = playerRoomData.getLastRoomCoords();
        RoomCoords newCoords = RoomCoords.of(player.blockPosition());
        boolean changedRoom = !Objects.equals(prevCoords, newCoords);
        boolean isAllowedToCheat = PlayerUtils.isCreativeOrOp(player);

        RoomStorage roomStorage = RoomStorage.getInstance();
        if (changedRoom) {
            LOGGER.debug(ROOM, "{} tried to illegally jump from {} to {}", player.getName().getString(), prevCoords, newCoords);
            if (isAllowedToCheat) {
                LOGGER.debug(ROOM, "{} is allowed to jump from {} to {} because he's admin", player.getName().getString(), prevCoords, newCoords);
                roomStorage.leaveRoom(player, prevCoords);

                Room room = roomStorage.getRoom(newCoords);
                roomStorage.enterRoom(player, room);

                playerRoomData.setLastRoomCoords(newCoords);
            } else {
                LOGGER.debug(ROOM, "{} is denied to jump from {} to {}", player.getName().getString(), prevCoords, newCoords);
                if (!roomStorage.teleportToRoom(player, prevCoords)) {
                    roomStorage.teleportAway(player);
                }
            }
        }

        if (prevCoords == null) playerRoomData.setLastRoomCoords(newCoords);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || event.getLevel().dimension() != LGDimensions.TEST_SITE_DIM) {
            return;
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            if (!onPlayerUpdateStatus(player, true)) {
                event.setCanceled(true);
                RoomStorage.getInstance().getPlayerTaskHelper().delayTeleportAway(player);
            }
        }
    }

    public static boolean onPlayerUpdateStatus(ServerPlayer player, boolean added) {
        if (player.level.dimension() != LGDimensions.TEST_SITE_DIM) {
            return true;
        }

        RoomStorage roomStorage = RoomStorage.getInstance();
        RoomCoords coords = RoomCoords.of(player.blockPosition());
        IServerPlayerRoomData.of(player).setLastRoomCoords(coords);

        if (added) {
            LOGGER.debug(ROOM, "{} tried to enter the room world in {}", player.getName().getString(), coords);
            Room room = roomStorage.getRoom(coords);
            if (room.isAllowedToEnter(player) || PlayerUtils.isCreativeOrOp(player)) {
                roomStorage.enterRoom(player, room);
            } else {
                LOGGER.debug(ROOM, "{} is disallowed to enter the room {}", player.getName().getString(), coords);
                return false;
            }
        } else {
            LOGGER.debug(ROOM, "{} removed from room world in {}", player.getName().getString(), coords);
            roomStorage.leaveRoom(player, coords);
        }

        return true;
    }

//   FIXME public static void transferPlayerDrops(LivingDropsEvent event) {}

    private static boolean isActionAllowed(Player player, Level level) {
        return isActionAllowed(player, level, null);
    }

    private static boolean isActionAllowed(Player player, Level level, @Nullable BlockPos actionPos) {
        if (level.isClientSide || player.level.dimension() != LGDimensions.TEST_SITE_DIM) {
            return true;
        }

        return actionPos == null || RoomCoords.isSameRoom(actionPos, player.blockPosition());
    }
}
