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

    Level getLevel();

    @Nullable LootGame<?> getGame();

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
