//package ru.timeconqueror.lootgames.common.world.gen;
//
//import net.minecraft.util.registry.Registry;
//import net.minecraft.world.biome.Biome;
//import net.minecraft.world.gen.GenerationStage;
//import net.minecraft.world.gen.feature.Feature;
//import net.minecraft.world.gen.feature.IFeatureConfig;
//import net.minecraft.world.gen.feature.NoFeatureConfig;
//import net.minecraft.world.gen.feature.structure.IStructurePieceType;
//import net.minecraft.world.gen.feature.structure.Structure;
//import net.minecraft.world.gen.placement.IPlacementConfig;
//import net.minecraft.world.gen.placement.Placement;
//import net.minecraftforge.event.RegistryEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
//import net.minecraftforge.registries.ForgeRegistries;
//import ru.timeconqueror.lootgames.LootGames;
//
//@Mod.EventBusSubscriber(modid = LootGames.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//public class StructureRegistry {
//
//	public static final GameDungeonStructure GAME_DUNGEON = new GameDungeonStructure(NoFeatureConfig::deserialize);
//
//	public static IStructurePieceType GAME_DUNGEON_PIECE;
//
//	/**
//	 * Register features
//	 * Structures are a type of feature so they get registered here too
//	 */
//	@SubscribeEvent
//	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
//		//Using the registry directly like this is bad, however this is currently the only way to register a structure piece
//		//Specifically, this is to avoid the error that occurs in StructurePiece.write()
//		//The piece's registry id is saved to nbt, and without the structure will not be saved properly and users will experience errors in the console whenever the chunk is loaded or unloaded
//		GAME_DUNGEON_PIECE = Registry.register(Registry.STRUCTURE_PIECE, LootGames.MODID + ":game_dungeon", GameDungeonPiece::new);
//
//		GAME_DUNGEON.setRegistryName(LootGames.MODID, "game_dungeon");
//		event.getRegistry().register(GAME_DUNGEON);
//	}
//
//	/**
//	 * Structures should be added as features to any biome that they might overlap into, to avoid generation being cut off at chunk edges
//	 * Structures can be added as structures to any biome they should start in
//	 */
//	private static void addOverworldStructures(Biome biome) {
//	    //Vegetal decoration runs after all other stages.
//	    biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(GAME_DUNGEON, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
//	}
//
//	/**
//	 * Add structures and features to biomes
//	 * Should be called after the registry events have run, so that all biomes and features already exist
//	 * Using categories like this is just one way of choosing which biome to place a feature in
//	 * It is important that each structure be added both as a feature and as a structure
//	 */
//	@SubscribeEvent
//	public static void applyFeatures(FMLCommonSetupEvent event) {
//		for (Biome biome : ForgeRegistries.BIOMES) {
//			biome.addStructure(GAME_DUNGEON, IFeatureConfig.NO_FEATURE_CONFIG);
//			biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(GAME_DUNGEON, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
//			addOverworldStructures(biome);
//		}
//	}
//}