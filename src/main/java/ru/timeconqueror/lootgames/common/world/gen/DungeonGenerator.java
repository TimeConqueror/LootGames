package ru.timeconqueror.lootgames.common.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.registry.LGBlocks;

public class DungeonGenerator {
    //    public static final int ROOM_WIDTH = 21;
//    public static final int ROOM_INNER_WIDTH = ROOM_WIDTH - 2;
//    public static final int ROOM_HEIGHT = 9;
//    public static final int FROM_SURFACE_DISTANCE = 8;
//    public static final int PUZZLEROOM_MASTER_TE_OFFSET = 3;
//
//    private static final BlockState AIR = Blocks.AIR.getDefaultState();
//    private static final BlockState PUZZLE_MASTER = LGBlocks.PUZZLE_MASTER.getDefaultState();
//    private static final BlockState DUNGEON_CEILING = LGBlocks.DUNGEON_CEILING.getDefaultState();
//    private static final BlockState DUNGEON_CEILING_CRACKED = LGBlocks.DUNGEON_CEILING_CRACKED.getDefaultState();
//    private static final BlockState DUNGEON_WALL = LGBlocks.DUNGEON_WALL.getDefaultState();
//    private static final BlockState DUNGEON_WALL_CRACKED = LGBlocks.DUNGEON_WALL_CRACKED.getDefaultState();
    private static final BlockState DUNGEON_FLOOR = LGBlocks.DUNGEON_FLOOR.getDefaultState();

    //    private static final BlockState DUNGEON_FLOOR_CRACKED = LGBlocks.DUNGEON_FLOOR_CRACKED.getDefaultState();
//    private static final BlockState DUNGEON_FLOOR_SHIELDED = LGBlocks.DUNGEON_FLOOR_SHIELDED.getDefaultState();
//    private static final BlockState DUNGEON_LAMP = LGBlocks.DUNGEON_LAMP.getDefaultState();
//    private static final BlockState DUNGEON_LAMP_BROKEN = LGBlocks.DUNGEON_LAMP_BROKEN.getDefaultState();
//
//    private World world;
//    private int dungeonTopY;
//    private int dungeonBottomY;
//    private Random chunkRand;
//
//    public DungeonGenerator(Random chunkRand) {
//        this.chunkRand = chunkRand;
//    }
//
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
            world.setBlockState(blockPos, DUNGEON_FLOOR);

            resetUnbreakableFieldsStartingFrom(world, blockPos.north());
            resetUnbreakableFieldsStartingFrom(world, blockPos.east());
            resetUnbreakableFieldsStartingFrom(world, blockPos.south());
            resetUnbreakableFieldsStartingFrom(world, blockPos.west());

            return true;
        }

        return false;
    }
