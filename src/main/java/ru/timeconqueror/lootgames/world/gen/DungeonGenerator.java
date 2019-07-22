package ru.timeconqueror.lootgames.world.gen;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.block.BlockDungeonBricks;
import ru.timeconqueror.lootgames.block.BlockDungeonLamp;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.timecore.api.auxiliary.RandHelper;

public class DungeonGenerator {
    public static final int PUZZLEROOM_CENTER_TO_BORDER = 10;
    public static final int PUZZLEROOM_HEIGHT = 8;
    public static final int PUZZLEROOM_SURFACE_DISTANCE = 2;
    public static final int PUZZLEROOM_MASTER_TE_OFFSET = 3;

    private World world;
    private int centerX;
    private int centerZ;
    private int surfaceLevel = -1;
    private int dungeonTop = -1;
    private int dungeonBottom = -1;

    private Material[] invalidMaterials = {Material.WOOD, Material.WATER, Material.CACTUS,
            Material.SNOW, Material.GRASS, Material.LEAVES, Material.PLANTS, Material.AIR, Material.LAVA, Material.PORTAL};

    /**
     * @param posIn - pos of center floor block.
     */
    public static void resetUnbreakablePlayfield(World world, BlockPos posIn) {
        for (int axisX = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisX <= PUZZLEROOM_CENTER_TO_BORDER; axisX++) {
            for (int axisZ = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisZ <= PUZZLEROOM_CENTER_TO_BORDER; axisZ++) {
                BlockPos pos = posIn.add(axisX, 0, axisZ);
                IBlockState state = world.getBlockState(pos);
                if (state.getBlock() == ModBlocks.DUNGEON_BRICKS) {
                    if (ModBlocks.DUNGEON_BRICKS.getMetaFromState(state) == BlockDungeonBricks.EnumType.DUNGEON_FLOOR_SHIELDED.getMeta()) {
                        world.setBlockState(pos, state.withProperty(BlockDungeonBricks.VARIANT, BlockDungeonBricks.EnumType.DUNGEON_FLOOR));
                    }
                }
            }
        }
    }

    /**
     * Make sure the block is really a "ground" block, and no plants, trees, water or whatever.
     */
    private boolean isValidSurfaceBlock(IBlockState block) {
        boolean result = true;

        for (Material m : invalidMaterials) {
            if (block.getMaterial() == m) {
                result = false;
                break;
            }
        }

        LootGames.logHelper.trace("DungeonGenerator => isValidSurfaceBlock() Result is: {}", result);

        return result;
    }

    /**
     * Determine the surfaceLevel level. This will scan the entire size the dungeon will use, in order to skip all "gaps" in
     * worldgen.
     */
    private boolean setSurfaceLevel(int x, int z) {
        boolean yLevelCheckPassed = true;

        for (int surfaceY = 128; surfaceY > 20; surfaceY--) {
            yLevelCheckPassed = true;
            LootGames.logHelper.trace("DungeonGenerator => setSurfaceLevel(): Scanning y={}", surfaceY);

            for (int axisX = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisX <= PUZZLEROOM_CENTER_TO_BORDER; axisX++) {
                for (int axisZ = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisZ <= PUZZLEROOM_CENTER_TO_BORDER; axisZ++) {
                    IBlockState state = world.getBlockState(new BlockPos(axisX + x, surfaceY, axisZ + z));

                    if (!isValidSurfaceBlock(state)) {
                        LootGames.logHelper.trace("DungeonGenerator => setSurfaceLevel(): y={} has failed. Block is not valid: {} in {}", surfaceY, state.getBlock().toString(), new BlockPos(axisX + x, surfaceY, axisZ + z).toString());
                        yLevelCheckPassed = false;
                        break;
                    }
                }
                if (!yLevelCheckPassed)
                    break;
            }

            if (yLevelCheckPassed) {
                LootGames.logHelper.trace("DungeonGenerator => setSurfaceLevel(): y={} has passed", surfaceY);
                surfaceLevel = surfaceY;
                dungeonTop = surfaceLevel - PUZZLEROOM_SURFACE_DISTANCE;
                dungeonBottom = surfaceLevel - (PUZZLEROOM_HEIGHT + PUZZLEROOM_SURFACE_DISTANCE);
                break;
            }
        }

        return yLevelCheckPassed;
    }

    public boolean generateDungeon(World world, int x, int z) {
//        long tStart = System.currentTimeMillis();
        boolean result = doGenDungeon(world, x, z);
//        long tStop = System.currentTimeMillis();
//        LootGames.Profiler.AddTimeToList("WorldGen => generateDungeon()", tStop - tStart);

        return result;
    }

