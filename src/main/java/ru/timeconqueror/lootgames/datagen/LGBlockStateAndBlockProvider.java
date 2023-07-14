package ru.timeconqueror.lootgames.datagen;

import net.minecraft.data.PackOutput;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.client.resource.BlockModels;

import static ru.timeconqueror.lootgames.registry.LGBlocks.SPACE_FABRIC;

public class LGBlockStateAndBlockProvider extends BlockStateProvider {
    public LGBlockStateAndBlockProvider(PackOutput output) {
        super(output, LootGames.MODID);
    }

    @Override
    protected void registerAll() {
        ModelProvider models = blockModels();

        var outerSpaceModel = models.addBlockModel(SPACE_FABRIC, BlockModels.cubeAllModel(models.defaultTextureLocation(SPACE_FABRIC)));
        addBlockState(SPACE_FABRIC, BlockStateResources.singleVariantWithSingleModel(outerSpaceModel));
    }

    @Override
    public String getName() {
        return "LootGames' BlockState Provider";
    }
}
