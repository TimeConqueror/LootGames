package ru.timeconqueror.lootgames.datagen;

import net.minecraft.data.PackOutput;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.client.resource.BlockModels;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;

import static ru.timeconqueror.lootgames.registry.LGBlocks.*;

public class LGBlockStateAndBlockProvider extends BlockStateProvider {
    public LGBlockStateAndBlockProvider(PackOutput output) {
        super(output, LootGames.MODID);
    }

    @Override
    protected void registerAll() {
        ModelProvider models = blockModels();

        addBlockState(SPACE_FABRIC, BlockStateResources.singleVariantWithSingleModel(models
                .addBlockModel(SPACE_FABRIC, BlockModels.cubeAllModel(models.defaultTextureLocation(SPACE_FABRIC)))));

        BlockModelLocation glassMatterModel = models
                .addBlockModel(GLASSY_MATTER, BlockModels.particlesOnly(new TextureLocation(LootGames.MODID, "block/empty")));
        addBlockState(GLASSY_MATTER, BlockStateResources.singleVariantWithSingleModel(glassMatterModel));
        addBlockState(MINESWEEPER_GLASSY_MATTER, BlockStateResources.singleVariantWithSingleModel(glassMatterModel));
    }

    @Override
    public String getName() {
        return "LootGames' BlockState Provider";
    }
}
