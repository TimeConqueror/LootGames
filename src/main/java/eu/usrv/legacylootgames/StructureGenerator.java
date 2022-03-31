package eu.usrv.legacylootgames;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import eu.usrv.legacylootgames.blocks.DungeonBrick;
import eu.usrv.legacylootgames.blocks.DungeonLightSource;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.common.config.ConfigGeneral;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.api.common.config.Config;
import ru.timeconqueror.timecore.api.util.RandHelper;


public class StructureGenerator {
    public static int PUZZLEROOM_CENTER_TO_BORDER = 10;
    public static int PUZZLEROOM_HEIGHT = 8;
    public static int PUZZLEROOM_SURFACE_DISTANCE = 2;
    public static int PUZZLEROOM_MASTER_TE_OFFSET = 3;

    private World world;
    private int centerX;
    private int centerZ;
    private int _mSurface = -1;
    private int dungeonTop = -1;
    private int dungeonBottom = -1;

    private final Material[] _mValidMaterials = {Material.wood, Material.water, Material.cactus, Material.snow, Material.grass, Material.leaves, Material.plants, Material.air, Material.lava, Material.portal};

    public static int getRoomWidth() {
        return PUZZLEROOM_CENTER_TO_BORDER * 2 + 1;
    }

    /**
     * Make sure the block is really a "ground" block, and no plants, trees, water, whatever
     *
     * @param pBlock
     * @return
     */
    private boolean isValidSurfaceBlock(Block pBlock) {
        boolean tState = true;

        for (Material m : _mValidMaterials) {
            if (pBlock.getMaterial() == m) {
                tState = false;
                break;
            }
        }

        LootGamesLegacy.DungeonLogger.trace("StructureGenerator => isValidSurfaceBlock() Result is: %s", tState);

        return tState;
    }

    /**
     * Determine the surface level. This will scan the entire size the dungeon will use, in order to skip all "gaps" in
     * worldgen
     */
    private boolean setSurfaceLevel(int pLocationX, int pLocationZ) {
        boolean tYLevelCheckPassed = true;

        for (int tSurfaceY = 128; tSurfaceY > 20; tSurfaceY--) {
            tYLevelCheckPassed = true;
            LootGamesLegacy.DungeonLogger.trace("StructureGenerator => setSurfaceLevel(): Scanning y=%d", tSurfaceY);

            for (int axisX = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisX <= PUZZLEROOM_CENTER_TO_BORDER; axisX++) {
                for (int axisZ = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisZ <= PUZZLEROOM_CENTER_TO_BORDER; axisZ++) {
                    if (!isValidSurfaceBlock(world.getBlock(axisX + pLocationX, tSurfaceY, axisZ + pLocationZ))) {
                        LootGamesLegacy.DungeonLogger.trace("StructureGenerator => setSurfaceLevel(): y=%d has failed. Block is not valid: %s", tSurfaceY, GameRegistry.findUniqueIdentifierFor(world.getBlock(axisX, tSurfaceY, axisZ)).toString());
                        tYLevelCheckPassed = false;
                    }

                    if (!tYLevelCheckPassed)
                        break;
                }
                if (!tYLevelCheckPassed)
                    break;
            }

            if (tYLevelCheckPassed) {
                LootGamesLegacy.DungeonLogger.trace("StructureGenerator => setSurfaceLevel(): y=%d has passed", tSurfaceY);
                _mSurface = tSurfaceY;
                dungeonTop = _mSurface - PUZZLEROOM_SURFACE_DISTANCE;
                dungeonBottom = _mSurface - (PUZZLEROOM_HEIGHT + PUZZLEROOM_SURFACE_DISTANCE);
                break;
            }
        }

        return tYLevelCheckPassed;
    }

    public boolean generatePuzzleMicroDungeon(World world, int x, int z) {
        long start = System.currentTimeMillis();
        boolean result = doGenDungeon(world, x, z);
        LootGamesLegacy.Profiler.AddTimeToList("WorldGen => generatePuzzleMicroDungeon()", System.currentTimeMillis() - start);

        return result;
    }

