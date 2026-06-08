package ru.timeconqueror.lootgames.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGItems;
import ru.timeconqueror.timecore.api.client.resource.ItemModel;
import ru.timeconqueror.timecore.api.client.resource.location.ItemModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.devtools.gen.ModelProvider;

public class LGItemModelProvider extends ModelProvider {
    private final static ItemModelLocation BASE_MICROVERSE_MODEL = LootGames.iml("boxed_microverse");

    public LGItemModelProvider(PackOutput output) {
        super(output, LootGames.MODID);
    }

    @Override
    protected void registerAll() {
        regMicroverseModel(LGItems.BOXED_UNKNOWN_MICROVERSE);
        regMicroverseModel(LGItems.BOXED_GAME_OF_LIGHT_MICROVERSE);
        regMicroverseModel(LGItems.BOXED_MINESWEEPER_MICROVERSE);

        regItemModelParentedByBlock(LGBlocks.BOARD_OPAQUE_MATTER);
    }

    protected ItemModel regMicroverseModel(Item item) {
        ResourceLocation id = getId(item);
        ItemModel model = ItemModel.parentedBy(BASE_MICROVERSE_MODEL).addTexture("texture", new TextureLocation(id.getNamespace(), "item/" + id.getPath()));
        regItemModel(item, model);
        return model;
    }

    @Override
    public String getName() {
        return "LGItemModelProvider";
    }
}
