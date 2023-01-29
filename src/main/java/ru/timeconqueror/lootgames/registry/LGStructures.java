package ru.timeconqueror.lootgames.registry;

import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.common.world.gen.GameDungeonStructure;
import ru.timeconqueror.timecore.api.registry.StructureRegister;
import ru.timeconqueror.timecore.api.registry.StructureRegister.StructureHolder;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class LGStructures {
    @AutoRegistrable
    private static final StructureRegister REGISTER = new StructureRegister(LootGames.MODID);

    public static StructureHolder<NoneFeatureConfiguration, GameDungeonStructure> GAME_DUNGEON =
            REGISTER.register("game_dungeon",
                    GameDungeonStructure::new,
                    StructureRegister.TimeStructureSeparationSettings.create(10, 6),
                    NoneFeatureConfiguration.CODEC,
                    NoneFeatureConfiguration.INSTANCE
            )
                    .setDimensionPredicate(serverWorld -> !LGConfigs.GENERAL.worldGen.disableDungeonGen.get())
                    .asHolder();
}
