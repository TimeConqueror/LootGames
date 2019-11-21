package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class BlockGame extends Block {
    public BlockGame() {
        super(Material.BARRIER);
        setBlockUnbreakable();
        setLightLevel(1.0F);
        setResistance(6000000.0F);
    }
}
