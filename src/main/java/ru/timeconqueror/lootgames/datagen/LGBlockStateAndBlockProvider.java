package ru.timeconqueror.lootgames.datagen;

import net.minecraft.data.PackOutput;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.client.resource.BlockModels;

import static ru.timeconqueror.lootgames.registry.LGBlocks.OUTER_SPACE;

public class LGBlockStateAndBlockProvider extends BlockStateProvider {
    public LGBlockStateAndBlockProvider(PackOutput output) {
        super(output, LootGames.MODID);
    }

    @Override
    protected void registerAll() {
        ModelProvider models = blockModels();

        var outerSpaceModel = models.addBlockModel(OUTER_SPACE, BlockModels.cubeAllModel(models.defaultTextureLocation(OUTER_SPACE)));
        addBlockState(OUTER_SPACE, BlockStateResources.singleVariantWithSingleModel(outerSpaceModel));
    }

    @Override
    public String getName() {
        return "LootGames' BlockState Provider";
    }
}
