package ru.timeconqueror.lootgames.api.room;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

import java.util.List;
import java.util.function.Consumer;

public interface Room {
    RoomCoords getCoords();

    AABB getRoomBox();

    List<Player> getPlayers();

    @Nullable
    default Player getRandomPlayer() {
        List<Player> players = getPlayers();
        if (players.isEmpty()) return null;
        return players.get(0);
    }

    Level getLevel();

    /**
     * Returns the game currently played in this room.
     * Game is not null only if <pre>this.getProgress() == GameProgress.STARTED</pre>
     */
    @Nullable LootGame<?> getGame();

    RoomProgress getProgress();

    default boolean isGenerated() {
        return getProgress() != RoomProgress.NOT_GENERATED;
    }

    default boolean contains(BlockPos pos) {
        return getCoords().contains(pos);
    }

    default void forEachInRoom(Consumer<Player> action) {
        for (Player player : getPlayers()) {
            action.accept(player);
        }
    }

    default void syncGame() {

    }

    void tick();
}
