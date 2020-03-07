package ru.timeconqueror.lootgames.common.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.api.util.RandHelper;

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
            Material.SNOW, Material.EARTH, Material.LEAVES, Material.PLANTS, Material.AIR, Material.LAVA, Material.PORTAL};

    public static void resetUnbreakablePlayField(World world, BlockPos floorPos) {
        if (!resetUnbreakableFieldsStartingFrom(world, floorPos)) {
            resetUnbreakableFieldsStartingFrom(world, floorPos.north());
            resetUnbreakableFieldsStartingFrom(world, floorPos.east());
            resetUnbreakableFieldsStartingFrom(world, floorPos.south());
            resetUnbreakableFieldsStartingFrom(world, floorPos.west());
        }
    }

    private static boolean resetUnbreakableFieldsStartingFrom(World world, BlockPos blockPos) {
        BlockState state = world.getBlockState(blockPos);
        if (state.getBlock() == LGBlocks.DUNGEON_FLOOR_SHIELDED) {
            world.setBlockState(blockPos, LGBlocks.DUNGEON_FLOOR.getDefaultState());

            resetUnbreakableFieldsStartingFrom(world, blockPos.north());
            resetUnbreakableFieldsStartingFrom(world, blockPos.east());
            resetUnbreakableFieldsStartingFrom(world, blockPos.south());
            resetUnbreakableFieldsStartingFrom(world, blockPos.west());

            return true;
        }

        return false;
    }

    /**
     * Make sure the block is really a "ground" block, and no plants, trees, water or whatever.
     */
    private boolean isValidSurfaceBlock(BlockState block) {
        boolean result = true;

        for (Material m : invalidMaterials) {
            if (block.getMaterial() == m) {
                result = false;
                break;
            }
        }

        LootGames.LOGGER.trace("DungeonGenerator => isValidSurfaceBlock() Result is: {}", result);

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
            LootGames.LOGGER.trace("DungeonGenerator => setSurfaceLevel(): Scanning y={}", surfaceY);

            for (int axisX = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisX <= PUZZLEROOM_CENTER_TO_BORDER; axisX++) {
                for (int axisZ = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisZ <= PUZZLEROOM_CENTER_TO_BORDER; axisZ++) {
                    BlockState state = world.getBlockState(new BlockPos(axisX + x, surfaceY, axisZ + z));

                    if (!isValidSurfaceBlock(state)) {
                        LootGames.LOGGER.trace("DungeonGenerator => setSurfaceLevel(): y={} has failed. Block is not valid: {} in {}", surfaceY, state.getBlock().toString(), new BlockPos(axisX + x, surfaceY, axisZ + z).toString());
                        yLevelCheckPassed = false;
                        break;
                    }
                }
                if (!yLevelCheckPassed)
                    break;
            }

            if (yLevelCheckPassed) {
                LootGames.LOGGER.trace("DungeonGenerator => setSurfaceLevel(): y={} has passed", surfaceY);
                surfaceLevel = surfaceY;
                dungeonTop = surfaceLevel - PUZZLEROOM_SURFACE_DISTANCE;
                dungeonBottom = surfaceLevel - (PUZZLEROOM_HEIGHT + PUZZLEROOM_SURFACE_DISTANCE);
                break;
            }
        }

        return yLevelCheckPassed;
    }

    public boolean generateDungeon(World world, int x, int z) {
        return doGenDungeon(world, x, z);
    }

    public boolean doGenDungeon(World world, int x, int z) {
        LootGames.LOGGER.trace("DungeonGenerator => generateDungeon()");

        this.world = world;
        centerX = x;
        centerZ = z;

        if (!setSurfaceLevel(x, z)) {
            LootGames.LOGGER.debug("Can't spawn PuzzleDungeon. Y level not found.");
            return false;
        }
        LootGames.LOGGER.trace("DungeonGenerator => generateDungeon() : Surface level found. Dungeon bottom at: y= {}", dungeonBottom);

        if (checkForFreeSpace()) {
            for (int axisX = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisX <= PUZZLEROOM_CENTER_TO_BORDER; axisX++) {
                for (int axisZ = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisZ <= PUZZLEROOM_CENTER_TO_BORDER; axisZ++) {
                    for (int axisY = dungeonBottom; axisY <= dungeonTop; axisY++) {
                        BlockPos pos = new BlockPos(axisX + centerX, axisY, axisZ + centerZ);

                        // Clear the space first before we continue
                        placeBlock(pos, Blocks.AIR.getDefaultState());

                        // Static center block with masterTE
                        if (axisX == 0 && axisZ == 0 && axisY == dungeonBottom + PUZZLEROOM_MASTER_TE_OFFSET)
                            placeBlock(pos, LGBlocks.PUZZLE_MASTER.getDefaultState());
                        else if (axisY == dungeonBottom) // bottom layer of the dungeon
                            placeBlock(pos, RandHelper.chance(90, LGBlocks.DUNGEON_FLOOR, LGBlocks.DUNGEON_FLOOR_CRACKED).getDefaultState());
                        else if (axisY == dungeonTop) // Top layer of the dungeon
                            placeBlock(pos, RandHelper.chance(90, LGBlocks.DUNGEON_CEILING, LGBlocks.DUNGEON_CEILING_CRACKED).getDefaultState());
                        else if (axisY == dungeonBottom + 1) // Playfield placeholder to the player doesn't stand within generated blocks
                            placeBlock(pos, LGBlocks.DUNGEON_FLOOR_SHIELDED.getDefaultState());
                        else {
                            if (axisX == (PUZZLEROOM_CENTER_TO_BORDER * -1) || axisX == PUZZLEROOM_CENTER_TO_BORDER || axisZ == (PUZZLEROOM_CENTER_TO_BORDER * -1) || axisZ == PUZZLEROOM_CENTER_TO_BORDER) {
                                if (axisY == (dungeonTop - (int) Math.floor((PUZZLEROOM_HEIGHT / 2F))))
                                    placeBlock(pos, RandHelper.chance(90, LGBlocks.DUNGEON_LAMP, LGBlocks.DUNGEON_LAMP_BROKEN).getDefaultState());
                                else
                                    placeBlock(pos, RandHelper.chance(90, LGBlocks.DUNGEON_WALL, LGBlocks.DUNGEON_WALL_CRACKED).getDefaultState());
                            }
                        }
                    }
                }
            }
            LootGames.LOGGER.debug("PuzzleDungeon spawned at {} {} {} in Dimension {}", centerX, dungeonBottom, centerZ, world.getDimension().getType());

            // Generate entrance
            // Loop as long as the last "staircase" block is something different than air
            // and is obstructed. This might lead to a very long staircase on mountains...
            // So we limit the distance from the border to max 15 blocks
            // If room needs to be even, make the entrance 2 blocks wide
            BlockState entrance;
            BlockPos pos;
            int axisZ = PUZZLEROOM_CENTER_TO_BORDER;
            int axisY = dungeonBottom + 2;
            do {
                axisZ++;

                for (int axisX = centerX; axisX <= centerX; axisX++)
                    for (axisY = (dungeonBottom + 2); axisY <= (dungeonBottom + 4); axisY++) {
                        BlockPos blockPos = new BlockPos(axisX, axisY + (axisZ - PUZZLEROOM_CENTER_TO_BORDER), axisZ + centerZ);
                        BlockState currentBlockState = this.world.getBlockState(blockPos);
                        if (currentBlockState.getMaterial().isSolid())
                            placeBlock(blockPos, Blocks.AIR.getDefaultState());
                        else
                            break;
                    }

                pos = new BlockPos(centerX, axisY + (axisZ - PUZZLEROOM_CENTER_TO_BORDER), axisZ + centerZ);
                entrance = this.world.getBlockState(pos);

                if ((axisZ - PUZZLEROOM_CENTER_TO_BORDER) >= 15)
                    break;
            }
            while (entrance.getBlock() != Blocks.AIR || !this.world.canBlockSeeSky(new BlockPos(centerX, axisY + (axisZ - PUZZLEROOM_CENTER_TO_BORDER), axisZ + centerZ)));

            return true;

        } else {
            LootGames.LOGGER.debug("PuzzleDungeon not spawned, location at X/Z {} {} is not suitable", x, z);
            return false;
        }

    }

    /**
     * Checking for tileentities and blocks forbidden to be replaced.
     */
    private boolean checkForFreeSpace() {
        boolean result = true;
        LootGames.LOGGER.trace("DungeonGenerator => checkForFreeSpace()");
        for (int axisX = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisX <= PUZZLEROOM_CENTER_TO_BORDER; axisX++) {
            for (int axisZ = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisZ <= PUZZLEROOM_CENTER_TO_BORDER; axisZ++) {
                for (int axisY = dungeonBottom; axisY <= dungeonTop; axisY++) {
                    BlockPos pos = new BlockPos(axisX + centerX, axisY, axisZ + centerZ);
//                    IBlockState blockState = world.getBlockState(pos);

                    // Skip TE check for GT Blockores
                    //TODO Add GT Compatibility
//                    UniqueIdentifier tBlockID = GameRegistry.findUniqueIdentifierFor(tBlock);
//                    if (tBlockID.modId.equalsIgnoreCase("gregtech") && tBlockID.name.toLowerCase().contains("blockores"))
//                        continue;

                    TileEntity te = world.getTileEntity(pos);
                    if (te != null) {
                        LootGames.LOGGER.debug("Block at {} {} {} is a TileEntity. Skipping dungeon generator.", axisX + centerX, axisY, axisZ + centerZ);
                        result = false;
                        break;
                    }
                }
            }
        }
        LootGames.LOGGER.trace("DungeonGenerator => checkForFreeSpace() : Result is {}.", result);
        return result;
    }

    public void placeBlock(BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, 3);
    }
}
