package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.world.IBlockReader;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;

public abstract class BlockGameMaster extends BlockGame {

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @NotNull
    public abstract TileEntityGameMaster<?> createTileEntity(BlockState state, IBlockReader world);
}
