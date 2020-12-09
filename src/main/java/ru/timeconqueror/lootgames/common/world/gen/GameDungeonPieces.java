package ru.timeconqueror.lootgames.common.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.TemplateManager;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGStructurePieces;
import ru.timeconqueror.timecore.mod.common.world.structure.TunedTemplateStructurePiece;
import ru.timeconqueror.timecore.util.RandHelper;

import java.util.Random;

public class GameDungeonPieces {
    private static final BlockState PUZZLE_MASTER = LGBlocks.PUZZLE_MASTER.defaultBlockState();
    private static final BlockState DUNGEON_CEILING = LGBlocks.DUNGEON_CEILING.defaultBlockState();
    private static final BlockState DUNGEON_CEILING_CRACKED = LGBlocks.CRACKED_DUNGEON_CEILING.defaultBlockState();
    private static final BlockState DUNGEON_WALL = LGBlocks.DUNGEON_WALL.defaultBlockState();
    private static final BlockState CRACKED_DUNGEON_WALL = LGBlocks.CRACKED_DUNGEON_WALL.defaultBlockState();
    private static final BlockState DUNGEON_FLOOR = LGBlocks.DUNGEON_FLOOR.defaultBlockState();
    private static final BlockState CRACKED_DUNGEON_FLOOR = LGBlocks.CRACKED_DUNGEON_FLOOR.defaultBlockState();
    private static final BlockState SHIELDED_DUNGEON_FLOOR = LGBlocks.SHIELDED_DUNGEON_FLOOR.defaultBlockState();
    private static final BlockState DUNGEON_LAMP = LGBlocks.DUNGEON_LAMP.defaultBlockState();
    private static final BlockState BROKEN_DUNGEON_LAMP = LGBlocks.BROKEN_DUNGEON_LAMP.defaultBlockState();

    private static final CorruptedBlockSelector FLOOR_SELECTOR = new CorruptedBlockSelector(DUNGEON_FLOOR, CRACKED_DUNGEON_FLOOR);
    private static final CorruptedBlockSelector CEILING_SELECTOR = new CorruptedBlockSelector(DUNGEON_CEILING, DUNGEON_CEILING_CRACKED);
    private static final CorruptedBlockSelector WALL_SELECTOR = new CorruptedBlockSelector(DUNGEON_WALL, CRACKED_DUNGEON_WALL, true);
    private static final CorruptedBlockSelector LAMP_SELECTOR = new CorruptedBlockSelector(DUNGEON_LAMP, BROKEN_DUNGEON_LAMP, true);

    public static final ResourceLocation ROOM = LootGames.rl("room");

    public static class Piece extends TunedTemplateStructurePiece {

        public Piece(TemplateManager templateManager, ResourceLocation templateLocation, BlockPos pos) {
            super(LGStructurePieces.GAME_DUNGEON_PIECE, templateManager, templateLocation, pos);
        }

        public Piece(TemplateManager templateManager, CompoundNBT nbt) {
            super(LGStructurePieces.GAME_DUNGEON_PIECE, templateManager, nbt);
        }

        @Override
        protected PlacementSettings makePlacementSettings() {
            return new PlacementSettings();
        }
    }

    //    public static class MainRoom extends ScatteredStructurePiece {
//        /**
//         * This is the constructor we use ourselves, the other constructor is needed for the piece type
//         */
//        protected MainRoom(BlockPos pos, Random rand) {
//            super(LGStructures.GD_PIECE_MAIN_ROOM, rand, pos.getX(), pos.getY(), pos.getZ(), ROOM_WIDTH, ROOM_HEIGHT, ROOM_WIDTH);
//        }
//
//        /**
//         * This constructor must always be implemented, so that the piece type can be created
//         */
//        public MainRoom(TemplateManager manager, CompoundNBT nbt) {
//            super(LGStructures.GD_PIECE_MAIN_ROOM, nbt);
//        }
//
//        @Override
//        protected void readAdditional(CompoundNBT tagCompound) {
//
//        }
//
//        @Override
//        public boolean addComponentParts(IWorld world, Random rand, MutableBoundingBox bb, ChunkPos chunkPosIn) {
//
//            int centerY = ROOM_HEIGHT / 2;
//
//            fillWithRandomizedBlocks(world, bb, 0, 0, 0, ROOM_WIDTH, 0, ROOM_WIDTH, false/*doNotReplaceAir*/, rand, FLOOR_SELECTOR);
//            fillWithBlocks(world, bb, 1, 1, 1, ROOM_WIDTH - 1, 1, ROOM_WIDTH - 1, SHIELDED_DUNGEON_FLOOR, SHIELDED_DUNGEON_FLOOR, false /*doNotReplaceAir*/);
//            fillWithRandomizedBlocks(world, bb, 0, 1, 0, ROOM_WIDTH, centerY - 1, ROOM_WIDTH, false/*doNotReplaceAir*/, rand, WALL_SELECTOR);
//            fillWithRandomizedBlocks(world, bb, 0, centerY, 0, ROOM_WIDTH, centerY, ROOM_WIDTH, false/*doNotReplaceAir*/, rand, LAMP_SELECTOR);
//            fillWithRandomizedBlocks(world, bb, 0, centerY + 1, 0, ROOM_WIDTH, ROOM_HEIGHT - 1, ROOM_WIDTH, false/*doNotReplaceAir*/, rand, WALL_SELECTOR);
//            fillWithRandomizedBlocks(world, bb, 0, ROOM_HEIGHT, 0, ROOM_WIDTH, ROOM_HEIGHT, ROOM_WIDTH, false/*doNotReplaceAir*/, rand, CEILING_SELECTOR);
//
//            setBlockState(world, PUZZLE_MASTER, ROOM_WIDTH / 2 + 1, MASTER_BLOCK_OFFSET, ROOM_WIDTH / 2 + 1, bb);
//
////            LootGames.LOGGER.info("PuzzleDungeon has spawned at {} {} {} in Dimension {}",
////                    (boundingBox.minX + boundingBox.maxX) / 2,
////                    (boundingBox.minY + boundingBox.maxY) / 2,
////                    (boundingBox.minZ + boundingBox.maxZ) / 2,
////                    world.getDimension().getType());
//
////            // Generate entrance
////            // Loop as long as the last "staircase" block is something different than air
////            // and is obstructed. This might lead to a very long staircase on mountains...
////            // So we limit the distance from the border to max 11 blocks
////            boolean zDirected = rand.nextBoolean();
////            BlockPos pos = zDirected ? new BlockPos(x + ROOM_WIDTH / 2 + 1, dungeonBottomY + 2, z + ROOM_WIDTH + 1) : new BlockPos(x + ROOM_WIDTH + 1, dungeonBottomY + 2, z + ROOM_WIDTH / 2 + 1);
////            int distance = 0;
////            while (world.getBlockState(pos).getBlock() != Blocks.AIR || !world.canBlockSeeSky(pos)) {
////                distance++;
////                if (distance > 15) break;
////
////                setBlockState(world, DUMMY, pos, bb);
////
////                pos = zDirected ? pos.south() : pos.east();
////
////                setBlockState(world, DUMMY, pos, bb);
////                pos = pos.up();
////            }
//
//            return true;
//        }
//
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
        public void next(Random rand, int x, int y, int z, boolean wall) {
            if (!wallOnly || wall) {
                this.next = RandHelper.chance(rand, 90, common, corrupted);
            }
        }
    }
}
