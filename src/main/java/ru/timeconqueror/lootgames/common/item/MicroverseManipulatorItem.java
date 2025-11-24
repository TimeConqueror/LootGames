package ru.timeconqueror.lootgames.common.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import ru.timeconqueror.lootgames.client.LGClient;

import java.util.function.Consumer;

public class MicroverseManipulatorItem extends BlockItem {
    public MicroverseManipulatorItem(Block block) {
        super(block, new Item.Properties()
                .fireResistant());
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
