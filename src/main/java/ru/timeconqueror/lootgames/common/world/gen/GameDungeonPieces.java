package ru.timeconqueror.lootgames.common.world.gen;

import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
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

        public Piece(TemplateManager templateManager, ResourceLocation templateLocation, BlockPos pos) {
            super(LGStructurePieces.GAME_DUNGEON_PIECE.get(), templateManager, templateLocation, pos);
        }

        public Piece(TemplateManager templateManager, CompoundNBT nbt) {
            super(LGStructurePieces.GAME_DUNGEON_PIECE.get(), templateManager, nbt);
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

        BlockPos.Mutable pathEnd = pathStart.mutable();

        int i = 0;
        while (i < LGConfigs.GENERAL.worldGen.maxEntryPathLength.get() && pathEnd.getY() < chunkGenerator.getFirstFreeHeight(pathEnd.getX(), pathEnd.getZ(), Heightmap.Type.WORLD_SURFACE_WG)) {
            pathEnd.move(direction, 1).move(Direction.UP, 1);
            i++;
        }

        return new EntryPath(direction, pathStart, pathEnd);
    }

    public static class EntryPath extends StructurePiece {
        public EntryPath(Direction direction, BlockPos start, BlockPos end) {
            super(LGStructurePieces.GAME_DUNGEON_ENTRY_PATH.get(), 0);

            setOrientation(direction);

            boundingBox = new MutableBoundingBox(start, end);
        }

        public EntryPath(TemplateManager templateManager, CompoundNBT nbt) {
            super(LGStructurePieces.GAME_DUNGEON_ENTRY_PATH.get(), nbt);
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT tagCompound) {
        }

        @Override
        public boolean postProcess(ISeedReader world, StructureManager structureManager_, ChunkGenerator chunkGenerator, Random random_, MutableBoundingBox chunkBox, ChunkPos chunkPos_, BlockPos blockPos_) {
            BlockPos.Mutable pos = new BlockPos.Mutable();
            BlockPos.Mutable absPos = new BlockPos.Mutable();

            updateAbsolute(absPos, pos);
            while (boundingBox.isInside(absPos)) {
                for (int i = 0; i < 3; i++) {
                    FluidState fluid = getFluidAround(world, absPos);
                    if (!fluid.isEmpty()) {
                        int freeHeight = chunkGenerator.getFirstFreeHeight(absPos.getX(), absPos.getZ(), Heightmap.Type.WORLD_SURFACE_WG);

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

        public FluidState getFluidAround(ISeedReader world, BlockPos pos) {
            FluidState fluidState = world.getFluidState(pos);
            if (!fluidState.isEmpty()) {
                return fluidState;
            }

            BlockPos.Mutable mutable = pos.mutable();
            for (Direction direction : DIRECTIONS) {
                mutable.move(direction);
                fluidState = world.getFluidState(mutable);
                if (!fluidState.isEmpty()) return fluidState;

                mutable.set(pos);
            }

            return Fluids.EMPTY.defaultFluidState();
        }

        private void updateAbsolute(BlockPos.Mutable absolute, BlockPos relative) {
            absolute.set(this.getWorldX(relative.getX(), relative.getZ()), this.getWorldY(relative.getY()), this.getWorldZ(relative.getX(), relative.getZ()));
        }
    }
}
