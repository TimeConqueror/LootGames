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
import java.util.function.Function;

public class GameDefinitionRegistry {
    private final List<GameDefinition> values;
    private final Map<Item, GameDefinition> itemsToGameDefinitions;

    public GameDefinitionRegistry(IForgeRegistryInternal<GameDefinition> forgeRegistry) {
        values = new ArrayList<>(forgeRegistry.getValues());
        itemsToGameDefinitions = values.stream()
                .collect(ImmutableMap.toImmutableMap(def -> def.getMicroverseHolderItem().get(), Function.identity()));
    }

    public int getSize() {
        return values.size();
    }

    public Item getHolderByDefinition(GameDefinition gameDefinition) {
        return gameDefinition.getMicroverseHolderItem().get();
    }

    @Nullable
    public GameDefinition getDefinitionByHolder(Item holder) {
        return itemsToGameDefinitions.get(holder);
    }

    public boolean isGameHolder(Item holder) {
        return getDefinitionByHolder(holder) != null;
    }

    public IForgeRegistry<GameDefinition> unwrapped() {
        return LGRegistries.forgeGameDefinitionRegistry();
    }

    public GameDefinition getRandom() {
        return getByIndex(RandHelper.RAND.nextInt(getSize()));
    }

    private GameDefinition getByIndex(int index) {
        return values.get(index);
    }
}
