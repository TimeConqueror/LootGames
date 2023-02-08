package ru.timeconqueror.lootgames.common.level.gen;
// fixme
//import com.mojang.serialization.Codec;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.RegistryAccess;
//import net.minecraft.world.level.biome.Biome;
//import net.minecraft.world.level.biome.Biome.BiomeCategory;
//import net.minecraft.world.level.chunk.ChunkGenerator;
//import net.minecraft.world.level.levelgen.GenerationStep;
//import net.minecraft.world.level.levelgen.Heightmap;
//import net.minecraft.world.level.levelgen.feature.StructureFeature;
//import net.minecraft.world.level.levelgen.feature.StructureFeature.StructureStartFactory;
//import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
//import net.minecraft.world.level.levelgen.structure.BoundingBox;
//import net.minecraft.world.level.levelgen.structure.StructureStart;
//import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
//import ru.timeconqueror.lootgames.common.config.LGConfigs;
//import ru.timeconqueror.timecore.api.util.CollectionUtils;
//import ru.timeconqueror.timecore.api.util.GenHelper;
//
//import java.util.Set;
//
public class GameDungeonStructure /*extends StructureFeature<NoneFeatureConfiguration> */ {
    public static final int ROOM_WIDTH = 21;
    public static final int ROOM_INNER_WIDTH = ROOM_WIDTH - 2;
    public static final int ROOM_HEIGHT = 9;
    public static final int DISTANCE_FROM_SURFACE = 8;
    public static final int MASTER_BLOCK_OFFSET = 3;
}
//
//    public GameDungeonStructure(Codec<NoneFeatureConfiguration> codec_) {
//        super(codec_);
//    }
//
//    @Override
//    public StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
//        return Start::new;
//    }
//
//    @Override
//    public GenerationStep.Decoration step() {
//        return GenerationStep.Decoration.VEGETAL_DECORATION;
//    }
//
//    public static class Start extends StructureStart<NoneFeatureConfiguration> {
//        public Start(StructureFeature<NoneFeatureConfiguration> structure, int chunkX, int chunkZ, BoundingBox mutableBoundingBox, int references, long seed) {
//            super(structure, chunkX, chunkZ, mutableBoundingBox, references, seed);
//        }
//
//        @Override
//        public void generatePieces(RegistryAccess dynamicRegistries, ChunkGenerator chunkGenerator, StructureManager templateManager, int chunkX, int chunkZ, Biome biomeIn, NoneFeatureConfiguration featureConfig) {
//            int x = chunkX << 4;
//            int z = chunkZ << 4;
//
//            int startTopY = GenHelper.getMinFirstFreeHeight(chunkGenerator, x, z, x + ROOM_WIDTH, z + ROOM_WIDTH);
//            Set<Biome> biomesNearby = chunkGenerator.getBiomeSource().getBiomesWithin(x + ROOM_WIDTH / 2, startTopY, z + ROOM_WIDTH / 2, ROOM_WIDTH / 2);
//
//            //makes deeper in water biomes
//            if (CollectionUtils.anyMatch(biomesNearby, biome -> biome.getBiomeCategory() == BiomeCategory.BEACH || biome.getBiomeCategory() == BiomeCategory.OCEAN || biome.getBiomeCategory() == BiomeCategory.RIVER)) {
//                startTopY = GenHelper.getMinFirstFreeHeight(chunkGenerator, x, z, x + ROOM_WIDTH, z + ROOM_WIDTH, Heightmap.Types.OCEAN_FLOOR_WG);
//            }
//
//            int yDungeonBottom = startTopY - DISTANCE_FROM_SURFACE - ROOM_HEIGHT;
//            if (yDungeonBottom < 1) return;
//
//            BlockPos start = new BlockPos(x, yDungeonBottom, z);
//
//            pieces.add(new GameDungeonPieces.Piece(templateManager, GameDungeonPieces.ROOM, start));
//
//            if (LGConfigs.GENERAL.worldGen.maxEntryPathLength.get() > 0) {
//                pieces.add(GameDungeonPieces.createEntryPath(random, start.offset(ROOM_WIDTH / 2, 0, ROOM_WIDTH / 2), chunkGenerator));
//            }
//
//            this.calculateBoundingBox();
//        }
//    }
//}
