package ru.timeconqueror.lootgames.datagen;

import net.minecraft.data.PackOutput;
import ru.timeconqueror.lootgames.LootGames;
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


        addBlockState(OPAQUE_MATTER, BlockStateResources.singleVariantWithSingleModel(
                models.addBlockModel(OPAQUE_MATTER, BlockModels.cubeAllModel(models.getDefaultTextureLocation(OPAQUE_MATTER)))
        ));
        addBlockState(BOARD_OPAQUE_MATTER, BlockStateResources.singleVariantWithSingleModel(
                models.getDefaultModelLocation(BOARD_OPAQUE_MATTER)
        ));
    }

    @Override
    public String getName() {
        return "LootGames' BlockState Provider";
    }
}
