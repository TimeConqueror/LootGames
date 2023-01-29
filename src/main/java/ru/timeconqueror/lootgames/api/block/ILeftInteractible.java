package ru.timeconqueror.lootgames.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface ILeftInteractible {
    /**
     * Should return true, if you need to prevent left click.
     */
    boolean onLeftClick(Level world, Player player, BlockPos pos, Direction face);
}
