package ru.timeconqueror.lootgames.api.block.tile;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.utils.MouseClickType;

public class TileBoardGameMaster<T extends BoardLootGame<T>> extends TileEntityGameMaster<T> {
    public TileBoardGameMaster(TileEntityType<? extends TileEntityGameMaster<T>> type, T game) {
        super(type, game);
    }

    @Override
    public void onBlockLeftClick(ServerPlayerEntity player, BlockPos subordinatePos) {
        super.onBlockLeftClick(player, subordinatePos);
        onClick(player, subordinatePos, MouseClickType.LEFT);
    }

    @Override
    public void onBlockRightClick(ServerPlayerEntity player, BlockPos subordinatePos) {
        super.onBlockRightClick(player, subordinatePos);
        onClick(player, subordinatePos, MouseClickType.RIGHT);
    }

    private void onClick(ServerPlayerEntity player, BlockPos subordinatePos, MouseClickType type) {
        Pos2i pos = game.convertToGamePos(subordinatePos);

        int size = game.getCurrentBoardSize();
        if (pos.getX() >= 0 && pos.getX() < size
                && pos.getY() >= 0 && pos.getY() < size) {
            game.onClick(player, pos, type);
        }
    }
}
