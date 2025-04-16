package ru.timeconqueror.lootgames.common.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import ru.timeconqueror.lootgames.client.LGClient;

import java.util.function.Consumer;

public class TransferenceCrystalItem extends BlockItem {
    public TransferenceCrystalItem(Block block_) {
        super(block_, new Item.Properties());
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return LGClient.BLOCK_ENTITY_WITHOUT_LEVEL_RENDERER.get();
            }
        });
    }
}
