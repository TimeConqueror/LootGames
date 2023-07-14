package ru.timeconqueror.lootgames.registry.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.registry.LGRegistries;
import ru.timeconqueror.timecore.api.util.RandHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameInfoRegistry {
    private final List<ResourceLocation> keysById;

    public GameInfoRegistry(IForgeRegistryInternal<GameInfo> forgeRegistry) {
        keysById = new ArrayList<>(forgeRegistry.getKeys());
    }

    public int getSize() {
        return keysById.size();
    }

    public ResourceLocation getById(int index) {
        return keysById.get(index);
    }

    public IForgeRegistry<GameInfo> getRegistry() {
        return LGRegistries.forgeGameInfoRegistry();
    }

    public ResourceLocation getRandom() {
        return getById(RandHelper.RAND.nextInt(getSize()));
    }

    public LootGame<?> makeRandomGame(Room room) {
        ResourceLocation key = getRandom();
        IForgeRegistry<GameInfo> forgeRegistry = LGRegistries.forgeGameInfoRegistry();
        GameInfo info = forgeRegistry.getValue(key);
        Objects.requireNonNull(info);
        return info.createGame(key, room);
    }
}
