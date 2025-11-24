package ru.timeconqueror.lootgames.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGItems;
import ru.timeconqueror.timecore.api.client.resource.ItemModel;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.ItemModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.devtools.gen.ModelProvider;

public class LGItemModelProvider extends ModelProvider {
    private final static ItemModelLocation BASE_MICROVERSE_MODEL = LootGames.iml("boxed_microverse");

    public LGItemModelProvider(PackOutput output) {
        super(output, LootGames.MODID, ITEM_FOLDER);
    }

    @Override
    protected void registerAll() {
        addMicroverseModel(LGItems.BOXED_UNKNOWN_MICROVERSE);
        addMicroverseModel(LGItems.BOXED_GAME_OF_LIGHT_MICROVERSE);
        addMicroverseModel(LGItems.BOXED_MINESWEEPER_MICROVERSE);

        addItemModelParentedByBlock(LGBlocks.BOARD_OPAQUE_MATTER);
        addItemModelParentedByBlock(LGBlocks.DUNGEON_LAMP);
    }

    protected ItemModel addMicroverseModel(Item item) {
        ResourceLocation id = getId(item);
        ItemModel model = ItemModel.parentedBy(BASE_MICROVERSE_MODEL).addTexture("texture", new TextureLocation(id.getNamespace(), "item/" + id.getPath()));
        addItemModel(item, model);
        return model;
    }

    @Override
    public String getName() {
        return "LGItemModelProvider";
    }
}
