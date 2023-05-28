package ru.timeconqueror.lootgames.room;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import ru.timeconqueror.lootgames.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DelayedPlayerTaskHelper {
    private final List<Task> delayedTasks = new ArrayList<>();

    public void delayTeleportAway(ServerPlayer playerIn) {
        UUID uuid = playerIn.getUUID();
        delayedTasks.add(new Task(0, () -> {
            ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
            if (player != null) {
                if (!player.isRemoved()) {
                    Log.LOG_ROOM_HANDLER.info("{} was teleported back to his respawn.", player.getName().getString());
                    RoomStorage.getInstance().teleportAway(player);
                }
            }
        }));
    }

    public void tick() {
        delayedTasks.removeIf(task -> {
            int ticks = task.getTicks();
            if (ticks <= 0) {
                task.run();
                return true;
            }
            task.setTicks(--ticks);
            return false;
        });
    }

    private static class Task {
        private int ticks;
        private final Runnable runnable;

        private Task(int ticks, java.lang.Runnable runnable) {
            this.ticks = ticks;
            this.runnable = runnable;
        }

        public int getTicks() {
            return ticks;
        }

        public void setTicks(int ticks) {
            this.ticks = ticks;
        }

        public void run() {
            this.runnable.run();
        }
    }
}
