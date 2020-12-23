package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.util.math.BlockPos;

public abstract class FloorLootGame<T extends FloorLootGame<T>> extends LootGame<T> {
    @Override
    public BlockPos getGameCenter() {
        return masterTileEntity.getBlockPos().offset(getBoardSize() / 2, 0, getBoardSize() / 2);
    }

    @Override
    protected BlockPos getRoomFloorPos() {
        return getMasterPos();
    }

    public abstract int getBoardSize();
}
