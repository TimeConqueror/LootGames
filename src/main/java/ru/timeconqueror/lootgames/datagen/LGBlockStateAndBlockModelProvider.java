package ru.timeconqueror.lootgames.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.custom.BlockVariations;
import ru.timeconqueror.timecore.api.client.resource.BlockModels;
import ru.timeconqueror.timecore.api.client.resource.BlockStateResources;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.devtools.gen.BlockStateProvider;
import ru.timeconqueror.timecore.api.devtools.gen.ModelProvider;

import java.util.List;

import static ru.timeconqueror.lootgames.registry.LGBlocks.*;

public class LGBlockStateAndBlockModelProvider extends BlockStateProvider {
    public LGBlockStateAndBlockModelProvider(PackOutput output) {
        super(output, LootGames.MODID);
    }

    @Override
    protected void registerAll() {
        ModelProvider models = modelProvider();

        regBlockState(SPACE_FABRIC, BlockStateResources.singleVariantWithSingleModel(
                models.regBlockModel(SPACE_FABRIC, BlockModels.particlesOnly(new TextureLocation(LootGames.MODID, "block/empty"))))
        );

        BlockModelLocation glassMatterModel =
                models.regBlockModel(GLASSY_MATTER, BlockModels.particlesOnly(new TextureLocation(LootGames.MODID, "block/empty")));
        regBlockState(GLASSY_MATTER, BlockStateResources.singleVariantWithSingleModel(glassMatterModel));
        regBlockState(BOARD_GLASSY_MATTER, BlockStateResources.singleVariantWithSingleModel(glassMatterModel));

        BlockModelLocation microverseManipulator =
                models.regBlockModel(MICROVERSE_MANIPULATOR, BlockModels.particlesOnly(LootGames.tl("blockentity/microverse_manipulator_particles")));
        regBlockState(MICROVERSE_MANIPULATOR, BlockStateResources.singleVariantWithSingleModel(microverseManipulator));

        BlockModelLocation transferenceCrystal =
                models.regBlockModel(TRANSFERENCE_CRYSTAL, BlockModels.particlesOnly(LootGames.tl("blockentity/transference_crystal")));
        regBlockState(TRANSFERENCE_CRYSTAL, BlockStateResources.singleVariantWithSingleModel(transferenceCrystal));

        regBlockState(BOARD_OPAQUE_MATTER, BlockStateResources.singleVariantWithSingleModel(
                models.getDefaultModelLocation(BOARD_OPAQUE_MATTER)
        ));

        COMMON_XENOLITHS.forEach(this::addBlockVariationsStatesAndModels);

        List.of(GLOWING_CHISELED_XENOLITH,
                        GLOWING_FRAMED_XENOLITH,
                        GLOWING_WAVED_XENOLITH,
                        GLOWING_CHEVRON_XENOLITH,
                        GLOWING_FIBROUS_XENOLITH)
                .forEach(block -> {
                    regBlockState(block, BlockStateResources.singleVariantWithSingleModel(
                            models.regBlockModel(block, BlockModels.cubeAllModel(models.getDefaultTextureLocation(block)))
                    ));
                    models.regItemModelParentedByBlock(block);
                });
    }

    private void addBlockVariationsStatesAndModels(BlockVariations<?> variations) {
        ModelProvider modelProvider = modelProvider();
        Block block = variations.block();
        SlabBlock slab = variations.slab();
        StairBlock stairs = variations.stairs();

        BlockModelLocation blockModelLoc = modelProvider.regBlockModel(block, BlockModels.cubeAllModel(modelProvider.getDefaultTextureLocation(block)));
        regBlockState(block, BlockStateResources.singleVariantWithSingleModel(
                blockModelLoc
        ));

        BlockModelLocation slabModelBottomLoc = modelProvider.regBlockModel(getId(slab).getPath(), BlockModels.slabBottomModel(modelProvider.getDefaultTextureLocation(block)));
        regBlockState(slab, BlockStateResources.slab(
                slabModelBottomLoc,
                blockModelLoc,
                modelProvider.regBlockModel(getId(slab).getPath() + "_top", BlockModels.slabTopModel(modelProvider.getDefaultTextureLocation(block)))
        ));

        BlockModelLocation stairsModelLoc = modelProvider.regBlockModel(getId(stairs).getPath(), BlockModels.stairsModel(modelProvider.getDefaultTextureLocation(block)));
        regBlockState(stairs, BlockStateResources.stairs(
                stairsModelLoc,
                modelProvider.regBlockModel(getId(stairs).getPath() + "_inner", BlockModels.stairsInnerModel((modelProvider.getDefaultTextureLocation(block)))),
                modelProvider.regBlockModel(getId(stairs).getPath() + "_outer", BlockModels.stairsOuterModel(modelProvider.getDefaultTextureLocation(block)))
        ));

        modelProvider.regItemModelParentedByBlock(block);
        modelProvider.regItemModelParentedByBlock(slab, slabModelBottomLoc);
        modelProvider.regItemModelParentedByBlock(stairs, stairsModelLoc);
    }

    @Override
    public String getName() {
        return "LootGames' BlockState Provider";
    }
}
