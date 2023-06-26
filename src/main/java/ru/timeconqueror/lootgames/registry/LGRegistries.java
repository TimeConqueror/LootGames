package ru.timeconqueror.lootgames.registry;

import lombok.extern.log4j.Log4j2;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.registry.custom.GameInfo;
import ru.timeconqueror.lootgames.registry.custom.GameInfoRegistry;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Log4j2
public class LGRegistries {
    private static Supplier<IForgeRegistry<GameInfo>> GAMES;

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent e) {
        GAMES = e.create(new RegistryBuilder<GameInfo>()
                        .setName(LootGamesAPI.GAME_REGISTRY.location())
                        .onBake((owner, stage) -> {
                            log.debug("Recalculating internals...");
                            recalculateInternals(owner);
                        })
                        .disableSync(),
                reg -> {
                }
        );
    }

    private static void recalculateInternals(IForgeRegistryInternal<GameInfo> owner) {
        LootGames.setGameInfoRegistry(new GameInfoRegistry(owner));
    }

    public static IForgeRegistry<GameInfo> forgeGameInfoRegistry() {
        return GAMES.get();
    }
}
