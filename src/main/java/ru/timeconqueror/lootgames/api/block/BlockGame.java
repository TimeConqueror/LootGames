package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import ru.timeconqueror.timecore.registry.BlockPropsFactory;

public abstract class BlockGame extends Block {
    private static final BlockPropsFactory DEF_PROPS = new BlockPropsFactory(() ->
            BlockPropsFactory.unbreakable(Properties.of(Material.BARRIER)
                    .lightLevel(value -> 1))
    );

    public BlockGame() {
        super(DEF_PROPS.create());
    }
}
