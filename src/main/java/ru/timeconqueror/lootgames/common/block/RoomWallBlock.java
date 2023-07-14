package ru.timeconqueror.lootgames.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RoomWallBlock extends Block {
    public RoomWallBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void fallOn(Level world_, BlockState state_, BlockPos pos_, Entity entity_, float fallDistance_) {
        // make no damage
    }
}