//
//    private void calcDungeonLevel(int x, int z) {
//        dungeonTopY = 256;
//        for (int axisX = 0; axisX <= ROOM_WIDTH; axisX++) {
//            for (int axisZ = 0; axisZ <= ROOM_WIDTH; axisZ++) {
//                int height = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x + axisX, z + axisZ);
//                dungeonTopY = Math.min(dungeonTopY, height);
//            }
//        }
//        dungeonTopY -= FROM_SURFACE_DISTANCE;
//        dungeonBottomY = dungeonTopY - ROOM_HEIGHT;
//    }
//
//    public boolean generateDungeon(World world, int x, int z) {
//        LootGames.LOGGER.trace("DungeonGenerator => generateDungeon()");
//
//        this.world = world;
//
//        calcDungeonLevel(x, z);
//
//        LootGames.LOGGER.trace("DungeonGenerator => generateDungeon() : Dungeon bottom at: y= {}", dungeonBottomY);
//
//        Iterable<BlockPos> allInBox = BlockPos.getAllInBoxMutable(new BlockPos(x, dungeonBottomY, z), new BlockPos(x + ROOM_WIDTH, dungeonTopY, z + ROOM_WIDTH));
//        if (canBeReplaced(allInBox)) {
//            for (BlockPos pos : allInBox) {
//                if (pos.getX() == x || pos.getZ() == z || pos.getX() == x + ROOM_WIDTH || pos.getZ() == z + ROOM_WIDTH) {
//                    if (pos.getY() != dungeonTopY - (int) Math.floor(ROOM_HEIGHT / 2F))
//                        placeBlock(pos, RandHelper.chance(chunkRand, 90, DUNGEON_WALL, DUNGEON_WALL_CRACKED));
//                    else {
//                        placeBlock(pos, RandHelper.chance(chunkRand, 90, DUNGEON_LAMP, DUNGEON_LAMP_BROKEN));
//                    }
//                } else if (pos.getY() == dungeonBottomY) {
//                    placeBlock(pos, RandHelper.chance(chunkRand, 90, DUNGEON_FLOOR, DUNGEON_FLOOR_CRACKED));
//                } else if (pos.getY() == dungeonTopY) {
//                    placeBlock(pos, RandHelper.chance(chunkRand, 90, DUNGEON_CEILING, DUNGEON_CEILING_CRACKED));
//                } else if (pos.getY() == dungeonBottomY + 1) {
//                    placeBlock(pos, DUNGEON_FLOOR_SHIELDED);
//                } else if (pos.getX() == x + (ROOM_WIDTH / 2 + 1) && pos.getZ() == z + (ROOM_WIDTH / 2 + 1) && pos.getY() == dungeonBottomY + PUZZLEROOM_MASTER_TE_OFFSET) {
//                    placeBlock(pos, PUZZLE_MASTER);
//                } else {
//                    placeBlock(pos, AIR);
//                }
//            }
//
//            LootGames.LOGGER.debug("PuzzleDungeon has spawned at {} {} {} in Dimension {}", x, dungeonBottomY, z, world.getDimension().getType());
//
//            // Generate entrance
//            // Loop as long as the last "staircase" block is something different than air
//            // and is obstructed. This might lead to a very long staircase on mountains...
//            // So we limit the distance from the border to max 11 blocks
//            boolean zDirected = chunkRand.nextBoolean();
//            BlockPos pos = zDirected ? new BlockPos(x + ROOM_WIDTH / 2 + 1, dungeonBottomY + 2, z + ROOM_WIDTH + 1) : new BlockPos(x + ROOM_WIDTH + 1, dungeonBottomY + 2, z + ROOM_WIDTH / 2 + 1);
//            int distance = 0;
//            while (world.getBlockState(pos).getBlock() != Blocks.AIR || !world.canBlockSeeSky(pos)) {
//                distance++;
//                if (distance > 11) break;//TODO maybe 15 will be better?
//
//                if (!canBeReplaced(pos)) break;
//                placeBlock(pos, AIR);
//                pos = zDirected ? pos.south() : pos.east();
//
//                if (!canBeReplaced(pos)) break;
//                placeBlock(pos, AIR);
//                pos = pos.up();
//            }
//
//            return true;
//        } else {
//            LootGames.LOGGER.debug("PuzzleDungeon hasn't spawned, location at X/Z {} {} is not suitable", x, z);
//            return false;
//        }
//    }
//
//
//    private boolean canBeReplaced(Iterable<BlockPos> box) {
//        boolean result = true;
//        LootGames.LOGGER.trace("DungeonGenerator => canBeReplaced()");
//
//        for (BlockPos pos : box) {
//            result = canBeReplaced(pos);
//            if (!result) break;
//        }
//
//        LootGames.LOGGER.trace("DungeonGenerator => canBeReplaced() : Result is {}.", result);
//        return result;
//    }
//
//    /**
//     * Checking for tileentities and blocks forbidden to be replaced.
//     */
//    private boolean canBeReplaced(BlockPos pos) {
//        if (world.getBlockState(pos).getBlockHardness(world, pos) == -1F) {
//            LootGames.LOGGER.debug("Block at {} is unbreakable. Skipping dungeon generator.", pos);
//            return false;
//        }
//
//        TileEntity te = world.getTileEntity(pos);
//        if (te != null) {
//            LootGames.LOGGER.debug("Block at {} is a TileEntity. Skipping dungeon generator.", pos);
//            return false;
//        }
//
//        return true;
//    }
//
//    public void placeBlock(BlockPos pos, BlockState state) {
//        placeBlock(pos, state, 3);
//    }
//
//    public void placeBlock(BlockPos pos, BlockState state, int flags) {
//        world.setBlockState(pos, state, flags);
//    }
}