    public boolean doGenDungeon(World world, int x, int z) {
        LootGamesLegacy.DungeonLogger.trace("StructureGenerator => generatePuzzleMicroDungeon()");

        this.world = world;
        centerX = x;
        centerZ = z;

        if (!setSurfaceLevel(x, z)) {
            LootGamesLegacy.DungeonLogger.debug("Can't spawn PuzzleDungeon. Y level not found");
            return false;
        }
        LootGamesLegacy.DungeonLogger.trace("StructureGenerator => generatePuzzleMicroDungeon() : Surface level found. Dungeon bottom at: y= %d", dungeonBottom);

        if (checkForFreeSpace()) {
            for (int axisX = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisX <= PUZZLEROOM_CENTER_TO_BORDER; axisX++) {
                for (int axisZ = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisZ <= PUZZLEROOM_CENTER_TO_BORDER; axisZ++) {
                    for (int axisY = dungeonBottom; axisY <= dungeonTop; axisY++) {
                        // Clear the space first before we continue
                        this.world.setBlockToAir(axisX + centerX, axisY, axisZ + centerZ);

                        // Static center block with masterTE
                        if (axisX == 0 && axisZ == 0 && axisY == dungeonBottom + PUZZLEROOM_MASTER_TE_OFFSET) {
                            this.world.setBlock(axisX + centerX, axisY, axisZ + centerZ, LGBlocks.PUZZLE_MASTER);
                        } else if (axisY == dungeonBottom) { // bottom layer of the dungeon
                            genWallBlock(axisX + centerX, axisY, axisZ + centerZ, LGBlocks.DUNGEON_WALL, RandHelper.chance(10, DungeonBrick.Type.FLOOR_CRACKED.ordinal(), DungeonBrick.Type.FLOOR.ordinal()));
                        } else if (axisY == dungeonTop) { // Top layer of the dungeon
                            genWallBlock(axisX + centerX, axisY, axisZ + centerZ, LGBlocks.DUNGEON_WALL, RandHelper.chance(10, DungeonBrick.Type.CEILING_CRACKED.ordinal(), DungeonBrick.Type.CEILING.ordinal()));
                        } else if (axisY == dungeonBottom + 1) {// Playfield placeholder to the player doesn't stand within generated blocks
                            this.world.setBlock(axisX + centerX, axisY, axisZ + centerZ, LGBlocks.DUNGEON_WALL, DungeonBrick.Type.FLOOR_SHIELDED.ordinal(), 2);
                        } else {
                            if (axisX == (PUZZLEROOM_CENTER_TO_BORDER * -1) || axisX == PUZZLEROOM_CENTER_TO_BORDER || axisZ == (PUZZLEROOM_CENTER_TO_BORDER * -1) || axisZ == PUZZLEROOM_CENTER_TO_BORDER) {
                                if (axisY == (dungeonTop - (int) Math.floor((PUZZLEROOM_HEIGHT / 2)))) {
                                    genLightBlock(axisX + centerX, axisY, axisZ + centerZ, LGBlocks.DUNGEON_LAMP, RandHelper.chance(10, DungeonLightSource.State.BROKEN.ordinal(), DungeonLightSource.State.NORMAL.ordinal()));
                                } else {
                                    genWallBlock(axisX + centerX, axisY, axisZ + centerZ, LGBlocks.DUNGEON_WALL, RandHelper.chance(10, DungeonBrick.Type.WALL_CRACKED.ordinal(), DungeonBrick.Type.WALL.ordinal()));
                                }
                            }
                        }
                    }
                }
            }
            LootGamesLegacy.DungeonLogger.info(String.format("PuzzleDungeon spawned at %d %d %d in Dimension %d", centerX, dungeonBottom, centerZ, world.provider.dimensionId));

            // Generate entrance
            // Loop as long as the last "staircase" block is something different than air
            // and is obstructed. This might lead to a very long staircase on mountains...
            // So we limit the distance from the border to max 15 blocks
            // If room needs to be even, make the entrance 2 blocks wide
            Block tEntrance = Blocks.air;
            int axisZ = PUZZLEROOM_CENTER_TO_BORDER;
            int axisY = dungeonBottom + 2;
            int axisX = centerX;
            do {
                axisZ++;

                for (axisX = centerX; axisX <= centerX; axisX++)
                    for (axisY = (dungeonBottom + 2); axisY <= (dungeonBottom + 4); axisY++) {
                        Block tCurrentBlock = this.world.getBlock(axisX, axisY + (axisZ - PUZZLEROOM_CENTER_TO_BORDER), axisZ + centerZ);
                        if (tCurrentBlock.getMaterial().isSolid())
                            this.world.setBlockToAir(axisX, axisY + (axisZ - PUZZLEROOM_CENTER_TO_BORDER), axisZ + centerZ);
                        else
                            break;
                    }

                tEntrance = this.world.getBlock(centerX, axisY + (axisZ - PUZZLEROOM_CENTER_TO_BORDER), axisZ + centerZ);

                if ((axisZ - PUZZLEROOM_CENTER_TO_BORDER) >= 15)
                    break;
            }
            while (tEntrance != Blocks.air || !this.world.canBlockSeeTheSky(centerX, axisY + (axisZ - PUZZLEROOM_CENTER_TO_BORDER), axisZ + centerZ));

            return true;

        } else {
            LootGamesLegacy.DungeonLogger.debug(String.format("PuzzleDungeon not spawned, location at X/Z %d %d is not suitable", x, z));
            return false;
        }
    }

