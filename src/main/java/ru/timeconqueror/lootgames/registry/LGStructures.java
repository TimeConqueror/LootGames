package ru.timeconqueror.lootgames.registry;

import net.minecraft.world.gen.feature.NoFeatureConfig;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.world.gen.GameDungeonStructure;
import ru.timeconqueror.timecore.api.registry.StructureRegister;
import ru.timeconqueror.timecore.api.registry.StructureRegister.StructureHolder;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

////TODO move to TimeCore registry system
//@TimeAutoRegistrable(target = TimeAutoRegistrable.Target.CLASS)
public class LGStructures {
    @AutoRegistrable
    private static final StructureRegister REGISTER = new StructureRegister(LootGames.MODID);

    public static StructureHolder<NoFeatureConfig, GameDungeonStructure> GAME_DUNGEON =
            REGISTER.register("game_dungeon",
                    GameDungeonStructure::new,
                    StructureRegister.TimeStructureSeparationSettings.create(10, 5),
                    NoFeatureConfig.CODEC,
                    NoFeatureConfig.INSTANCE
            )
                    .asHolder();

//    public static final GameDungeonStructure GAME_DUNGEON = new GameDungeonStructure(NoFeatureConfig::deserialize);
//
//    public static IStructurePieceType GD_PIECE_MAIN_ROOM;
//
//    /**
//     * Register features
//     * Structures are a type of feature so they get registered here too
//     */
//    @SubscribeEvent
//    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
//        //Using the registry directly like this is bad, however this is currently the only way to register a structure piece
//        //Specifically, this is to avoid the error that occurs in StructurePiece.write()
//        //The piece's registry id is saved to nbt, and without the structure will not be saved properly and users will experience errors in the console whenever the chunk is loaded or unloaded
//        GD_PIECE_MAIN_ROOM = Registry.register(Registry.STRUCTURE_PIECE, LootGames.MODID + ":game_dungeon/main_room", GameDungeonPieces.MainRoom::new);
//
//        GAME_DUNGEON.setRegistryName(LootGames.MODID, "game_dungeon");
//        event.getRegistry().register(GAME_DUNGEON);
//    }
//
//    /**
//     * Add structures and features to biomes
//     * Should be called after the registry events have run, so that all biomes and features already exist
//     * Using categories like this is just one way of choosing which biome to place a feature in
//     * It is important that each structure be added both as a feature and as a structure
//     */
//    @SubscribeEvent
//    public static void applyFeatures(FMLCommonSetupEvent event) {
//        for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
//            biome.addStructure(GAME_DUNGEON, IFeatureConfig.NO_FEATURE_CONFIG);
//            /*
//             * Structures should be added as features to any biome that they might overlap into, to avoid generation being cut off at chunk edges
//             * Structures can be added as structures to any biome they should start in
//             *
//             * VEGETAL_DECORATION is the last stage
//             */
//            biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(GAME_DUNGEON, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
//        }
//    }
}