    public boolean doGenDungeon(World world, int x, int z) {
        LootGames.logHelper.trace("DungeonGenerator => generateDungeon()");

        this.world = world;
        centerX = x;
        centerZ = z;

        if (!setSurfaceLevel(x, z)) {
            LootGames.logHelper.debug("Can't spawn PuzzleDungeon. Y level not found.");
            return false;
        }
        LootGames.logHelper.trace("DungeonGenerator => generateDungeon() : Surface level found. Dungeon bottom at: y= {}", dungeonBottom);

        if (checkForFreeSpace()) {
            for (int axisX = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisX <= PUZZLEROOM_CENTER_TO_BORDER; axisX++) {
                for (int axisZ = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisZ <= PUZZLEROOM_CENTER_TO_BORDER; axisZ++) {
                    for (int axisY = dungeonBottom; axisY <= dungeonTop; axisY++) {
                        BlockPos pos = new BlockPos(axisX + centerX, axisY, axisZ + centerZ);

                        // Clear the space first before we continue
                        placeBlock(pos, Blocks.AIR.getDefaultState());

                        // Static center block with masterTE
                        if (axisX == 0 && axisZ == 0 && axisY == dungeonBottom + PUZZLEROOM_MASTER_TE_OFFSET)
                            placeBlock(pos, ModBlocks.PUZZLE_MASTER.getDefaultState());
                        else if (axisY == dungeonBottom) // bottom layer of the dungeon
                            placeBlock(pos, ModBlocks.DUNGEON_BRICKS.getStateFromMeta(RandHelper.chance(10, BlockDungeonBricks.EnumType.DUNGEON_FLOOR_CRACKED.getMeta(), BlockDungeonBricks.EnumType.DUNGEON_FLOOR.getMeta())));
                        else if (axisY == dungeonTop) // Top layer of the dungeon
                            placeBlock(pos, ModBlocks.DUNGEON_BRICKS.getStateFromMeta(RandHelper.chance(10, BlockDungeonBricks.EnumType.DUNGEON_CEILING_CRACKED.getMeta(), BlockDungeonBricks.EnumType.DUNGEON_CEILING.getMeta())));
                        else if (axisY == dungeonBottom + 1) // Playfield placeholder to the player doesn't stand within generated blocks
                            placeBlock(pos, ModBlocks.DUNGEON_BRICKS.getStateFromMeta(BlockDungeonBricks.EnumType.DUNGEON_FLOOR_SHIELDED.getMeta()));
                        else {
                            if (axisX == (PUZZLEROOM_CENTER_TO_BORDER * -1) || axisX == PUZZLEROOM_CENTER_TO_BORDER || axisZ == (PUZZLEROOM_CENTER_TO_BORDER * -1) || axisZ == PUZZLEROOM_CENTER_TO_BORDER) {
                                if (axisY == (dungeonTop - (int) Math.floor((PUZZLEROOM_HEIGHT / 2F))))
                                    placeBlock(pos, ModBlocks.DUNGEON_LAMP.getDefaultState().withProperty(BlockDungeonLamp.BROKEN, RandHelper.chance(10, true, false)));
                                else
                                    placeBlock(pos, ModBlocks.DUNGEON_BRICKS.getStateFromMeta(RandHelper.chance(10, BlockDungeonBricks.EnumType.DUNGEON_WALL_CRACKED.getMeta(), BlockDungeonBricks.EnumType.DUNGEON_WALL.getMeta())));
                            }
                        }
                    }
                }
            }
            LootGames.logHelper.debug("PuzzleDungeon spawned at {} {} {} in Dimension {}", centerX, dungeonBottom, centerZ, world.provider.getDimension());

            // Generate entrance
            // Loop as long as the last "staircase" block is something different than air
            // and is obstructed. This might lead to a very long staircase on mountains...
            // So we limit the distance from the border to max 15 blocks
            // If room needs to be even, make the entrance 2 blocks wide
            IBlockState entrance;
            int axisZ = PUZZLEROOM_CENTER_TO_BORDER;
            int axisY = dungeonBottom + 2;
            do {
                axisZ++;

                for (int axisX = centerX; axisX <= centerX; axisX++)
                    for (axisY = (dungeonBottom + 2); axisY <= (dungeonBottom + 4); axisY++) {
                        BlockPos pos = new BlockPos(axisX, axisY + (axisZ - PUZZLEROOM_CENTER_TO_BORDER), axisZ + centerZ);
                        IBlockState currentBlockState = this.world.getBlockState(pos);
                        if (currentBlockState.getMaterial().isSolid())
                            placeBlock(pos, Blocks.AIR.getDefaultState());
                        else
                            break;
                    }

                entrance = this.world.getBlockState(new BlockPos(centerX, axisY + (axisZ - PUZZLEROOM_CENTER_TO_BORDER), axisZ + centerZ));

                if ((axisZ - PUZZLEROOM_CENTER_TO_BORDER) >= 15)
                    break;
            }
            while (entrance != Blocks.AIR || !this.world.canBlockSeeSky(new BlockPos(centerX, axisY + (axisZ - PUZZLEROOM_CENTER_TO_BORDER), axisZ + centerZ)));

            return true;

        } else {
            LootGames.logHelper.debug("PuzzleDungeon not spawned, location at X/Z {} {} is not suitable", x, z);
            return false;
        }

    }

    /**
     * Checking for tileentities and blocks forbidden to be replaced.
     */
    private boolean checkForFreeSpace() {
        boolean result = true;
        LootGames.logHelper.trace("DungeonGenerator => checkForFreeSpace()");
        for (int axisX = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisX <= PUZZLEROOM_CENTER_TO_BORDER; axisX++) {
            for (int axisZ = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisZ <= PUZZLEROOM_CENTER_TO_BORDER; axisZ++) {
                for (int axisY = dungeonBottom; axisY <= dungeonTop; axisY++) {
                    BlockPos pos = new BlockPos(axisX + centerX, axisY, axisZ + centerZ);
//                    IBlockState blockState = world.getBlockState(pos);

                    // Skip TE check for GT Blockores
                    //TODO Add GT Compability in 1.12.2
//                    UniqueIdentifier tBlockID = GameRegistry.findUniqueIdentifierFor(tBlock);
//                    if (tBlockID.modId.equalsIgnoreCase("gregtech") && tBlockID.name.toLowerCase().contains("blockores"))
//                        continue;

                    TileEntity te = world.getTileEntity(pos);
                    if (te != null) {
                        LootGames.logHelper.debug("Block at {} {} {} is a TileEntity. Skipping dungeon generator.", axisX + centerX, axisY, axisZ + centerZ);
                        result = false;
                        break;
                    }
                }
            }
        }
        LootGames.logHelper.trace("DungeonGenerator => checkForFreeSpace() : Result is {}.", result);
        return result;
    }

    public void placeBlock(BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, 3);
    }
}
