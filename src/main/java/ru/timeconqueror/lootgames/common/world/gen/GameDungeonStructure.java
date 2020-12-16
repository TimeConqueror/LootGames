package ru.timeconqueror.lootgames.common.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.timecore.util.GenHelper;

public class GameDungeonStructure extends Structure<NoFeatureConfig> {
    public static final int ROOM_WIDTH = 21;
    public static final int ROOM_INNER_WIDTH = ROOM_WIDTH - 2;
    public static final int ROOM_HEIGHT = 9;
    public static final int DISTANCE_FROM_SURFACE = 8;
    public static final int MASTER_BLOCK_OFFSET = 3;

    public GameDungeonStructure(Codec<NoFeatureConfig> codec_) {
        super(codec_);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Start::new;
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.VEGETAL_DECORATION;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(Structure<NoFeatureConfig> structure, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int references, long seed) {
            super(structure, chunkX, chunkZ, mutableBoundingBox, references, seed);
        }

        @Override
        public void generatePieces(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator, TemplateManager templateManager, int chunkX, int chunkZ, Biome biome, NoFeatureConfig featureConfig) {
            int x = chunkX << 4;
            int z = chunkZ << 4;

            int surfaceY = GenHelper.getMinFirstFreeHeight(chunkGenerator, x, z, x + ROOM_WIDTH, z + ROOM_WIDTH);

            int yDungeonBottom = surfaceY - DISTANCE_FROM_SURFACE - ROOM_HEIGHT;

            BlockPos start = new BlockPos(x, yDungeonBottom, z);

            pieces.add(new GameDungeonPieces.Piece(templateManager, GameDungeonPieces.ROOM, start));

            if (LGConfigs.GENERAL.worldGen.maxEntryPathLength.get() > 0) {
                pieces.add(GameDungeonPieces.createEntryPath(random, start.offset(ROOM_WIDTH / 2, 0, ROOM_WIDTH / 2), chunkGenerator));
            }

            this.calculateBoundingBox();
        }
    }
}
