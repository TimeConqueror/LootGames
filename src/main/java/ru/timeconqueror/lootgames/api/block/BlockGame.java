package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import ru.timeconqueror.timecore.api.registry.block.BlockPropsFactory;

public abstract class BlockGame extends Block {
    private static final BlockPropsFactory DEF_PROPS = new BlockPropsFactory(() ->
            BlockPropsFactory.setUnbreakableAndInexplosive(Properties.create(Material.BARRIER)).lightValue(1));

    public BlockGame() {
        super(DEF_PROPS.create());
    }
}
