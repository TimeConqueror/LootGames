package ru.timeconqueror.lootgames.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class RoomWallBlock extends Block {
    public RoomWallBlock() {
        super(Properties.copy(Blocks.BARRIER));
    }
}
