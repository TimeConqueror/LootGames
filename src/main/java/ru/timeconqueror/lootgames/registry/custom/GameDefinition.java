package ru.timeconqueror.lootgames.registry.custom;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.GameEnvironmentGenerator;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomLocalPos;
import ru.timeconqueror.lootgames.minigame.EmptyGameEnvironmentGenerator;

import java.util.Objects;
import java.util.function.Function;

@Builder
public class GameDefinition {
    @Getter
    @NonNull
    private final String langKey;
    @NonNull
    private final Function<Room, ? extends LootGame<?>> gameInstanceCreator;
    @Getter
    @NonNull
    private final Lazy<Item> microverseHolderItem;
    @Getter
    @NonNull
    @Builder.Default
    private GameEnvironmentGenerator generator = EmptyGameEnvironmentGenerator.INSTANCE;
    @Getter
    @NonNull
    private final RoomLocalPos teleportPos;

    public static String serialize(GameDefinition id) {
        ResourceLocation location = id.toKey();
        Objects.requireNonNull(location);
        return location.toString();
    }

    @Nullable
    public static GameDefinition deserialize(String location) {
        GameDefinition id = LootGames.getGameDefinitionRegistry().unwrapped().getValue(new ResourceLocation(location));
        if (id == null) {
            LootGames.LOGGER.warn("Can't find game factory for {}", location);
            return null;
        }

        return id;
    }

    public LootGame<?> createGame(Room room) {
        return gameInstanceCreator.apply(room);
    }

    public ResourceLocation toKey() {
        return LootGames.getGameDefinitionRegistry().unwrapped().getKey(this);
    }

    @Override
    public String toString() {
        return String.valueOf(toKey());
    }
}