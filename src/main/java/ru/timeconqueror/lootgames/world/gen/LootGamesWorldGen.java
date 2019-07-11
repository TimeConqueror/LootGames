package ru.timeconqueror.lootgames.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.config.LootGamesConfig;

import java.util.Random;

public class LootGamesWorldGen implements IWorldGenerator {
    private Random rand;

    public LootGamesWorldGen() {
        rand = new Random();
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
//        long timeStart = System.currentTimeMillis();

        LootGames.logHelper.trace("WorldGen => Generate()");
        if (!checkSpawnConditions(chunkX, chunkZ, world)) {
            LootGames.logHelper.trace("Stopped this worldgen run");
            return;
        }

        DungeonGenerator dungeonGenerator = new DungeonGenerator();
//        // Locked to GOL for now. Will be changed to random later
//        ILootGame tGOLgame = new GameOfLightGame();
//
        dungeonGenerator.generateDungeon(world, (chunkX << 4) + 8, (chunkZ << 4) + 8);

//        long timeStop = System.currentTimeMillis();
//        LootGames.profiler.addTimeToList("WorldGen", timeStop - timeStart);
    }

    private boolean checkSpawnConditions(int chunkX, int chunkZ, World world) {
//        long tStart = System.currentTimeMillis();

        boolean canSpawn = false;

        if (!LootGamesConfig.worldGen.isDungeonWorldGenEnabled) {
            LootGames.logHelper.trace("WorldGen => Generate() => checkSpawnConditions() => WorldGen is DISABLED");
        } else if (!LootGamesConfig.isDimensionEnabledForWG(world.provider.getDimension())) {
            LootGames.logHelper.trace("WorldGen => Generate() => checkSpawnConditions() => Dim %d is not whitelisted", world.provider.getDimension());
        } else if (!canSpawnInChunk(chunkX, chunkZ, world)) {
            LootGames.logHelper.trace("WorldGen => Generate() => checkSpawnConditions() => Location not suitable");
        } else {
            LootGames.logHelper.trace("WorldGen => Generate() => canSpawnInChunk() => Location suitable");
            canSpawn = true;
        }

//        long tStop = System.currentTimeMillis();
//        LootGames.profiler.addTimeToList("WorldGen => checkSpawnConditions()", tStop - tStart);
        return canSpawn;
    }

    private boolean canSpawnInChunk(int chunkX, int chunkZ, World world) {
        boolean canSpawn = false;
        int rhombSize = LootGamesConfig.getRhombSizeForDim(world.provider.getDimension());

        int xc = (chunkX * 2) + chunkZ, zc = (chunkZ * 2) + chunkX;
        rand.setSeed(world.getSeed() + (xc / (rhombSize * 2)) + ((zc / (rhombSize * 2)) << 14));

        int pos1 = 3 + rand.nextInt(rhombSize * 2 - 3);
        int pos2 = 3 + rand.nextInt(rhombSize * 2 - 3);

        int modXC = mod(xc, rhombSize * 2);
        int modZC = mod(zc, rhombSize * 2);

        if (modXC >= 3 && modZC >= 3) {
            if ((modXC == pos1 && modZC == pos2) || (modXC == pos1 + 1 && (modZC == pos2 || modZC == pos2 + 1)))
                canSpawn = true;
        }

        return canSpawn;
    }

    int mod(int x, int div) {
        int r = x % div;
        return r < 0 ? r + div : r;
    }
}
