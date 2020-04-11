package ru.timeconqueror.lootgames.common.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGStructures;
import ru.timeconqueror.timecore.api.util.RandHelper;

import java.util.Random;

import static ru.timeconqueror.lootgames.common.world.gen.GameDungeonPieces.MainRoom.CorruptedBlockSelector;
import static ru.timeconqueror.lootgames.common.world.gen.GameDungeonStructure.*;

public class GameDungeonPieces {
    private static final BlockState PUZZLE_MASTER = LGBlocks.PUZZLE_MASTER.getDefaultState();
    private static final BlockState DUNGEON_CEILING = LGBlocks.DUNGEON_CEILING.getDefaultState();
    private static final BlockState DUNGEON_CEILING_CRACKED = LGBlocks.DUNGEON_CEILING_CRACKED.getDefaultState();
    private static final BlockState DUNGEON_WALL = LGBlocks.DUNGEON_WALL.getDefaultState();
    private static final BlockState DUNGEON_WALL_CRACKED = LGBlocks.DUNGEON_WALL_CRACKED.getDefaultState();
    private static final BlockState DUNGEON_FLOOR = LGBlocks.DUNGEON_FLOOR.getDefaultState();
    private static final BlockState DUNGEON_FLOOR_CRACKED = LGBlocks.DUNGEON_FLOOR_CRACKED.getDefaultState();
    private static final BlockState DUNGEON_FLOOR_SHIELDED = LGBlocks.DUNGEON_FLOOR_SHIELDED.getDefaultState();
    private static final BlockState DUNGEON_LAMP = LGBlocks.DUNGEON_LAMP.getDefaultState();
    private static final BlockState DUNGEON_LAMP_BROKEN = LGBlocks.DUNGEON_LAMP_BROKEN.getDefaultState();

    private static final CorruptedBlockSelector FLOOR_SELECTOR = new CorruptedBlockSelector(DUNGEON_FLOOR, DUNGEON_FLOOR_CRACKED);
    private static final CorruptedBlockSelector CEILING_SELECTOR = new CorruptedBlockSelector(DUNGEON_CEILING, DUNGEON_CEILING_CRACKED);
    private static final CorruptedBlockSelector WALL_SELECTOR = new CorruptedBlockSelector(DUNGEON_WALL, DUNGEON_WALL_CRACKED, true);
    private static final CorruptedBlockSelector LAMP_SELECTOR = new CorruptedBlockSelector(DUNGEON_LAMP, DUNGEON_LAMP_BROKEN, true);

    public static class MainRoom extends StructurePiece {
        /**
         * This is the constructor we use ourselves, the other constructor is needed for the piece type
         */
        protected MainRoom(BlockPos pos) {
            super(LGStructures.GD_PIECE_MAIN_ROOM, 0);
            boundingBox = new MutableBoundingBox(pos, pos.add(ROOM_WIDTH, ROOM_HEIGHT, ROOM_WIDTH));
        }

        /**
         * This constructor must always be implemented, so that the piece type can be created
         */
        public MainRoom(TemplateManager manager, CompoundNBT nbt) {
            super(LGStructures.GD_PIECE_MAIN_ROOM, nbt);
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {

        }

        @Override
        public boolean addComponentParts(IWorld world, Random rand, MutableBoundingBox bb, ChunkPos chunkPosIn) {

            int centerY = ROOM_HEIGHT / 2;

            fillWithRandomizedBlocks(world, bb, 0, 0, 0, ROOM_WIDTH, 0, ROOM_WIDTH, true/*param - doNotReplaceAir*/, rand, FLOOR_SELECTOR);
            fillWithBlocks(world, bb, 1, 1, 1, ROOM_WIDTH - 1, 1, ROOM_WIDTH - 1, DUNGEON_FLOOR_SHIELDED, DUNGEON_FLOOR_SHIELDED, true /*param - doNotReplaceAir*/);
            fillWithRandomizedBlocks(world, bb, 0, 1, 0, ROOM_WIDTH, centerY - 1, ROOM_WIDTH, true/*param - doNotReplaceAir*/, rand, WALL_SELECTOR);
            fillWithRandomizedBlocks(world, bb, 0, centerY, 0, ROOM_WIDTH, centerY, ROOM_WIDTH, true/*param - doNotReplaceAir*/, rand, LAMP_SELECTOR);
            fillWithRandomizedBlocks(world, bb, 0, centerY + 1, 0, ROOM_WIDTH, ROOM_HEIGHT - 1, ROOM_WIDTH, false/*param - doNotReplaceAir*/, rand, WALL_SELECTOR);
            fillWithRandomizedBlocks(world, bb, 0, ROOM_HEIGHT, 0, ROOM_WIDTH, ROOM_HEIGHT, ROOM_WIDTH, false/*param - doNotReplaceAir*/, rand, CEILING_SELECTOR);

            setBlockState(world, PUZZLE_MASTER, ROOM_WIDTH / 2 + 1, MASTER_BLOCK_OFFSET, ROOM_WIDTH / 2 + 1, bb);

//            LootGames.LOGGER.info("PuzzleDungeon has spawned at {} {} {} in Dimension {}",
//                    (boundingBox.minX + boundingBox.maxX) / 2,
//                    (boundingBox.minY + boundingBox.maxY) / 2,
//                    (boundingBox.minZ + boundingBox.maxZ) / 2,
//                    world.getDimension().getType());

//            // Generate entrance
//            // Loop as long as the last "staircase" block is something different than air
//            // and is obstructed. This might lead to a very long staircase on mountains...
//            // So we limit the distance from the border to max 11 blocks
//            boolean zDirected = rand.nextBoolean();
//            BlockPos pos = zDirected ? new BlockPos(x + ROOM_WIDTH / 2 + 1, dungeonBottomY + 2, z + ROOM_WIDTH + 1) : new BlockPos(x + ROOM_WIDTH + 1, dungeonBottomY + 2, z + ROOM_WIDTH / 2 + 1);
//            int distance = 0;
//            while (world.getBlockState(pos).getBlock() != Blocks.AIR || !world.canBlockSeeSky(pos)) {
//                distance++;
//                if (distance > 15) break;
//
//                setBlockState(world, DUMMY, pos, bb);
//
//                pos = zDirected ? pos.south() : pos.east();
//
//                setBlockState(world, DUMMY, pos, bb);
//                pos = pos.up();
//            }

            return true;
        }

        public static class CorruptedBlockSelector extends StructurePiece.BlockSelector {
            private final BlockState common;
            private final BlockState corrupted;
            private final boolean wallOnly;

            public CorruptedBlockSelector(BlockState common, BlockState corrupted) {
                this(common, corrupted, false);
            }

            public CorruptedBlockSelector(BlockState common, BlockState corrupted, boolean wallOnly) {
                this.common = common;
                this.corrupted = corrupted;
                this.wallOnly = wallOnly;
            }

            @Override
            public void selectBlocks(Random rand, int x, int y, int z, boolean wall) {
                if (!wallOnly || wall) {
                    this.blockstate = RandHelper.chance(rand, 90, common, corrupted);
                }
            }
        }
    }
}
