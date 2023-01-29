package ru.timeconqueror.lootgames.common.world.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGStructurePieces;
import ru.timeconqueror.timecore.api.common.world.structure.TunedTemplateStructurePiece;
import ru.timeconqueror.timecore.api.common.world.structure.processor.RandomizeBlockProcessor;

import java.util.Random;

public class GameDungeonPieces {
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final ResourceLocation ROOM = LootGames.rl("room");

    public static class Piece extends TunedTemplateStructurePiece {

        public Piece(StructureManager templateManager, ResourceLocation templateLocation, BlockPos pos) {
            super(LGStructurePieces.GAME_DUNGEON_PIECE.get(), templateManager, templateLocation, pos);
        }

        public Piece(StructureManager templateManager, CompoundTag nbt) {
            super(LGStructurePieces.GAME_DUNGEON_PIECE.get(), templateManager, nbt);
        }

        @Override
        protected StructurePlaceSettings makePlacementSettings() {
            return new StructurePlaceSettings()
                    .addProcessor(new RandomizeBlockProcessor(LGBlocks.DUNGEON_FLOOR, LGBlocks.CRACKED_DUNGEON_FLOOR))
                    .addProcessor(new RandomizeBlockProcessor(LGBlocks.DUNGEON_CEILING, LGBlocks.CRACKED_DUNGEON_CEILING))
                    .addProcessor(new RandomizeBlockProcessor(LGBlocks.DUNGEON_WALL, LGBlocks.CRACKED_DUNGEON_WALL))
                    .addProcessor(new RandomizeBlockProcessor(LGBlocks.DUNGEON_LAMP, LGBlocks.BROKEN_DUNGEON_LAMP));
        }
    }

    public static EntryPath createEntryPath(Random rand, BlockPos roomCenter, ChunkGenerator chunkGenerator) {
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
        BlockPos pathStart = roomCenter.relative(direction, GameDungeonStructure.ROOM_WIDTH / 2 + 1);

        BlockPos.MutableBlockPos pathEnd = pathStart.mutable();

        int i = 0;
        while (i < LGConfigs.GENERAL.worldGen.maxEntryPathLength.get() && pathEnd.getY() < chunkGenerator.getFirstFreeHeight(pathEnd.getX(), pathEnd.getZ(), Heightmap.Types.WORLD_SURFACE_WG)) {
            pathEnd.move(direction, 1).move(Direction.UP, 1);
            i++;
        }

        return new EntryPath(direction, pathStart, pathEnd);
    }

    public static class EntryPath extends StructurePiece {
        public EntryPath(Direction direction, BlockPos start, BlockPos end) {
            super(LGStructurePieces.GAME_DUNGEON_ENTRY_PATH.get(), 0);

            setOrientation(direction);

            boundingBox = new BoundingBox(start, end);
        }

        public EntryPath(StructureManager templateManager, CompoundTag nbt) {
            super(LGStructurePieces.GAME_DUNGEON_ENTRY_PATH.get(), nbt);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tagCompound) {
        }

        @Override
        public boolean postProcess(WorldGenLevel world, StructureFeatureManager structureManager_, ChunkGenerator chunkGenerator, Random random_, BoundingBox chunkBox, ChunkPos chunkPos_, BlockPos blockPos_) {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos absPos = new BlockPos.MutableBlockPos();

            updateAbsolute(absPos, pos);
            while (boundingBox.isInside(absPos)) {
                for (int i = 0; i < 3; i++) {
                    FluidState fluid = getFluidAround(world, absPos);
                    if (!fluid.isEmpty()) {
                        int freeHeight = chunkGenerator.getFirstFreeHeight(absPos.getX(), absPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG);

                        if (absPos.getY() + i < freeHeight) {
                            placeBlock(world, fluid.createLegacyBlock(), pos.getX(), pos.getY() + i, pos.getZ(), chunkBox);
                        }
                    } else {
                        placeBlock(world, Blocks.AIR.defaultBlockState(), pos.getX(), pos.getY() + i, pos.getZ(), chunkBox);
                    }
                }

                pos.move(Direction.SOUTH, 1).move(Direction.UP, 1);//south is the forward direction without rotations
                updateAbsolute(absPos, pos);
            }

            return true;
        }

        public FluidState getFluidAround(WorldGenLevel world, BlockPos pos) {
            FluidState fluidState = world.getFluidState(pos);
            if (!fluidState.isEmpty()) {
                return fluidState;
            }

            BlockPos.MutableBlockPos mutable = pos.mutable();
            for (Direction direction : DIRECTIONS) {
                mutable.move(direction);
                fluidState = world.getFluidState(mutable);
                if (!fluidState.isEmpty()) return fluidState;

                mutable.set(pos);
            }

            return Fluids.EMPTY.defaultFluidState();
        }

        private void updateAbsolute(BlockPos.MutableBlockPos absolute, BlockPos relative) {
            absolute.set(this.getWorldX(relative.getX(), relative.getZ()), this.getWorldY(relative.getY()), this.getWorldZ(relative.getX(), relative.getZ()));
        }
    }
}
