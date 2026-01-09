package ru.timeconqueror.lootgames.datagen;

import net.minecraft.data.PackOutput;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.client.resource.BlockModel;
import ru.timeconqueror.timecore.api.client.resource.BlockModels;
import ru.timeconqueror.timecore.api.client.resource.BlockStateResources;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.devtools.gen.BlockStateProvider;
import ru.timeconqueror.timecore.api.devtools.gen.ModelProvider;

import static ru.timeconqueror.lootgames.registry.LGBlocks.*;

public class LGBlockStateAndBlockModelProvider extends BlockStateProvider {
    public LGBlockStateAndBlockModelProvider(PackOutput output) {
        super(output, LootGames.MODID);
    }

    @Override
    protected void registerAll() {
        ModelProvider models = blockModels();

        addBlockState(SPACE_FABRIC, BlockStateResources.singleVariantWithSingleModel(
                models.addBlockModel(SPACE_FABRIC, BlockModels.particlesOnly(new TextureLocation(LootGames.MODID, "block/empty"))))
        );

        BlockModelLocation glassMatterModel =
                models.addBlockModel(GLASSY_MATTER, BlockModels.particlesOnly(new TextureLocation(LootGames.MODID, "block/empty")));
        addBlockState(GLASSY_MATTER, BlockStateResources.singleVariantWithSingleModel(glassMatterModel));
        addBlockState(BOARD_GLASSY_MATTER, BlockStateResources.singleVariantWithSingleModel(glassMatterModel));

        BlockModelLocation microverseManipulator =
                models.addBlockModel(MICROVERSE_MANIPULATOR, BlockModels.particlesOnly(LootGames.tl("blockentity/microverse_manipulator_particles")));
        addBlockState(MICROVERSE_MANIPULATOR, BlockStateResources.singleVariantWithSingleModel(microverseManipulator));

        BlockModelLocation transferenceCrystal =
                models.addBlockModel(TRANSFERENCE_CRYSTAL, BlockModels.particlesOnly(LootGames.tl("blockentity/transference_crystal")));
        addBlockState(TRANSFERENCE_CRYSTAL, BlockStateResources.singleVariantWithSingleModel(transferenceCrystal));

        addBlockState(DUNGEON_BLOCK, BlockStateResources.singleVariantWithSingleModel(
                models.addBlockModel(DUNGEON_BLOCK, BlockModels.cubeAllModel(models.getDefaultTextureLocation(DUNGEON_BLOCK)))
        ));
        addBlockState(BOARD_OPAQUE_MATTER, BlockStateResources.singleVariantWithSingleModel(
                models.getDefaultModelLocation(BOARD_OPAQUE_MATTER)
        ));
        addBlockState(DUNGEON_LAMP, BlockStateResources.singleVariantWithSingleModel(
                models.addBlockModel(DUNGEON_LAMP, BlockModels.cubeAllModel(models.getDefaultTextureLocation(DUNGEON_LAMP)))
        ));
        TextureLocation matterBlockTl = models.getDefaultTextureLocation(MATTER_BLOCK);
        addBlockState(MATTER_BLOCK, BlockStateResources.singleVariantWithSingleModel(
                models.addBlockModel(MATTER_BLOCK, BlockModels.cubeAllModel(matterBlockTl))
        ));
        addBlockState(MATTER_BLOCK_STAIRS, BlockStateResources.stairs(
                models.addBlockModel("matter_block_stairs", BlockModels.stairsModel(matterBlockTl)),
                models.addBlockModel("matter_block_stairs_inner", BlockModels.stairsInnerModel(matterBlockTl)),
                models.addBlockModel("matter_block_stairs_outer", BlockModels.stairsOuterModel(matterBlockTl))
        ));
        addBlockState(MATTER_BLOCK_SLAB, BlockStateResources.slab(
                models.addBlockModel("matter_block_slab", BlockModels.slabBottomModel(matterBlockTl)),
                models.getDefaultModelLocation(MATTER_BLOCK),
                models.addBlockModel("matter_block_slab_top", BlockModels.slabTopModel(matterBlockTl))
        ));
        addBlockState(MATTER_WALL, BlockStateResources.singleVariantWithSingleModel(
                models.addBlockModel(MATTER_WALL, BlockModels.cubeAllModel(models.getDefaultTextureLocation(MATTER_WALL)))
        ));
        addBlockState(MATTER_GLOWING_BLOCK, BlockStateResources.singleVariantWithSingleModel(
                models.addBlockModel(MATTER_GLOWING_BLOCK, BlockModels.cubeColumnModel(LootGames.tl("block/matter_glowing_block_top_bottom"), LootGames.tl("block/matter_glowing_block_side")))
        ));
    }

    @Override
    public String getName() {
        return "LootGames' BlockState Provider";
    }
}
