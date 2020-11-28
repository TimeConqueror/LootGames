package ru.timeconqueror.lootgames.api.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ILeftInteractible {
    /**
     * Should return true, if you need to prevent left click.
     */
    boolean onLeftClick(World world, PlayerEntity player, BlockPos pos, Direction face);
}