    private boolean fitVanillaStyle() {
        return LGConfigs.GENERAL.worldGen.fitVanillaDungeonStyle;
    }

    private void genLightBlock(int x, int y, int z, Block defaultBlock, int meta) {
        if(fitVanillaStyle()) {
            world.setBlock(x, y, z, Blocks.glowstone, 0, 2);
        } else {
            world.setBlock(x, y, z, defaultBlock, meta, 2);
        }
    }

    private void genWallBlock(int x, int y, int z, Block defaultBlock, int meta) {
        if(fitVanillaStyle()) {
            world.setBlock(x, y, z, RandHelper.chance(75, Blocks.cobblestone, Blocks.mossy_cobblestone), 0, 2);
        } else {
            world.setBlock(x, y, z, defaultBlock, meta, 2);
        }
    }

    private boolean checkForFreeSpace() {
        boolean tResult = true;
        LootGamesLegacy.DungeonLogger.trace("StructureGenerator => checkForFreeSpace()");
        for (int axisX = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisX <= PUZZLEROOM_CENTER_TO_BORDER; axisX++) {
            for (int axisZ = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisZ <= PUZZLEROOM_CENTER_TO_BORDER; axisZ++) {
                for (int axisY = dungeonBottom; axisY <= dungeonTop; axisY++) {
                    Block tBlock = world.getBlock(axisX + centerX, axisY, axisZ + centerZ);

                    // Skip TE check for GT Blockores
                    UniqueIdentifier tBlockID = GameRegistry.findUniqueIdentifierFor(tBlock);
                    if (tBlockID.modId.equalsIgnoreCase("gregtech") && tBlockID.name.toLowerCase().contains("blockores"))
                        continue;

                    TileEntity tTE = world.getTileEntity(axisX + centerX, axisY, axisZ + centerZ);
                    if (tTE != null) {
                        LootGamesLegacy.DungeonLogger.debug(String.format("Block at %d %d %d is a TileEntitiy. Skipping dungeon generator", axisX + centerX, axisY, axisZ + centerZ));
                        tResult = false;
                        break;
                    }
                }
            }
        }
        LootGamesLegacy.DungeonLogger.trace("StructureGenerator => checkForFreeSpace() : Result is %s", tResult);
        return tResult;
    }
}
