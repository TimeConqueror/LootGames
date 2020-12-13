package ru.timeconqueror.lootgames.common.world.gen;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.TemplateManager;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGStructurePieces;
import ru.timeconqueror.timecore.common.world.structure.TunedTemplateStructurePiece;
import ru.timeconqueror.timecore.common.world.structure.processor.RandomizeBlockProcessor;

import java.util.Random;

public class GameDungeonPieces {
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
            return new PlacementSettings()
                    .addProcessor(new RandomizeBlockProcessor(LGBlocks.DUNGEON_FLOOR, LGBlocks.CRACKED_DUNGEON_FLOOR))
                    .addProcessor(new RandomizeBlockProcessor(LGBlocks.DUNGEON_CEILING, LGBlocks.CRACKED_DUNGEON_CEILING))
                    .addProcessor(new RandomizeBlockProcessor(LGBlocks.DUNGEON_WALL, LGBlocks.CRACKED_DUNGEON_WALL))
                    .addProcessor(new RandomizeBlockProcessor(LGBlocks.DUNGEON_LAMP, LGBlocks.BROKEN_DUNGEON_LAMP));
        }
    }

    public static EntryPath createEntryPath(Random rand, BlockPos roomCenter, ChunkGenerator chunkGenerator) {
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
        BlockPos pathStart = roomCenter.relative(direction, GameDungeonStructure.ROOM_WIDTH / 2 + 1);

        BlockPos pathEnd = pathStart;

        while (pathEnd.getY() < chunkGenerator.getFirstFreeHeight(pathEnd.getX(), pathEnd.getZ(), Heightmap.Type.WORLD_SURFACE_WG)) {
            pathEnd = pathEnd.relative(direction, 1).above(1);
        }

        return new EntryPath(direction, pathStart, pathEnd);
    }

    public static class EntryPath extends StructurePiece {
        public EntryPath(Direction direction, BlockPos start, BlockPos end) {
            super(LGStructurePieces.GAME_DUNGEON_ENTRY_PATH, 0);

            setOrientation(direction);

            boundingBox = new MutableBoundingBox(start, end);
        }

        public EntryPath(TemplateManager templateManager, CompoundNBT nbt) {
            super(LGStructurePieces.GAME_DUNGEON_ENTRY_PATH, nbt);
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT tagCompound) {
        }

        @Override
        public boolean postProcess(ISeedReader world, StructureManager structureManager_, ChunkGenerator chunkGenerator_, Random random_, MutableBoundingBox chunkBox, ChunkPos chunkPos_, BlockPos blockPos_) {
            BlockPos pos = new BlockPos(0, 0, 0);

            while (boundingBox.isInside(new BlockPos(this.getWorldX(pos.getX(), pos.getZ()), this.getWorldY(pos.getY()), this.getWorldZ(pos.getX(), pos.getZ())))) {
                for (int i = 0; i < 3; i++) {
                    placeBlock(world, Blocks.AIR.defaultBlockState(), pos.getX(), pos.getY() + i, pos.getZ(), chunkBox);
                }

                pos = pos.south(1).above(1); //south is the forward direction without rotations
            }

            return true;
        }
    }
}
