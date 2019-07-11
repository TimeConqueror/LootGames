package ru.timeconqueror.lootgames.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockPuzzleMaster extends Block {
    public BlockPuzzleMaster() {
        super(Material.IRON);
        setBlockUnbreakable();
        setLightLevel(1.0F);
    }
}
