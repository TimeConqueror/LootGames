package ru.timeconqueror.lootgames.common.world.gen;

//import com.mojang.datafixers.Dynamic;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.util.math.MutableBoundingBox;
//import net.minecraft.world.IWorld;
//import net.minecraft.world.biome.Biome;
//import net.minecraft.world.gen.ChunkGenerator;
//import net.minecraft.world.gen.Heightmap;
//import net.minecraft.world.gen.feature.NoFeatureConfig;
//import net.minecraft.world.gen.feature.structure.ScatteredStructure;
//import net.minecraft.world.gen.feature.structure.Structure;
//import net.minecraft.world.gen.feature.structure.StructureStart;
//import net.minecraft.world.gen.feature.template.TemplateManager;
//import ru.timeconqueror.lootgames.LootGames;
//import ru.timeconqueror.lootgames.common.world.gen.GameDungeonPieces.MainRoom;

import java.util.Random;
import java.util.function.Function;

//public class GameDungeonStructure extends ScatteredStructure<NoFeatureConfig> {
//    public static final int ROOM_WIDTH = 21;
//    public static final int ROOM_INNER_WIDTH = ROOM_WIDTH - 2;
//    public static final int ROOM_HEIGHT = 9;
//    public static final int DISTANCE_FROM_SURFACE = 8;
//    public static final int MASTER_BLOCK_OFFSET = 3;
//
//    public GameDungeonStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn) {
//        super(configFactoryIn);
//    }
//
//    @Override
//    protected int getSeedModifier() {
//        return getStructureName().hashCode();
//    }
//
//    @Override
//    public IStartFactory getStartFactory() {
//        return Start::new;
//    }
//
//    /**
//     * An 'id' for the structure, distinct from registry id
//     * Used for the Locate command (by forge only, vanilla uses its own system)
//     * Should probably be in the format 'modid:name'
//     *
//     * @return name of structure
//     */
//    @Override
//    public String getStructureName() {
//        return LootGames.MODID + ":game_dungeon";
//    }
//
//    /**
//     * Legacy code, only used to update from old structure format
//     *
//     * @return irrelevant
//     */
//    @Override
//    public int getSize() {
//        return 1;
//    }
//
//    /**
//     * The structure start is responsible for creating the structure in memory, but not for placing the blocks themselves
//     */
//    public static class Start extends StructureStart {
//
//        public Start(Structure<?> structure, int chunkX, int chunkZ, Biome biome, MutableBoundingBox boundingBox, int references, long seed) {
//            super(structure, chunkX, chunkZ, biome, boundingBox, references, seed);
//        }
//
//        @Override
//        public void init(ChunkGenerator<?> generator, TemplateManager templateManager, int chunkX, int chunkZ, Biome biome) {
//            int x0 = chunkX << 4;
//            int z0 = chunkZ << 4;
//
//            int ya = generator.func_222531_c(x0, z0, Heightmap.Type.WORLD_SURFACE_WG); //get highest surface block
//            int yb = generator.func_222531_c(x0, z0 + ROOM_WIDTH, Heightmap.Type.WORLD_SURFACE_WG);
//            int yc = generator.func_222531_c(x0 + ROOM_WIDTH, z0 + ROOM_WIDTH, Heightmap.Type.WORLD_SURFACE_WG);
//            int yd = generator.func_222531_c(x0 + ROOM_WIDTH, z0, Heightmap.Type.WORLD_SURFACE_WG);
//
//            int yDungeonBottom = Math.min(Math.min(ya, yb), Math.min(yc, yd)) - DISTANCE_FROM_SURFACE - ROOM_HEIGHT;
//
//            MainRoom room = new MainRoom(new BlockPos(chunkX * 16, yDungeonBottom, chunkZ * 16), rand);
//            this.components.add(room);
//
//            //This should be called last, after all components have been added
//            this.recalculateStructureSize();
//        }
//
//        @Override
//        public void generateStructure(IWorld worldIn, Random rand, MutableBoundingBox structurebb, ChunkPos pos) {
//            super.generateStructure(worldIn, rand, structurebb, pos);
//        }
//    }
//}
