package ru.timeconqueror.lootgames.registry.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.registry.LGRegistries;
import ru.timeconqueror.timecore.api.util.RandHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameInfoRegistry {
    private final List<GameInfo> values;
    private final Map<Item, GameInfo> itemsToGameInfos;

    public GameInfoRegistry(IForgeRegistryInternal<GameInfo> forgeRegistry) {
        values = new ArrayList<>(forgeRegistry.getValues());
        itemsToGameInfos = values.stream()
                .collect(ImmutableMap.toImmutableMap(gameInfo -> gameInfo.getMicroverseHolderItem().get(), Function.identity()));
    }

    public int getSize() {
        return values.size();
    }

    public Item getHolderByGameInfo(GameInfo gameInfo) {
        return gameInfo.getMicroverseHolderItem().get();
    }

    @Nullable
    public GameInfo getGameInfoByHolder(Item holder) {
        return itemsToGameInfos.get(holder);
    }

    public IForgeRegistry<GameInfo> unwrapped() {
        return LGRegistries.forgeGameInfoRegistry();
    }

    public GameInfo getRandom() {
        return getByIndex(RandHelper.RAND.nextInt(getSize()));
    }

    private GameInfo getByIndex(int index) {
        return values.get(index);
    }
}
