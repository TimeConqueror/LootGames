package ru.timeconqueror.lootgames.datagen;

import net.minecraft.data.PackOutput;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.client.resource.BlockModels;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;

import static ru.timeconqueror.lootgames.registry.LGBlocks.GLASSY_MATTER;
import static ru.timeconqueror.lootgames.registry.LGBlocks.SPACE_FABRIC;

public class LGBlockStateAndBlockProvider extends BlockStateProvider {
    public LGBlockStateAndBlockProvider(PackOutput output) {
        super(output, LootGames.MODID);
    }

    @Override
    protected void registerAll() {
        ModelProvider models = blockModels();

        addBlockState(SPACE_FABRIC, BlockStateResources.singleVariantWithSingleModel(models
                .addBlockModel(SPACE_FABRIC, BlockModels.cubeAllModel(models.defaultTextureLocation(SPACE_FABRIC)))));
        addBlockState(GLASSY_MATTER, BlockStateResources.singleVariantWithSingleModel(models
                .addBlockModel(GLASSY_MATTER, BlockModels.particlesOnly(new TextureLocation(LootGames.MODID, "block/empty")))));
    }

    @Override
    public String getName() {
        return "LootGames' BlockState Provider";
    }
}
