package ru.timeconqueror.lootgames.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class LootGamesWorldGen implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

    }
//    @Override
//    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
//        if (!checkSpawnConditions(pChunkX, pChunkZ, pWorld)) {
//            LootGames.DungeonLogger.trace("Stopped this worldgen run");
//            return;
//        }
//        StructureGenerator sGen = new StructureGenerator();
//        // Locked to GOL for now. Will be changed to random later
//        ILootGame tGOLgame = new GameOfLightGame();
//
//        sGen.generatePuzzleMicroDungeon(pWorld, (pChunkX << 4) + 8, (pChunkZ << 4) + 8);
//        long tStop = System.currentTimeMillis();
//        LootGames.Profiler.AddTimeToList("WorldGen", tStop - tStart);
//    }
//
//    private boolean checkSpawnConditions(int pChunkX, int pChunkZ, World pWorld) {
//        long tStart = System.currentTimeMillis();
//        boolean tState = true;
//
//        if (!LootGames.ModConfig.WorldGenEnabled) {
//            LootGames.DungeonLogger.trace("WorldGen => Generate() => checkSpawnConditions() => WorldGen is DISABLED");
//            tState = false;
//        }
//
//        if (tState && !LootGames.ModConfig.isDimensionEnabledForWG(pWorld.provider.dimensionId)) {
//            LootGames.DungeonLogger.trace("WorldGen => Generate() => checkSpawnConditions() => Dim %d is not Whitelisted", pWorld.provider.dimensionId);
//            tState = false;
//        }
//
//        if (tState && !canSpawnInChunk_v3(pChunkX, pChunkZ, pWorld)) {
//            LootGames.DungeonLogger.trace("WorldGen => Generate() => checkSpawnConditions() => Location not suitable");
//            tState = false;
//        }
//
//        if (tState)
//            LootGames.DungeonLogger.trace("WorldGen => Generate() => canSpawnInChunk() => Location suitable");
//
//        long tStop = System.currentTimeMillis();
//        LootGames.Profiler.AddTimeToList("WorldGen => checkSpawnConditions()", tStop - tStart);
//        return tState;
//    }
}
